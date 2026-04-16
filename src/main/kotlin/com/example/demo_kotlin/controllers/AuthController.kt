package com.example.demo_kotlin.controllers

import com.example.demo_kotlin.dtos.JwtResponseDto
import com.example.demo_kotlin.dtos.LoginRequestDto
import com.example.demo_kotlin.security.CustomUserDetails
import com.example.demo_kotlin.security.JwtUtils
import com.example.demo_kotlin.security.TokenBlacklistService
import com.example.demo_kotlin.utils.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
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
    private val jwtUtils: JwtUtils,
    private val tokenBlacklistService: TokenBlacklistService
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

            val jwtToken = jwtUtils.generateToken(userDetails)

            val apiResponse = JwtResponseDto(
                token = jwtToken,
                email = userDetails.user.email,
                fullName = userDetails.user.fullName
            )

            return ApiResponse.success(apiResponse, "Login Berhasil")
        } catch (e: DisabledException) {
            log.warn("Login failed for disabled user: {}", loginRequest.email)
            return ApiResponse.error("Akun kamu dinonaktifkan", HttpStatus.FORBIDDEN)
        } catch (e: BadCredentialsException) {
            log.warn("Login failed due to bad credentials: {}", loginRequest.email)
            return ApiResponse.error("Email atau Password salah", HttpStatus.UNAUTHORIZED)
        } catch (e: Exception) {
            log.error("Unexpected error during login for: {}", loginRequest.email, e)
            return ApiResponse.error("Terjadi kesalahan server", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Operation(summary = "Logout user API")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    fun logoutUser(request: HttpServletRequest): ResponseEntity<ApiResponse<Unit>> {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error("Unauthorized", HttpStatus.UNAUTHORIZED)
        }

        val jwtToken = authHeader.substring(7)
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated) {
            return ApiResponse.error("Unauthorized", HttpStatus.UNAUTHORIZED)
        }

        val expiry = runCatching { jwtUtils.extractExpiration(jwtToken).toInstant() }.getOrNull()
        tokenBlacklistService.revokeToken(jwtToken, expiry)
        SecurityContextHolder.clearContext()

        return ApiResponse.success(null, "Logout berhasil")
    }
}