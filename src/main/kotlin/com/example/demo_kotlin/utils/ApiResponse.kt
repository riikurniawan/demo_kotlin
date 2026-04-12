package com.example.demo_kotlin.utils

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T? = null, message: String = "Success"): ResponseEntity<ApiResponse<T>> {
            return ResponseEntity.ok(ApiResponse(true, message, data))
        }

        fun <T> created(data: T? = null, message: String = "Created successfully"): ResponseEntity<ApiResponse<T>> {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse(true, message, data))
        }

        fun <T> error(message: String, status: HttpStatus = HttpStatus.BAD_REQUEST, data: T? = null): ResponseEntity<ApiResponse<T>> {
            return ResponseEntity.status(status).body(ApiResponse(false, message, data))
        }
    }
}
