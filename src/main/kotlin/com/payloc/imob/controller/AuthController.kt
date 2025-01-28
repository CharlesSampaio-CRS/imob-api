package com.payloc.imob.controller

import com.payloc.imob.model.dto.LoginRequest
import com.payloc.imob.model.dto.RegisterRequest
import com.payloc.imob.model.entity.User
import com.payloc.imob.repository.UserRepository
import com.payloc.imob.util.JwtUtil
import com.payloc.imob.util.UsernameUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val jwtUtil: JwtUtil
) {

    @PostMapping("/register")
    fun register(@Validated @RequestBody request: RegisterRequest): ResponseEntity<String> {
        if (userRepository.findByEmail(request.email) != null) {
            return ResponseEntity.badRequest().body("User with this email already exists!")
        }

        val hashedPassword = passwordEncoder.encode(request.password)
        val newUser = User(
            id = null,
            username = UsernameUtil.generateUsername(request.email),
            name = request.name,
            email = request.email,
            password = hashedPassword,
            role = request.role,
            status = "ACTIVE"
        )
        userRepository.save(newUser)
        return ResponseEntity.ok("User registered successfully!")
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        val existingUser = userRepository.findByEmail(request.email)
            ?: return ResponseEntity.badRequest().body("Invalid username or password!")

        return if (passwordEncoder.matches(request.password, existingUser.password)) {
            val token = jwtUtil.generateToken(existingUser.username)
            ResponseEntity.ok(mapOf("token" to token))
        } else {
            ResponseEntity.badRequest().body("Invalid username or password!")
        }
    }

}