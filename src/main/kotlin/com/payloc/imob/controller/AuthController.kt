package com.payloc.imob.controller

import com.payloc.imob.model.dto.LoginRequestDTO
import com.payloc.imob.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {

    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
            ApiResponse(responseCode = "400", description = "Invalid username or password", content = [Content()])
        ]
    )
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequestDTO): ResponseEntity<Any> = authService.login(request)
}


