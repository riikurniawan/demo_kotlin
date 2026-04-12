package com.example.demo_kotlin.controllers

import com.example.demo_kotlin.dtos.JwtResponseDto
import com.example.demo_kotlin.dtos.LoginRequestDto
import com.example.demo_kotlin.security.CustomUserDetails
import com.example.demo_kotlin.security.JwtUtils
import com.example.demo_kotlin.utils.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtils: JwtUtils
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Login user API")
    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequestDto): ResponseEntity<ApiResponse<JwtResponseDto>> {

        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
            )

            SecurityContextHolder.getContext().authentication = authentication

            val userDetails = authentication.principal as CustomUserDetails

            println(userDetails.user)
            val jwtToken = jwtUtils.generateToken(userDetails)

            val apiResponse = JwtResponseDto(
                token = jwtToken,
                email = userDetails.user.email,
                fullName = userDetails.user.fullName
            )

            return ApiResponse.success(apiResponse, "Login Berhasil")
        } catch (e: Exception) {
            println("Login error: ${e.cause.}")
            return ApiResponse.error("Email atau Password salah", HttpStatus.UNAUTHORIZED)
        }
    }
}