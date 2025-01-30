package com.payloc.imob.controller

import com.payloc.imob.model.dto.LoginRequest
import com.payloc.imob.model.dto.RegisterRequest
import com.payloc.imob.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {

    @Operation(summary = "Register a new user", description = "Creates a new user account with encrypted password")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User registered successfully"),
            ApiResponse(responseCode = "400", description = "User with this email already exists", content = [Content()])
        ]
    )
    @PostMapping("/register")
    fun register(@Validated @RequestBody request: RegisterRequest): ResponseEntity<String> =
        if (authService.register(request)) {
            ResponseEntity.badRequest().body("User with this email already exists!")
        } else {
            ResponseEntity.ok("User registered successfully!")
        }

    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            ApiResponse(responseCode = "400", description = "Invalid username or password", content = [Content()])
        ]
    )
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> = authService.login(request)
}
