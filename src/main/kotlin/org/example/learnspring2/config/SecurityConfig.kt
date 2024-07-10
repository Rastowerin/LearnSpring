package org.example.learnspring2.config

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.example.learnspring2.config.components.JwtAuthenticationConverter
import org.example.learnspring2.config.components.JwtFilter
import org.example.learnspring2.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl.fromHierarchy
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@Configuration
class SecurityConfig {

    @Autowired
    private lateinit var userService: UserService

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http {
            authorizeRequests {
                authorize(anyRequest, permitAll)
            }
            oauth2ResourceServer {
                jwt {
                    jwtAuthenticationConverter = JwtAuthenticationConverter()
                }
            }
            addFilterAt<BasicAuthenticationFilter>(JwtFilter(userService, jwtDecoder()))
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