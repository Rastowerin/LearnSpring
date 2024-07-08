package org.example.learnspring2.config

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl.fromHierarchy
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.authorization.OAuth2ReactiveAuthorizationManagers.hasScope
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.filter.GenericFilterBean
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey


class CustomFilter : GenericFilterBean() {
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain
    ) {

        val httpRequest: HttpServletRequest = request as HttpServletRequest
        val httpResponse: HttpServletResponse = response as HttpServletResponse

        chain.doFilter(request, response)
    }
}

class CustomJwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken> {

  private val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

  override fun convert(source: Jwt): AbstractAuthenticationToken {
    val scopes = jwtGrantedAuthoritiesConverter.convert(source)
    val authorities = source.getClaimAsStringList("authorities")?.map { SimpleGrantedAuthority(it) }
    return JwtAuthenticationToken(source, scopes.orEmpty() + authorities.orEmpty())
  }
}

@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http {
            authorizeRequests {
                authorize(anyRequest, permitAll)
            }
            sessionManagement {  }
            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = CustomJwtAuthenticationConverter()
                }
            }
            httpBasic {  }
            csrf { disable() }
        }

        return http.build()
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy {
        return fromHierarchy("ROLE_ADMIN > ROLE_USER > ROLE_ANONYMOUS")
    }

    @Value("\${jwt.public.key}")
    var key: RSAPublicKey? = null

    @Value("\${jwt.private.key}")
    var priv: RSAPrivateKey? = null

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(this.key).build()
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk: JWK = RSAKey.Builder(this.key).privateKey(this.priv).build()
        val jwks: JWKSource<SecurityContext> = ImmutableJWKSet(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()
    }
}
