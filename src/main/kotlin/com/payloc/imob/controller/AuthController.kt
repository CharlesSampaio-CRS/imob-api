package com.payloc.imob.controller

import com.payloc.imob.model.dto.LoginRequestDTO
import com.payloc.imob.model.dto.UserRequestDTO
import com.payloc.imob.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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
    fun register(@Validated @RequestBody request: UserRequestDTO): ResponseEntity<String> =
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
    fun login(@RequestBody request: LoginRequestDTO): ResponseEntity<Any> = authService.login(request)

    @Operation(summary = "Update user", description = "Updates an existing user's details")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User updated successfully"),
            ApiResponse(responseCode = "400", description = "User not found", content = [Content()]),
            ApiResponse(responseCode = "403", description = "You do not have permission to edit users", content = [Content()])
        ]
    )
    @PutMapping("/update")
    fun update(@Validated @RequestBody request: UserRequestDTO): ResponseEntity<Any> {
        return try {
            authService.update(request)
        } catch (e: AccessDeniedException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to edit users!")
        }
    }
}
