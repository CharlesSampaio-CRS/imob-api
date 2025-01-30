package com.payloc.imob.controller

import com.payloc.imob.model.entity.Property
import com.payloc.imob.service.PropertyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/property")
class PropertyController(
    private val service: PropertyService
) {

    @Operation(summary = "Create a new property", description = "Creates a new property record")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Property created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    @PostMapping
    fun create(@RequestBody property: Property): ResponseEntity<Any> {
        return service.create(property)
    }

    @Operation(summary = "Get all properties", description = "Retrieves a list of all properties")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of properties")
    @GetMapping
    fun findall(): ResponseEntity<Any> {
        return service.findAll()
    }

    @Operation(summary = "Find a property by property number", description = "Retrieves a property using its property number")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Property found"),
            ApiResponse(responseCode = "404", description = "Property not found", content = [Content()])
        ]
    )
    @GetMapping("/{propertyNumber}")
    fun findByPropertyNumber(@PathVariable propertyNumber: String): ResponseEntity<Any> {
        return service.findByPropertyNumber(propertyNumber)
    }

    @Operation(summary = "Update a property", description = "Updates an existing property record")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Property updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Property not found", content = [Content()])
        ]
    )
    @PutMapping("/update")
    fun update(@RequestBody property: Property): ResponseEntity<Any> {
        return service.updateProperty(property)
    }

    @Operation(summary = "Find properties by status", description = "Retrieves properties that match a given status")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful retrieval of properties"),
            ApiResponse(responseCode = "404", description = "No properties found", content = [Content()])
        ]
    )
    @GetMapping("/status/{status}")
    fun findByStatus(@PathVariable status: String): ResponseEntity<Any> {
        return service.findByStatus(status)
    }
}
