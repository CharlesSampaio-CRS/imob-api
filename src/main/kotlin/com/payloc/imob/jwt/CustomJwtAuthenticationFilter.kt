package com.payloc.imob.jwt

import com.payloc.imob.repository.UserRepository
import com.payloc.imob.util.JwtUtil
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class CustomJwtAuthenticationFilter(
    authManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : BasicAuthenticationFilter(authManager) {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val token = request.getHeader("Authorization")?.replace("Bearer ", "")

        if (!token.isNullOrEmpty()) {
            val username = jwtUtil.validateToken(token)
            if (username != null) {
                val user = userRepository.findByUsername(username)
                if (user != null) {
                    val auth = UsernamePasswordAuthenticationToken(user.username, null, emptyList())
                    SecurityContextHolder.getContext().authentication = auth
                }
            }
        }

        chain.doFilter(request, response)
    }
}
