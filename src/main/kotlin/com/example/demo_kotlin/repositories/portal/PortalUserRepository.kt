package com.example.demo_kotlin.repositories.portal

import com.example.demo_kotlin.models.portal.PortalUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PortalUserRepository : JpaRepository<PortalUser, Long> {
	fun findByFullNameContainingIgnoreCase(
		fullName: String,
		pageable: Pageable
	): Page<PortalUser>

	fun findByEmailContainingIgnoreCase(
		email: String,
		pageable: Pageable
	): Page<PortalUser>

	fun findByEmail(email: String): PortalUser?

	fun existsByEmail(email: String): Boolean
}