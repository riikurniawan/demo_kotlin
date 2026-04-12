package com.example.demo_kotlin.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @field:NotBlank(message = "email tidak boleh kosong")
    @field:Email(message = "email tidak valid")
    val email: String,

    @field:NotBlank(message = "password tidak boleh kosong")
    val password: String
)
