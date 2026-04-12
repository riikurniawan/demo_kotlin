package com.example.demo_kotlin.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateUserDto(
    @field:NotBlank(message = "fullName tidak boleh kosong")
    @field:Size(max = 100, message = "fullName maksimal 100 karakter")
    val fullName: String,

    @field:NotBlank(message = "email tidak boleh kosong")
    @field:Size(max = 100, message = "email maksimal 100 karakter")
    @field:Email(message = "email harus valid")
    val email: String,

    @field:Size(min = 6, message = "password minimal 6 karakter")
    val password: String? = null
)
