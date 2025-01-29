package com.payloc.imob.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.payloc.imob.model.entity.Tenant
import com.payloc.imob.service.TenantService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
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
    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(
        @RequestPart("tenant") tenantJson: String, // JSON como string
        @RequestPart("files", required = false) files: List<MultipartFile>? // Arquivos opcionais
    ): ResponseEntity<Any> {
        val tenant = objectMapper.readValue(tenantJson, Tenant::class.java) // Converte JSON para objeto
        return service.create(tenant, files ?: emptyList()) // Garante que files não seja null
    }

    @GetMapping
    fun findAll(): ResponseEntity<Any> {
        return service.findAll()
    }

    @GetMapping("/{cpf}")
    fun findByCpf(@PathVariable cpf: String): ResponseEntity<Any> {
        return service.findByPersonCpf(cpf)
    }

    @PutMapping("/update")
    fun update(@RequestBody tenant: Tenant): ResponseEntity<Any> {
        return service.updateTenant(tenant)
    }
}
