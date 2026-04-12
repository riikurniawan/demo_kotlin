package com.example.demo_kotlin.dtos

import jakarta.validation.constraints.Size
import org.springdoc.core.annotations.ParameterObject

@ParameterObject
data class UserSearchDto(
    @field:Size(max = 100, message = "fullName maksimal 100 karakter")
    val fullName: String? = null,

    @field:Size(max = 100, message = "email maksimal 100 karakter")
    val email: String? = null
)
