package org.example.learnspring2.config.components

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.learnspring2.services.UserService
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class JwtFilter(private var userService: UserService, private var jwtDecoder: JwtDecoder) : GenericFilterBean() {


    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain
    ) {
        val httpRequest: HttpServletRequest = request as HttpServletRequest
        val httpResponse: HttpServletResponse = response as HttpServletResponse

        val authHeader: String? = httpRequest.getHeader("Authorization")

        if (authHeader != null) {

            val token = authHeader.split(" ")[1]
            val jwt = jwtDecoder.decode(token)

            val id = jwt.subject.toLong()
            if (!userService.existsById(id)) {
                httpResponse.status = 401
                return
            }
        }
        chain.doFilter(request, response)
    }
}
