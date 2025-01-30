package com.payloc.imob.service

import com.payloc.imob.constants.Constants.Companion.INITIAL_ELEMENT_NUMBER
import com.payloc.imob.constants.Constants.Companion.ITEM_ALREADY_EXISTS
import com.payloc.imob.controller.vo.PropertyVO
import com.payloc.imob.exception.ErrorResponse
import com.payloc.imob.exception.ItemAlreadyExistsException
import com.payloc.imob.exception.ItemNotFoundException
import com.payloc.imob.model.entity.Property
import com.payloc.imob.model.enumerate.PropertyStatus
import com.payloc.imob.repository.PropertyRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class PropertyService @Autowired constructor(
    private val repository: PropertyRepository,
    private val awsS3Service: AwsS3Service
) {
    private val logger = LoggerFactory.getLogger(PropertyService::class.java)

    fun create(property: Property, files: List<MultipartFile>): ResponseEntity<Any> =
        try {
            validatePropertyAddress(property)
            val uploadedFiles = uploadFiles(files)
            val savedProperty = saveProperty(property, uploadedFiles)
            logger.info("Property created successfully: $savedProperty")
            ResponseEntity.ok(savedProperty)
        } catch (ex: ItemAlreadyExistsException) {
            handleItemAlreadyExistsException(property, ex)
        } catch (e: Exception) {
            handleGenericException("creating property", e)
        }

    fun findAll(): ResponseEntity<Any> =
        try {
            val properties = repository.findAll().map(::toPropertyVO)
            logger.info("Retrieved ${properties.size} properties")
            ResponseEntity.ok(properties)
        } catch (e: Exception) {
            handleGenericException("fetching properties", e)
        }

    fun findByPropertyNumber(propertyNumber: String): ResponseEntity<Any> =
        try {
            val property = repository.findByPropertyNumber(propertyNumber).firstOrNull()
                ?: throw ItemNotFoundException("Property with number $propertyNumber not found")
            logger.info("Property retrieved successfully: $property")
            ResponseEntity.ok(property)
        } catch (ex: ItemNotFoundException) {
            handleItemNotFoundException(ex)
        } catch (e: Exception) {
            handleGenericException("fetching property", e)
        }

    fun updateProperty(property: Property): ResponseEntity<Any> =
        try {
            val existingProperty = property.propertyNumber?.let { findExistingProperty(it) }
            val updatedProperty = existingProperty?.let { updateExistingProperty(it, property) }
            logger.info("Property updated successfully: $updatedProperty")
            ResponseEntity.ok(updatedProperty)
        } catch (ex: ItemNotFoundException) {
            handleItemNotFoundException(ex)
        } catch (e: Exception) {
            handleGenericException("updating property", e)
        }

    fun findByStatus(status: String): ResponseEntity<Any> =
        try {
            val properties = repository.findByStatus(status).map(::toPropertyVO)
            logger.info("Found ${properties.size} properties with status: $status")
            ResponseEntity.ok(properties)
        } catch (e: Exception) {
            handleGenericException("fetching properties by status", e)
        }

    private fun validatePropertyAddress(property: Property) {
        val existingProperty = repository.findByAddress(
            property.address.street,
            property.address.number,
            property.address.complement.orEmpty()
        )
        if (existingProperty.isNotEmpty()) {
            logger.warn("Property with the same address already exists: ${property.address}")
            throw ItemAlreadyExistsException("Property with address already exists")
        }
    }

    private fun uploadFiles(files: List<MultipartFile>): List<String> =
        files.map(awsS3Service::uploadImage)

    private fun saveProperty(property: Property, uploadedFiles: List<String>): PropertyVO {
        property.apply {
            status = PropertyStatus.AVAILABLE
            propertyNumber = repository.count().plus(INITIAL_ELEMENT_NUMBER).toString()
            createdAt = LocalDateTime.now()
            this.files = uploadedFiles
        }
        val savedProperty = repository.save(property)
        return toPropertyVO(savedProperty)
    }

    private fun toPropertyVO(property: Property): PropertyVO =
        PropertyVO(
            propertyNumber = property.propertyNumber,
            typeProperty = property.typeProperty,
            owner = property.owner.name,
            status = property.status,
            value = property.value,
            createdAt = property.createdAt.toString()
        )

    private fun findExistingProperty(propertyNumber: String): Property =
        repository.findByPropertyNumber(propertyNumber).firstOrNull()
            ?: throw ItemNotFoundException("Property with number $propertyNumber not found")

    private fun updateExistingProperty(existingProperty: Property, property: Property): Property {
        return existingProperty.apply {
            propertyNumber = property.propertyNumber
            typeProperty = property.typeProperty
            status = property.status
            owner = property.owner
            value = property.value
            address = property.address
            createdAt = property.createdAt
            updatedAt = LocalDateTime.now()
        }.let { repository.save(it) }
    }

    private fun handleItemAlreadyExistsException(property: Property, ex: ItemAlreadyExistsException): ResponseEntity<Any> {
        logger.warn("$ITEM_ALREADY_EXISTS ${property.address}")
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(message = ex.message ?: "Address already exists", status = HttpStatus.CONFLICT.value())
        )
    }

    private fun handleItemNotFoundException(ex: ItemNotFoundException): ResponseEntity<Any> {
        logger.warn(ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(message = ex.message ?: "Property not found", status = HttpStatus.NOT_FOUND.value())
        )
    }

    private fun handleGenericException(action: String, e: Exception): ResponseEntity<Any> {
        logger.error("Error while $action: ${e.message}", e)
        val errorMessage = mapOf("error" to "Error while $action", "details" to e.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage)
    }
}
