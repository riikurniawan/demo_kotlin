package com.example.demo_kotlin.controllers

import com.example.demo_kotlin.models.portal.PortalUser
import com.example.demo_kotlin.services.UsersService
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Pageable
import org.springdoc.core.annotations.ParameterObject
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import com.example.demo_kotlin.utils.ApiResponse
import com.example.demo_kotlin.dtos.UserSearchDto
import com.example.demo_kotlin.dtos.CreateUserDto
import com.example.demo_kotlin.dtos.UpdateUserDto

@RestController
@Validated
class UsersController(
    private val usersService: UsersService
) {

    @Operation(summary = "Get all users", description = "Returns paginated users and supports optional search by fullName or email.")
    @GetMapping("/users")
    fun getAllUsers(
        @Valid search: UserSearchDto,
        @ParameterObject
        pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<PortalUser>>> {
        val users = usersService.getAllUsers(search.fullName, search.email, pageable)
        return ApiResponse.success(users, "Users retrieved successfully")
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/users/{id}")
    fun getUsersById(@PathVariable @Positive(message = "id harus lebih besar dari 0") id: Long): ResponseEntity<ApiResponse<PortalUser>> {
        val user = usersService.getUserById(id)
        return if (user != null) {
            ApiResponse.success(user, "User retrieved successfully")
        } else {
            ApiResponse.error("User not found", HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping("/users")
    fun createUser(@RequestBody @Valid request: CreateUserDto): ResponseEntity<ApiResponse<PortalUser>> {
        val createdUser = usersService.createUser(request)
        return ApiResponse.created(createdUser, "User successfully created")
    }

    @Operation(summary = "Update user by id")
    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable @Positive(message = "id harus lebih besar dari 0") id: Long,
        @RequestBody @Valid request: UpdateUserDto
    ): ResponseEntity<ApiResponse<PortalUser>> {
        val updatedUser = usersService.updateUser(id, request)
        return ApiResponse.success(updatedUser, "User successfully updated")
    }

    @Operation(summary = "Delete user by id")
    @DeleteMapping("/users/{id}")
    fun deleteUser(
        @PathVariable @Positive(message = "id harus lebih besar dari 0") id: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        usersService.deleteUser(id)
        return ApiResponse.success(null, "User successfully deleted")
    }
}