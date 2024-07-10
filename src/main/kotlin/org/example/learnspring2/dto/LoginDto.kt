package org.example.learnspring2.dto

import jakarta.validation.constraints.NotBlank

data class LoginDto(
    @field:NotBlank(message = "username cant be blank")
    val username: String,

    @field:NotBlank(message = "password cant be blank")
    val password: String
)
