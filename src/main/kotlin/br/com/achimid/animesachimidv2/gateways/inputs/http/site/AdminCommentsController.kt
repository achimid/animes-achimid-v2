package br.com.achimid.animesachimidv2.gateways.inputs.http.site

import br.com.achimid.animesachimidv2.configurations.AdminAccessChecker
import br.com.achimid.animesachimidv2.usecases.ModerateCommentUseCase
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/comments")
class AdminCommentsController(
    val adminAccessChecker: AdminAccessChecker,
    val moderateCommentUseCase: ModerateCommentUseCase,
) {

    @GetMapping
    fun comments(
        @CookieValue(value = "user_id", required = false) userId: String? = null,
        model: Model
    ): String {
        if (!adminAccessChecker.isAdmin(userId)) return "redirect:/"
        model.addAttribute("allComments", moderateCommentUseCase.listAll())
        return "admin-comments"
    }
}
