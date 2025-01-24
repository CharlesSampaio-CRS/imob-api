package com.payloc.imob.service

import com.payloc.imob.constants.Constants.Companion.INITIAL_ELEMENT_NUMBER
import com.payloc.imob.controller.vo.TenantVO
import com.payloc.imob.exception.DocumentValidationException
import com.payloc.imob.exception.ItemAlreadyExistsException
import com.payloc.imob.exception.ItemNotFoundException
import com.payloc.imob.model.entity.Tenant
import com.payloc.imob.model.enumerate.PersonStatus
import com.payloc.imob.repository.TenantRepository
import com.payloc.imob.utils.ValidatorDocument
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TenantService @Autowired constructor(
    private val repository: TenantRepository
) {
    private val logger = LoggerFactory.getLogger(TenantService::class.java)

    fun create(tenant: Tenant): ResponseEntity<Any> {
        return try {
            validateTenantDocument(tenant.person.cpf)

            repository.findByPersonCpf(tenant.person.cpf).ifPresent {
                throw ItemAlreadyExistsException("Tenant with CPF ${tenant.person.cpf} already exists.")
            }

            tenant.apply {
                person.status = PersonStatus.ACTIVE
                createdAt = LocalDateTime.now()
                tenantNumber = repository.count().plus(INITIAL_ELEMENT_NUMBER).toString()
            }

            val tenantSaved = repository.save(tenant)
            logger.info("Tenant created successfully: $tenantSaved")

            ResponseEntity.ok(
                TenantVO(
                    id = tenantSaved.id,
                    tenantNumber = tenantSaved.tenantNumber,
                    name = tenantSaved.person.name,
                    cpf = tenantSaved.person.cpf,
                    status = tenantSaved.person.status
                )
            )
        } catch (ex: ItemAlreadyExistsException) {
            logger.warn("Tenant already exists: ${tenant.person.cpf}")
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                mapOf("error" to "Conflict", "message" to ex.message)
            )
        } catch (ex: DocumentValidationException) {
            logger.warn("Invalid CPF: ${tenant.person.cpf}")
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf("error" to "Invalid document", "message" to ex.message)
            )
        } catch (e: Exception) {
            logger.error("Error while creating tenant: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf("error" to "Internal server error", "details" to e.message)
            )
        }
    }

    fun findAll(): ResponseEntity<Any> {
        return try {
            val tenants = repository.findAll().map {
                TenantVO(
                    id = it.id,
                    tenantNumber = it.tenantNumber,
                    name = it.person.name,
                    cpf = it.person.cpf,
                    status = it.person.status
                )
            }
            logger.info("Retrieved ${tenants.size} tenants")
            ResponseEntity.ok(tenants)
        } catch (e: Exception) {
            logger.error("Error while fetching tenants: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf("error" to "Internal server error", "details" to e.message)
            )
        }
    }

    fun findByPersonCpf(cpf: String): ResponseEntity<Any> {
        return try {
            val tenant = repository.findByPersonCpf(cpf)
                .orElseThrow { ItemNotFoundException("Tenant not found with CPF: $cpf") }
            logger.info("Tenant retrieved successfully: $tenant")
            ResponseEntity.ok(tenant)
        } catch (ex: ItemNotFoundException) {
            logger.warn("Tenant not found: $cpf")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf("error" to "Not found", "message" to ex.message)
            )
        } catch (e: Exception) {
            logger.error("Error while fetching tenant: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf("error" to "Internal server error", "details" to e.message)
            )
        }
    }

    fun updateTenant(tenant: Tenant): ResponseEntity<Any> {
        return try {
            val existingTenant = repository.findByPersonCpf(tenant.person.cpf)
                .orElseThrow { ItemNotFoundException("Tenant not found with CPF: ${tenant.person.cpf}") }

            validateTenantDocument(tenant.person.cpf)

            existingTenant.apply {
                id = tenant.id
                person = tenant.person
                address = tenant.address
                occupation = tenant.occupation
                numberOfChildren = tenant.numberOfChildren
                minors = tenant.minors
                spouse = tenant.spouse
                participation = tenant.participation
                references = tenant.references
                goods = tenant.goods
                createdAt = tenant.createdAt
                updatedAt = LocalDateTime.now()
            }

            val updatedTenant = repository.save(existingTenant)
            logger.info("Tenant updated successfully: $updatedTenant")

            ResponseEntity.ok(updatedTenant)
        } catch (ex: ItemNotFoundException) {
            logger.warn("Tenant not found: ${tenant.person.cpf}")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf("error" to "Not found", "message" to ex.message)
            )
        } catch (ex: DocumentValidationException) {
            logger.warn("Invalid CPF: ${tenant.person.cpf}")
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf("error" to "Invalid document", "message" to ex.message)
            )
        } catch (e: Exception) {
            logger.error("Error while updating tenant: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf("error" to "Internal server error", "details" to e.message)
            )
        }
    }

    private fun validateTenantDocument(cpf: String) {
        if (!ValidatorDocument(cpf).isValid()) {
            throw DocumentValidationException("Document CPF is invalid: $cpf")
        }
    }
}
