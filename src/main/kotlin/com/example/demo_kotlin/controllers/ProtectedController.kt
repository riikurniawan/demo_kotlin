package com.example.demo_kotlin.controllers

import com.example.demo_kotlin.security.CustomUserDetails
import com.example.demo_kotlin.utils.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/secure")
@SecurityRequirement(name = "bearerAuth")
class ProtectedController {

    @Operation(summary = "Get current authenticated user")
    @GetMapping("/me")
    fun me(authentication: Authentication?): ResponseEntity<ApiResponse<Map<String, String>>> {
        val principal = authentication?.principal as? CustomUserDetails
            ?: return ApiResponse.error("Unauthorized", HttpStatus.UNAUTHORIZED)

        val data = mapOf(
            "email" to principal.user.email,
            "fullName" to principal.user.fullName
        )

        return ApiResponse.success(data, "JWT valid")
    }
}