package com.example.demo_kotlin.models.portal

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "portal_user_db")
data class PortalUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    val idUser: Long? = null,

    @Column(name = "full_name")
    val fullName: String,

    @Column(name = "email")
    val email: String
)