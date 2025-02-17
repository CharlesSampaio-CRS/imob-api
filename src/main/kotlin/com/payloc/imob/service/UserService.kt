package com.payloc.imob.service

import com.payloc.imob.model.dto.UserRequestDTO
import com.payloc.imob.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun update(request: UserRequestDTO): ResponseEntity<Any> {

        val existingUser = userRepository.findById(request.id).orElse(null) ?: return ResponseEntity.badRequest()
            .body("User not found!")

        existingUser.apply {
            request.password.let { password = passwordEncoder.encode(it) }
            request.role.let { role = it }
            request.status?.let { status = it }
            updatedAt = Date()
        }

        userRepository.save(existingUser)
        return ResponseEntity.ok("User updated successfully!")
    }

    fun delete(user: UserRequestDTO): ResponseEntity<Any> {
        val existingUser =
            userRepository.findById(user.id).orElse(null) ?: return ResponseEntity.badRequest().body("User not found!")

        existingUser.apply {
            status = "INACTIVE"
            updatedAt = Date()
        }

        userRepository.save(existingUser)
        return ResponseEntity.ok("User deleted successfully!")
    }
}