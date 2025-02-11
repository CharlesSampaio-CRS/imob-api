package com.payloc.imob.config

import com.payloc.imob.jwt.CustomJwtAuthenticationFilter
import com.payloc.imob.repository.UserRepository
import com.payloc.imob.util.JwtUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig() {

    @Bean
    fun securityFilterChain(http: HttpSecurity, authenticationManager: AuthenticationManager, jwtUtil: JwtUtil, userRepository: UserRepository): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilter(CustomJwtAuthenticationFilter(authenticationManager, jwtUtil, userRepository))

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
}
