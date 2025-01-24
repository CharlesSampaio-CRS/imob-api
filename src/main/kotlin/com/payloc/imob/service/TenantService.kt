package com.payloc.imob.service

import com.payloc.imob.constants.Constants.Companion.INITIAL_ELEMENT_NUMBER
import com.payloc.imob.controller.vo.TenantVO
import com.payloc.imob.exception.DocumentValidationException
import com.payloc.imob.exception.ItemAlreadyExistsException
import com.payloc.imob.exception.ItemNotFoundException
import com.payloc.imob.model.entity.Tenant
import com.payloc.imob.model.enumerate.PersonStatus
import com.payloc.imob.repository.TenantRepository
import com.payloc.imob.util.EncryptionUtil
import com.payloc.imob.util.ValidatorDocument
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

            val cpfEncrypt = EncryptionUtil.encrypt(tenant.person.cpf)
            tenant.person.cpf = cpfEncrypt

            repository.findByPersonCpf(cpfEncrypt).ifPresent {
                throw ItemAlreadyExistsException("Tenant already exists.")
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
                    tenantNumber = tenantSaved.tenantNumber,
                    name = tenantSaved.person.name,
                    cpf = tenantSaved.person.cpf,
                    status = tenantSaved.person.status,
                    createdAt = tenantSaved.createdAt
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
                    tenantNumber = it.tenantNumber,
                    name = it.person.name,
                    cpf = EncryptionUtil.decrypt(it.person.cpf),
                    status = it.person.status,
                    createdAt = it.createdAt
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
            val encryptedCpf = EncryptionUtil.encrypt(cpf)
            val tenant = repository.findByPersonCpf(encryptedCpf)
                .orElseThrow { ItemNotFoundException("Tenant not found with CPF: $cpf") }

            logger.info("Tenant retrieved successfully: $tenant")

            tenant.person.cpf = EncryptionUtil.decrypt(tenant.person.cpf)
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
            val encryptedCpf = EncryptionUtil.encrypt(tenant.person.cpf)
            val existingTenant = repository.findByPersonCpf(encryptedCpf)
                .orElseThrow { ItemNotFoundException("Tenant not found with CPF: ${tenant.person.cpf}") }

            existingTenant.apply {
                person.cpf = encryptedCpf
                updatedAt = LocalDateTime.now()
                repository.save(existingTenant)
            }
            logger.info("Tenant updated successfully")
            ResponseEntity.ok(
                TenantVO(
                    tenantNumber = existingTenant.tenantNumber,
                    name = existingTenant.person.name,
                    cpf = tenant.person.cpf,
                    status = existingTenant.person.status,
                    createdAt = existingTenant.createdAt,
                    updatedAt = existingTenant.updatedAt
                )
            )
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
