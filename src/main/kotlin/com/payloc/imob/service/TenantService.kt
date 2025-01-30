package com.payloc.imob.service

import com.payloc.imob.constants.Constants.Companion.INITIAL_ELEMENT_NUMBER
import com.payloc.imob.constants.Constants.Companion.ITEM_NOT_FOUND
import com.payloc.imob.controller.vo.TenantVO
import com.payloc.imob.exception.DocumentValidationException
import com.payloc.imob.exception.ItemAlreadyExistsException
import com.payloc.imob.exception.ItemNotFoundException
import com.payloc.imob.model.entity.Tenant
import com.payloc.imob.model.enumerate.PersonStatus
import com.payloc.imob.repository.TenantRepository
import com.payloc.imob.util.EncryptionUtil
import com.payloc.imob.util.ValidatorDocumentUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class TenantService @Autowired constructor(
    private val repository: TenantRepository,
    private val awsS3Service: AwsS3Service
) {
    private val logger = LoggerFactory.getLogger(TenantService::class.java)

    fun create(tenant: Tenant, files: List<MultipartFile>): ResponseEntity<Any> {
        return try {
            validateAndEncryptTenantCpf(tenant)
            checkIfTenantExists(tenant.person.cpf)
            val uploadedFiles = uploadFiles(files)
            populateTenantDetails(tenant, uploadedFiles)
            val tenantSaved = saveTenant(tenant)
            buildSuccessResponse(tenantSaved)
        } catch (ex: ItemAlreadyExistsException) {
            buildErrorResponse(HttpStatus.CONFLICT, "Conflict", ex.message)
        } catch (ex: DocumentValidationException) {
            buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid document", ex.message)
        } catch (e: Exception) {
            buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                e.message
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
            buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                e.message
            )
        }
    }

    fun findByPersonCpf(cpf: String): ResponseEntity<Any> {
        return try {
            val encryptedCpf = EncryptionUtil.encrypt(cpf)
            val tenant = repository.findByPersonCpf(encryptedCpf)
                .orElseThrow { ItemNotFoundException(ITEM_NOT_FOUND) }
            tenant.person.cpf = EncryptionUtil.decrypt(tenant.person.cpf)
            logger.info("Tenant retrieved successfully")
            ResponseEntity.ok(tenant)
        } catch (ex: ItemNotFoundException) {
            logger.warn(ITEM_NOT_FOUND)
            buildErrorResponse(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND, ex.message)
        } catch (e: Exception) {
            logger.error("Error while fetching tenant: ${e.message}", e)
            buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e.message)
        }
    }

    fun updateTenant(tenant: Tenant): ResponseEntity<Any> {
        return try {
            val encryptedCpf = EncryptionUtil.encrypt(tenant.person.cpf)
            val existingTenant = repository.findByPersonCpf(encryptedCpf)
                .orElseThrow { ItemNotFoundException(ITEM_NOT_FOUND) }

            existingTenant.apply {
                person.cpf = encryptedCpf
                updatedAt = LocalDateTime.now()
                repository.save(existingTenant)
            }
            logger.info("Tenant updated successfully")
            buildSuccessResponse(existingTenant)
        } catch (ex: ItemNotFoundException) {
            logger.warn("Tenant not found:")
            buildErrorResponse(HttpStatus.NOT_FOUND, "Not found", ex.message)
        } catch (ex: DocumentValidationException) {
            logger.warn("Invalid CPF")
            buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid document", ex.message)
        } catch (e: Exception) {
            logger.error("Error while updating tenant: ${e.message}", e)
            buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e.message)
        }
    }

    private fun validateTenantDocument(cpf: String) {
        if (!ValidatorDocumentUtil(cpf).isValid()) {
            throw DocumentValidationException("Document CPF is invalid")
        }
    }

    private fun validateAndEncryptTenantCpf(tenant: Tenant) {
        validateTenantDocument(tenant.person.cpf)
        tenant.person.cpf = EncryptionUtil.encrypt(tenant.person.cpf)
    }

    private fun checkIfTenantExists(cpf: String) {
        repository.findByPersonCpf(cpf).ifPresent {
            throw ItemAlreadyExistsException("Tenant already exists")
        }
    }

    private fun uploadFiles(files: List<MultipartFile>): List<String> {
        return files.map { file -> awsS3Service.uploadImage(file) }
    }

    private fun populateTenantDetails(tenant: Tenant, uploadedFiles: List<String>) {
        tenant.apply {
            person.status = PersonStatus.ACTIVE
            createdAt = LocalDateTime.now()
            tenantNumber = repository.count().plus(INITIAL_ELEMENT_NUMBER).toString()
            this.files = uploadedFiles
        }
    }

    private fun saveTenant(tenant: Tenant): Tenant {
        return repository.save(tenant).also {
            logger.info("Tenant created successfully")
        }
    }

    private fun buildSuccessResponse(tenant: Tenant): ResponseEntity<Any> {
        return ResponseEntity.ok(
            TenantVO(
                tenantNumber = tenant.tenantNumber,
                name = tenant.person.name,
                cpf = tenant.person.cpf,
                status = tenant.person.status,
                createdAt = tenant.createdAt,
                updatedAt = tenant.updatedAt
            )
        )
    }

    private fun buildErrorResponse(status: HttpStatus, error: String, message: String?): ResponseEntity<Any> {
        logger.warn("$error: $message")
        return ResponseEntity.status(status).body(
            mapOf("error" to error, "message" to message)
        )
    }
}