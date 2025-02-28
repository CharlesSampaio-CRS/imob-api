package com.payloc.imob.controller

import com.payloc.imob.model.dto.RentalDTO
import com.payloc.imob.model.entity.Rental
import com.payloc.imob.service.RentalService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/rental")
class RentalController(
    private val service: RentalService
) {

    @Operation(summary = "Create a new rental", description = "Creates a new rental record")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Rental created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    @PostMapping
    fun create(@RequestBody rental: RentalDTO): ResponseEntity<Any> {
        return service.create(rental)
    }

    @Operation(summary = "Get all rentals", description = "Retrieves a list of all rentals")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of rentals")
    @GetMapping
    fun findAll(): ResponseEntity<Any> {
        return service.findAll()
    }

    @Operation(summary = "Find a rental by rental number", description = "Retrieves a rental using its rental number")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Rental found"),
            ApiResponse(responseCode = "404", description = "Rental not found", content = [Content()])
        ]
    )
    @GetMapping("/{rentalNumber}")
    fun findByRentalNumber(@PathVariable rentalNumber: String): ResponseEntity<Any> {
        return service.findByRentalNumber(rentalNumber)
    }

    @Operation(summary = "Update a rental", description = "Updates an existing rental record")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Rental updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Rental not found", content = [Content()])
        ]
    )
    @PutMapping("/update")
    fun update(@RequestBody rental: Rental): ResponseEntity<Any> {
        return service.updateRental(rental)
    }

    @Operation(summary = "Find rentals by status", description = "Retrieves rentals that match a given status")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful retrieval of rentals"),
            ApiResponse(responseCode = "404", description = "No rentals found", content = [Content()])
        ]
    )
    @GetMapping("/status/{status}")
    fun findByStatus(@PathVariable status: String): ResponseEntity<Any> {
        return service.findByStatus(status)
    }
}
