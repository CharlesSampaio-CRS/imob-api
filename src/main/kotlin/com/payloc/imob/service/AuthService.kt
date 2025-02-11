package com.payloc.imob.service

import com.payloc.imob.model.dto.LoginRequestDTO
import com.payloc.imob.model.dto.UserRequestDTO
import com.payloc.imob.model.entity.User
import com.payloc.imob.model.enumerate.PersonStatus
import com.payloc.imob.repository.UserRepository
import com.payloc.imob.util.JwtUtil
import com.payloc.imob.util.UsernameUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
    ) {

    fun register(request: UserRequestDTO): Boolean {
        if (userRepository.findByEmail(request.email) != null) {
            return true
        }

        val hashedPassword = passwordEncoder.encode(request.password)
        val newUser = User(
            id = null,
            username = UsernameUtil.generateUsername(request.email),
            name = request.name,
            email = request.email,
            password = hashedPassword,
            role = request.role,
            status = PersonStatus.ACTIVE.name,
            createdAt = Date(),
            updatedAt = null
        )
        userRepository.save(newUser)
        return false
    }

    fun login(request: LoginRequestDTO): ResponseEntity<Any> {
        val existingUser = when {
            !request.email.isNullOrBlank() -> userRepository.findByEmail(request.email)
            !request.username.isNullOrBlank() -> userRepository.findByUsername(request.username)
            else -> null
        } ?: return ResponseEntity.badRequest().body("Invalid username or password!")
        return if (passwordEncoder.matches(request.password, existingUser.password)) {
            val token = jwtUtil.generateToken(existingUser.username)
            ResponseEntity.ok(mapOf("token" to token))
        } else {
            ResponseEntity.badRequest().body("Invalid username or password!")
        }
    }

    fun update(request: UserRequestDTO): ResponseEntity<Any> {
        val existingUser = userRepository.findById(request.id).orElse(null)
            ?: return ResponseEntity.badRequest().body("User not found!")

        request.password.let {
            existingUser.password = passwordEncoder.encode(it)
        }
        request.role.let {
            existingUser.role = it
        }
        request.status.let {
            if (it != null) {
                existingUser.status = it
            }
        }
        existingUser.updatedAt = Date()

        userRepository.save(existingUser)
        return ResponseEntity.ok("User updated successfully!")
    }
}