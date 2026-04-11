package com.example.demo_kotlin.models.iss

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "iss_dept")
data class IssDept(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    val deptId  : Long? = null,

    @Column(name = "dept")
    val dept: String,
)