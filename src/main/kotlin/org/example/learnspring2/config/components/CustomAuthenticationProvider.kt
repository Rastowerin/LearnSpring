package org.example.learnspring2.config.components

import org.example.learnspring2.users.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component


@Component
class CustomAuthenticationProvider : AuthenticationProvider {

    @Autowired
    private val userDetailsService: UserService? = null

    @Autowired
    private val passwordEncoder: PasswordEncoder? = null

    override fun authenticate(authentication: Authentication): Authentication {
        val username: String = authentication.name
        val password: String = authentication.credentials.toString()
        val u = userDetailsService!!.findByUsername(username)

        if (passwordEncoder!!.matches(password, u.password)) {
            return UsernamePasswordAuthenticationToken(
                username,
                password,
                u.authorities
            )
        } else {
            throw BadCredentialsException("Something went wrong!")
        }
    }

    override fun supports(authenticationType: Class<*>): Boolean {
        return (authenticationType
                == UsernamePasswordAuthenticationToken::class.java)
    }
}
