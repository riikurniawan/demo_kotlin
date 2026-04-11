package com.example.demo_kotlin.config

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(exception: ConstraintViolationException): ResponseEntity<Map<String, Any>> {
        val errors = exception.constraintViolations.map {
            mapOf(
                "field" to it.propertyPath.toString(),
                "message" to it.message
            )
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf(
                "message" to "Validation failed",
                "errors" to errors
            )
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(exception: MethodArgumentTypeMismatchException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf(
                "message" to "Invalid parameter",
                "field" to exception.name,
                "value" to (exception.value?.toString() ?: "null")
            )
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(exception: IllegalArgumentException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf(
                "message" to (exception.message ?: "Invalid request")
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = exception.bindingResult.fieldErrors.map {
            mapOf(
                "field" to it.field,
                "message" to (it.defaultMessage ?: "Invalid value")
            )
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf(
                "message" to "Validation failed",
                "errors" to errors
            )
        )
    }
}