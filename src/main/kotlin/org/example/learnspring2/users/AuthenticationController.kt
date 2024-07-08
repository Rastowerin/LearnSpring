package org.example.learnspring2.users

import com.fasterxml.jackson.annotation.JsonView
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.web.bind.annotation.*


@RestController
@RolesAllowed("ANONYMOUS")
@RequestMapping("/auth")
class AuthenticationController {

    @Autowired
    var userService: UserService? = null

    @PostMapping("signup")
    fun signup(@Valid @RequestBody userDto: UserDto): ResponseEntity<Any> {

        try {
            userService!!.createFromDto(userDto)
        } catch (e: Exception) {
            return ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity(userService!!.getAccessTokenByDto(userDto), HttpStatus.CREATED)
    }

    @PostMapping("login")
    fun login(@Valid @RequestBody login: LoginDto): ResponseEntity<String> {

        try {
            return ResponseEntity(userService!!.getAccessTokenByLogin(login), HttpStatus.OK)
        } catch (e: Exception) {
            return ResponseEntity("Invalid username or password", HttpStatus.BAD_REQUEST)
        }
    }
}
