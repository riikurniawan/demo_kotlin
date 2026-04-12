package com.example.demo_kotlin.services

import com.example.demo_kotlin.models.portal.PortalUser
import com.example.demo_kotlin.repositories.portal.PortalUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import com.example.demo_kotlin.dtos.CreateUserDto
import com.example.demo_kotlin.dtos.UpdateUserDto

@Service
class UsersService(
    private val usersRepository: PortalUserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    private val allowedSortFields = setOf("idUser", "fullName", "email")

    fun getAllUsers(fullName: String?, email: String?, pageable: Pageable): Page<PortalUser> {
        validateSortFields(pageable.sort)

        return when {
            !fullName.isNullOrBlank() -> usersRepository.findByFullNameContainingIgnoreCase(fullName, pageable)
            !email.isNullOrBlank() -> usersRepository.findByEmailContainingIgnoreCase(email, pageable)
            else -> usersRepository.findAll(pageable)
        }
    }

    fun getUserById(id: Long): PortalUser? {
        return usersRepository.findById(id).orElse(null)
    }

    fun createUser(request: CreateUserDto): PortalUser {
        if (usersRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email '${request.email}' sudah terdaftar.")
        }
        
        val hashedPassword = passwordEncoder.encode(request.password)
        val newUser = PortalUser(
            fullName = request.fullName,
            email = request.email,
            password = hashedPassword,
            createdDate = LocalDateTime.now(),
            isDeleted = 0
        )
        
        return usersRepository.save(newUser)
    }

    fun updateUser(id: Long, request: UpdateUserDto): PortalUser {
        val existingUser = usersRepository.findById(id).orElseThrow {
            IllegalArgumentException("User dengan ID $id tidak ditemukan.")
        }

        // Cek jika email diubah dan apakah email baru sudah terdaftar
        if (request.email != existingUser.email && usersRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email '${request.email}' sudah terdaftar.")
        }

        val updatedPassword = request.password?.let { passwordEncoder.encode(it) } ?: existingUser.password

        val updatedUser = existingUser.copy(
            fullName = request.fullName,
            updatedDate = LocalDateTime.now(),
            email = request.email,
            password = updatedPassword
        )

        return usersRepository.save(updatedUser)
    }

    fun deleteUser (id: Long) {
        val existingUser = usersRepository.findById(id).orElseThrow {
            IllegalArgumentException("User dengan ID $id tidak ditemukan.")
        }
        
        // Soft delete: tandai isDeleted = 1 dan set updatedDate
        val deletedUser = existingUser.copy(
            isDeleted = 1,
            updatedDate = LocalDateTime.now()
        )
        usersRepository.save(deletedUser)
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