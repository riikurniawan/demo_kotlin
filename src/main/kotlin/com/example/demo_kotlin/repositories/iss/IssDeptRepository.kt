package com.example.demo_kotlin.repositories.iss

import com.example.demo_kotlin.models.iss.IssDept
import com.example.demo_kotlin.models.portal.PortalUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IssDeptRepository : JpaRepository<IssDept, Long> {}