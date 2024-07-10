package org.example.learnspring2.services

import org.example.learnspring2.dto.LoginDto
import org.example.learnspring2.entities.User
import org.example.learnspring2.dto.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AuthService {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jwtEncoder: JwtEncoder


    fun getAccessToken(user: User): String {
        val now = Instant.now()
        val expiry = 36000L

         val scope: List<String> = user.authorities.stream()
        .map{obj: GrantedAuthority -> obj.authority}
        .toList()
         val claims = JwtClaimsSet.builder()
        .issuer("self")
        .issuedAt(now)
        .expiresAt(now.plusSeconds(expiry))
        .subject(user.id.toString())
        .claim("authorities", scope)
        .build()

        val token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue

        return token
    }

    fun getAccessTokenByDto(userDto: UserDto): String {
        val user = userService.findByDto(userDto)
        return this.getAccessToken(user)
    }

    fun getAccessTokenByLogin(login: LoginDto): String {
        val user = userService.findByUsername(login.username)
        if (!passwordEncoder.matches(login.password, user.password)) { throw Exception("Invalid password") }
        return this.getAccessToken(user)
    }


    fun register(userDto: UserDto) {

        val encodedPassword = passwordEncoder.encode(userDto.password)
        val user = User(
            username=userDto.username,
            password=encodedPassword,
            firstName=userDto.firstName,
            middleName=userDto.middleName,
            lastName=userDto.lastName,
            email=userDto.email,
            phone=userDto.phone
        )

        if (userService.existsByUsername(user.username!!)) {
           throw Exception("User with that username already exists.")
        }

        if (userService.existsByEmail(user.email!!)) {
            throw Exception("User with that email already exists.")
        }

        userService.save(user)
    }
}
