package br.com.achimid.animesachimidv2.gateways.outputs.http.webpush

import br.com.achimid.animesachimidv2.domains.PushSubscription
import br.com.achimid.animesachimidv2.gateways.outputs.mongodb.PushSubscriptionGateway
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.math.BigInteger
import java.net.URI
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.*
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.*
import java.time.Instant
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Envia mensagens Web Push via protocolo VAPID (RFC 8291 + RFC 8188 aes128gcm).
 * Implementação sem dependências externas usando java.security / javax.crypto.
 */
@Component
class WebPushGateway(
    private val pushSubscriptionGateway: PushSubscriptionGateway,
    @Value("\${webpush.vapid-public-key}") private val vapidPublicKeyBase64: String,
    @Value("\${webpush.vapid-private-key}") private val vapidPrivateKeyBase64: String,
    @Value("\${webpush.subject}") private val subject: String,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val restTemplate = RestTemplate()
    private val b64 = Base64.getUrlEncoder().withoutPadding()
    private val b64Dec = Base64.getUrlDecoder()

    private val vapidPrivateKey: ECPrivateKey by lazy { loadPrivateKey(vapidPrivateKeyBase64) }
    private val ecParamSpec: ECParameterSpec by lazy {
        val kg = KeyPairGenerator.getInstance("EC")
        kg.initialize(ECGenParameterSpec("secp256r1"))
        (kg.generateKeyPair().public as ECPublicKey).params
    }

    fun send(subscription: PushSubscription, payload: String) {
        try {
            val encrypted = encrypt(payload, subscription.p256dh, subscription.auth)
            val jwt = createVapidJwt(subscription.endpoint)
            val headers = HttpHeaders().apply {
                set("Authorization", "vapid t=$jwt,k=$vapidPublicKeyBase64")
                set("TTL", "86400")
                set("Urgency", "normal")
                contentType = MediaType("application", "octet-stream")
                set("Content-Encoding", "aes128gcm")
            }
            restTemplate.postForEntity(subscription.endpoint, HttpEntity(encrypted, headers), Void::class.java)
        } catch (e: HttpClientErrorException) {
            if (e.statusCode == HttpStatus.GONE || e.statusCode == HttpStatus.NOT_FOUND) {
                logger.info("Subscription expirada, removendo: ${subscription.endpoint.take(60)}...")
                pushSubscriptionGateway.deleteByEndpoint(subscription.endpoint)
            } else {
                logger.warn("WebPush HTTP ${e.statusCode} para userId=${subscription.userId}: ${e.message}")
            }
        } catch (e: Exception) {
            logger.warn("WebPush falhou para userId=${subscription.userId}: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    // ── VAPID JWT (RFC 8292) ──────────────────────────────────────────────────

    private fun createVapidJwt(endpoint: String): String {
        val uri = URI(endpoint)
        val audience = "${uri.scheme}://${uri.host}"
        val exp = Instant.now().epochSecond + 12 * 3600

        val header = b64.encodeToString("""{"typ":"JWT","alg":"ES256"}""".toByteArray(Charsets.UTF_8))
        val claims = b64.encodeToString(
            """{"aud":"$audience","exp":$exp,"sub":"$subject"}""".toByteArray(Charsets.UTF_8)
        )
        val sigInput = "$header.$claims"

        val sig = Signature.getInstance("SHA256withECDSA")
        sig.initSign(vapidPrivateKey)
        sig.update(sigInput.toByteArray(Charsets.US_ASCII))
        return "$sigInput.${b64.encodeToString(derToRawECDSA(sig.sign()))}"
    }

    // Convert DER-encoded ECDSA signature to raw R||S (32 + 32 bytes)
    private fun derToRawECDSA(der: ByteArray): ByteArray {
        var i = 2 // skip SEQUENCE tag + length
        i++ // skip INTEGER tag
        val rLen = der[i++].toInt() and 0xFF
        val r = der.copyOfRange(i, i + rLen); i += rLen
        i++ // skip INTEGER tag
        val sLen = der[i++].toInt() and 0xFF
        val s = der.copyOfRange(i, i + sLen)
        return normalise32(r) + normalise32(s)
    }

    private fun normalise32(b: ByteArray): ByteArray {
        val stripped = b.dropWhile { it == 0.toByte() }.toByteArray()
        return when {
            stripped.size == 32 -> stripped
            stripped.size > 32 -> stripped.takeLast(32).toByteArray()
            else -> ByteArray(32 - stripped.size) + stripped
        }
    }

    // ── Content encryption (RFC 8291 + RFC 8188 aes128gcm) ───────────────────

    private fun encrypt(payload: String, p256dhBase64: String, authBase64: String): ByteArray {
        val payloadBytes = payload.toByteArray(Charsets.UTF_8)
        val uaPublicKey = loadPublicKey(p256dhBase64)
        val authSecret = b64Dec.decode(pad(authBase64))

        // Ephemeral server key pair
        val kg = KeyPairGenerator.getInstance("EC")
        kg.initialize(ECGenParameterSpec("secp256r1"))
        val ephemeral = kg.generateKeyPair()
        val asPublicBytes = publicKeyBytes(ephemeral.public as ECPublicKey)
        val uaPublicBytes = publicKeyBytes(uaPublicKey)

        // ECDH
        val ka = KeyAgreement.getInstance("ECDH")
        ka.init(ephemeral.private)
        ka.doPhase(uaPublicKey, true)
        val ecdhSecret = ka.generateSecret()

        // RFC 8291 key derivation:
        // auth_info = "WebPush: info" || 0x00 || ua_pub (65) || as_pub (65)
        val authInfo = byteArrayOf(*"WebPush: info".toByteArray(), 0x00) + uaPublicBytes + asPublicBytes
        val prk = hmacSHA256(authSecret, ecdhSecret)
        val ikm = hmacExpand1(prk, authInfo, 32)

        // RFC 8188 aes128gcm key derivation:
        // cek_info  = "Content-Encoding: aes128gcm" || 0x00
        // nonce_info= "Content-Encoding: nonce"    || 0x00
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val prk2 = hmacSHA256(salt, ikm)
        val cek = hmacExpand1(prk2, byteArrayOf(*"Content-Encoding: aes128gcm".toByteArray(), 0x00), 16)
        val nonce = hmacExpand1(prk2, byteArrayOf(*"Content-Encoding: nonce".toByteArray(), 0x00), 12)

        // AES-128-GCM encrypt — append 0x02 as RFC 8188 "last record" delimiter
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(cek, "AES"), GCMParameterSpec(128, nonce))
        val ciphertext = cipher.doFinal(payloadBytes + byteArrayOf(0x02))

        // RFC 8188 header: salt(16) + rs(uint32 BE, 4096) + keyid_len(1 = 65) + keyid(asPublicBytes)
        val header = salt +
            ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(4096).array() +
            byteArrayOf(65.toByte()) +
            asPublicBytes

        return header + ciphertext
    }

    // HKDF-Extract (RFC 5869): PRK = HMAC-SHA-256(salt, IKM)
    private fun hmacSHA256(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data)
    }

    // HKDF-Expand T(1) = HMAC(PRK, "" || info || 0x01), trimmed to `length`
    private fun hmacExpand1(prk: ByteArray, info: ByteArray, length: Int): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(prk, "HmacSHA256"))
        mac.update(info)
        mac.update(0x01.toByte())
        return mac.doFinal().copyOf(length)
    }

    // ── Key helpers ───────────────────────────────────────────────────────────

    private fun loadPrivateKey(base64url: String): ECPrivateKey {
        val bytes = b64Dec.decode(pad(base64url))
        return KeyFactory.getInstance("EC")
            .generatePrivate(ECPrivateKeySpec(BigInteger(1, bytes), ecParamSpec)) as ECPrivateKey
    }

    private fun loadPublicKey(base64url: String): ECPublicKey {
        val bytes = b64Dec.decode(pad(base64url))
        require(bytes[0] == 0x04.toByte()) { "Expected uncompressed EC point" }
        val x = BigInteger(1, bytes.copyOfRange(1, 33))
        val y = BigInteger(1, bytes.copyOfRange(33, 65))
        return KeyFactory.getInstance("EC")
            .generatePublic(ECPublicKeySpec(ECPoint(x, y), ecParamSpec)) as ECPublicKey
    }

    private fun publicKeyBytes(key: ECPublicKey): ByteArray {
        fun norm(n: BigInteger): ByteArray {
            val raw = n.toByteArray()
            val stripped = if (raw.size == 33 && raw[0] == 0.toByte()) raw.drop(1).toByteArray() else raw
            return if (stripped.size < 32) ByteArray(32 - stripped.size) + stripped else stripped
        }
        return byteArrayOf(0x04) + norm(key.w.affineX) + norm(key.w.affineY)
    }

    private fun pad(s: String) = s + "=".repeat((4 - s.length % 4) % 4)
}
