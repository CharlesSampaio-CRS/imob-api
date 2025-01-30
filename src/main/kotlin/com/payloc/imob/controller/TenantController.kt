package com.payloc.imob.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.payloc.imob.model.entity.Tenant
import com.payloc.imob.service.TenantService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/tenant")
class TenantController(
    private val service: TenantService,
    private val objectMapper: ObjectMapper
) {

    @Operation(summary = "Create a new tenant", description = "Creates a new tenant with optional file uploads")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenant created successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(
        @RequestPart("tenant") tenantJson: String,
        @RequestPart("files", required = false) files: List<MultipartFile>?
    ): ResponseEntity<Any> {
        val tenant = objectMapper.readValue(tenantJson, Tenant::class.java)
        return service.create(tenant, files ?: emptyList())
    }

    @Operation(summary = "Get all tenants", description = "Retrieves a list of all tenants")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of tenants")
    @GetMapping
    fun findAll(): ResponseEntity<Any> {
        return service.findAll()
    }

    @Operation(summary = "Find a tenant by CPF", description = "Retrieves a tenant using their CPF")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenant found"),
            ApiResponse(responseCode = "404", description = "Tenant not found", content = [Content()])
        ]
    )
    @GetMapping("/{cpf}")
    fun findByCpf(@PathVariable cpf: String): ResponseEntity<Any> {
        return service.findByPersonCpf(cpf)
    }

    @Operation(summary = "Update a tenant", description = "Updates an existing tenant's information")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Tenant updated successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Tenant not found", content = [Content()])
        ]
    )
    @PutMapping("/update")
    fun update(@RequestBody tenant: Tenant): ResponseEntity<Any> {
        return service.updateTenant(tenant)
    }
}
