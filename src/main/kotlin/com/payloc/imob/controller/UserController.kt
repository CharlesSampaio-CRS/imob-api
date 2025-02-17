package com.payloc.imob.controller

import com.payloc.imob.model.dto.UserRequestDTO
import com.payloc.imob.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
) {

    @Operation(summary = "Update user", description = "Updates an existing user's details")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User updated successfully"),
            ApiResponse(responseCode = "400", description = "User not found", content = [Content()]),
            ApiResponse(responseCode = "403", description = "You do not have permission to edit users", content = [Content()])
        ]
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update")
    fun update(@Validated @RequestBody request: UserRequestDTO): ResponseEntity<Any> {
        return userService.update(request)
    }

    @Operation(summary = "Delete user", description = "Deletes an existing user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User deleted successfully"),
            ApiResponse(responseCode = "400", description = "User not found", content = [Content()]),
            ApiResponse(responseCode = "403", description = "You do not have permission to delete users", content = [Content()])
        ]
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/delete")
    fun delete(@Validated @RequestBody request: UserRequestDTO): ResponseEntity<Any> {
        return userService.delete(request)
    }
}
