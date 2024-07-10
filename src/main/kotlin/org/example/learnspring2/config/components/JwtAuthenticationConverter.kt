package org.example.learnspring2.config.components

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

class JwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {

    private val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(source: Jwt): AbstractAuthenticationToken {
        val scopes = jwtGrantedAuthoritiesConverter.convert(source)
        val authorities = source.getClaimAsStringList("authorities")?.map { SimpleGrantedAuthority(it) }
        return JwtAuthenticationToken(source, scopes.orEmpty() + authorities.orEmpty())
    }
}
