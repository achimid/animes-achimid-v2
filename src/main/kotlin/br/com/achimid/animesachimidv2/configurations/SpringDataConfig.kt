package br.com.achimid.animesachimidv2.configurations

import org.springframework.context.annotation.Configuration
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
class SpringDataConfig