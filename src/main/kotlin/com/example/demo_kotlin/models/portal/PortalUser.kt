package com.example.demo_kotlin.models.portal

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.boot.context.properties.bind.DefaultValue
import java.time.LocalDateTime

@Entity
@Table(name = "portal_user_db")
data class PortalUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    val idUser: Long? = null,

    @Column(name = "full_name")
    val fullName: String,

    @Column(name = "email", unique = true)
    val email: String,

    @JsonIgnore
    @Column(name = "password")
    val password: String? = null,

    @JsonIgnore
    @Column(name = "created_date")
    val createdDate: LocalDateTime? = null,

    @JsonIgnore
    @Column(name = "updated_date")
    val updatedDate: LocalDateTime? = null,

    @JsonIgnore
    @DefaultValue("0")
    @Column(name = "is_deleted")
    val isDeleted: Int? = null
)