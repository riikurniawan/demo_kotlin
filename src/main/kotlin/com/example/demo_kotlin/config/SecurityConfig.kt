package com.example.demo_kotlin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import com.example.demo_kotlin.security.JwtAuthenticationFilter

@Configuration
// @EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    private val jwtAuthFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
    }

    @Bean
    fun authenticationProvider(passwordEncoder: PasswordEncoder): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder)
        return authProvider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity, env: Environment, authProvider: AuthenticationProvider): SecurityFilterChain {
        val isProd = env.activeProfiles.contains("prod")

        if (!isProd) {
            // Development / Default: JWT tetap aktif agar endpoint protected bisa dites dari lokal.
            http
                .cors { } // Mengaktifkan CORS
                .authorizeHttpRequests { authz ->
                    authz
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/v3/api-docs.yaml"
                        ).permitAll()
                        .requestMatchers("/secure/**").authenticated()
                        .anyRequest().permitAll()
                }
                .sessionManagement { session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                }
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
                .httpBasic { it.disable() }
                .formLogin { it.disable() }
                .csrf { it.disable() }
            return http.build()
        }

        // Production: Wajib JWT / Auth
        http
            .cors { } // Mengaktifkan CORS
            .authorizeHttpRequests { authz ->
                authz
                    // Auth login endpoint public
                    .requestMatchers("/auth/login").permitAll()
                    
                    // Allow public access to Swagger UI and API docs
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml"
                    ).permitAll()
                    // All other requests require authentication
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .csrf { it.disable() }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(env: Environment): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        val isProd = env.activeProfiles.contains("prod")

        if (isProd) {
            // Production CORS
            configuration.allowedOrigins = listOf("http://192.168.1.140") // Sesuaikan dengan domain riil Frontend Anda
            configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            configuration.allowedHeaders = listOf("Authorization", "Content-Type")
            configuration.allowCredentials = true
        } else {
            // Development CORS
            configuration.allowedOriginPatterns = listOf("*") // Lebih fleksibel dan aman dibanding allowedOrigins="*" di Spring Security terbaru
            configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            configuration.allowedHeaders = listOf("*")
            configuration.allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
