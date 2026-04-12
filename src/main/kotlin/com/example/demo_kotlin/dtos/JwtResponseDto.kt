package com.example.demo_kotlin.dtos

data class JwtResponseDto(
    val token: String,
    val email: String,
    val fullName: String
)
