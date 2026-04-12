package com.example.demo_kotlin.security

import com.example.demo_kotlin.models.portal.PortalUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val user: PortalUser
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        // Beri default ROLE_USER
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String {
        return user.password ?: ""
    }

    override fun getUsername(): String {
        // Username di Spring kita map dari Email
        return user.email
    }

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    
    // Akun diblokir jika isDeleted = 1
    override fun isEnabled(): Boolean = (user.isDeleted == null || user.isDeleted == 0)
}
