package org.example.learnspring2.controllers

import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.example.learnspring2.services.AuthService
import org.example.learnspring2.dto.LoginDto
import org.example.learnspring2.dto.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RolesAllowed("ANONYMOUS")
@RequestMapping("/auth")
class AuthenticationController {

    @Autowired
    private lateinit var authService: AuthService

    @PostMapping("signup")
    fun signup(@Valid @RequestBody userDto: UserDto): ResponseEntity<Any> {

        try {
            authService.register(userDto)
        } catch (e: Exception) {
            return ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(authService.getAccessTokenByDto(userDto), HttpStatus.CREATED)
    }

    @PostMapping("login")
    fun login(@Valid @RequestBody login: LoginDto): ResponseEntity<String> {

        try {
            return ResponseEntity(authService.getAccessTokenByLogin(login), HttpStatus.OK)
        } catch (e: Exception) {
            return ResponseEntity("Invalid username or password", HttpStatus.BAD_REQUEST)
        }
    }
}
