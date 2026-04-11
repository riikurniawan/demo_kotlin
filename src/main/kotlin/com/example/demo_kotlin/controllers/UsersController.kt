package com.example.demo_kotlin.controllers

import com.example.demo_kotlin.models.portal.PortalUser
import com.example.demo_kotlin.repositories.portal.PortalUserRepository
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Pageable
import org.springdoc.core.annotations.ParameterObject
import io.swagger.v3.oas.annotations.Operation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@RestController
@Validated
class UsersController(
    private val usersRepository: PortalUserRepository
) {

    private val allowedSortFields = setOf("idUser", "fullName", "email")

    @Operation(summary = "Get all users", description = "Returns paginated users and supports optional search by fullName or email.")
    @GetMapping("/users")
    fun getAllUsers(
        @RequestParam(required = false)
        @Size(max = 100, message = "fullName maksimal 100 karakter")
        fullName: String?,
        @RequestParam(required = false)
        @Size(max = 100, message = "email maksimal 100 karakter")
        email: String?,
        @ParameterObject
        pageable: Pageable
    ): Page<PortalUser> {
        validateSortFields(pageable.sort)

        return when {
            !fullName.isNullOrBlank() -> usersRepository.findByFullNameContainingIgnoreCase(fullName, pageable)
            !email.isNullOrBlank() -> usersRepository.findByEmailContainingIgnoreCase(email, pageable)
            else -> usersRepository.findAll(pageable)
        }
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/users/{id}")
    fun getUsersById(@PathVariable @Positive(message = "id harus lebih besar dari 0") id: Long): PortalUser? {
        return usersRepository.findById(id).orElse(null)
    }

    private fun validateSortFields(sort: Sort) {
        val invalidFields = sort.map { it.property }.filterNot { it in allowedSortFields }
        if (invalidFields.isNotEmpty()) {
            throw IllegalArgumentException(
                "Invalid sort field(s): ${invalidFields.joinToString(", ")}. Allowed fields: ${allowedSortFields.joinToString(", ")}"
            )
        }
    }
}