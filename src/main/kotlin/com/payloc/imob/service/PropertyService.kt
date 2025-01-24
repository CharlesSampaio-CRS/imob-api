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
import java.time.LocalDateTime

@Service
class PropertyService @Autowired constructor(
    private val repository: PropertyRepository
) {
    private val logger = LoggerFactory.getLogger(PropertyService::class.java)

    fun create(property: Property): ResponseEntity<Any> {
        return try {
            validatePropertyAddress(property)

            property.apply {
                status = PropertyStatus.AVAILABLE
                propertyNumber = repository.count().plus(INITIAL_ELEMENT_NUMBER).toString()
                createdAt = LocalDateTime.now()
            }
            val save = repository.save(property)
            val response = PropertyVO(
                propertyNumber = save.propertyNumber,
                typeProperty = save.typeProperty,
                owner = save.owner.name,
                status = save.status,
                value = save.value,
                createdAt = save.createdAt.toString()
            )
            logger.info("Property created successfully: $response")
            ResponseEntity.ok(response)
        } catch (ex: ItemAlreadyExistsException) {
            logger.warn("$ITEM_ALREADY_EXISTS ${property.address}")
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse(message = ex.message ?: "Address already exists", status = HttpStatus.CONFLICT.value())
            )
        } catch (e: Exception) {
            logger.error("Error while creating property: ${e.message}", e)
            val errorMessage = mapOf("error" to "Error while creating property", "details" to e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage)
        }
    }

    fun findAll(): ResponseEntity<Any> {
        return try {
            val properties = repository.findAll().map { property ->
                PropertyVO(
                    propertyNumber = property.propertyNumber,
                    typeProperty = property.typeProperty,
                    owner = property.owner.name,
                    status = property.status,
                    value = property.value,
                    createdAt = property.createdAt.toString()
                )
            }
            logger.info("Retrieved ${properties.size} properties")
            ResponseEntity.ok(properties)
        } catch (e: Exception) {
            logger.error("Error while fetching properties: ${e.message}", e)
            val errorMessage = mapOf("error" to "Error while fetching properties", "details" to e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage)
        }
    }

    fun findByPropertyNumber(propertyNumber: String): ResponseEntity<Any> {
        return try {
            val property = repository.findByPropertyNumber(propertyNumber).firstOrNull()
                ?: throw ItemNotFoundException("Property with number $propertyNumber not found")
            logger.info("Property retrieved successfully: $property")
            ResponseEntity.ok(property)
        } catch (ex: ItemNotFoundException) {
            logger.warn(ex.message)
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(message = ex.message ?: "Property not found", status = HttpStatus.NOT_FOUND.value())
            )
        } catch (e: Exception) {
            logger.error("Error while fetching property: ${e.message}", e)
            val errorMessage = mapOf("error" to "Error while fetching property", "details" to e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage)
        }
    }

    fun updateProperty(property: Property): ResponseEntity<Any> {
        return try {
            val existingProperty = repository.findByPropertyNumber(property.propertyNumber).firstOrNull()
                ?: throw ItemNotFoundException("Property with number ${property.propertyNumber} not found")

            existingProperty.apply {
                propertyNumber = property.propertyNumber
                typeProperty = property.typeProperty
                status = property.status
                owner = property.owner
                value = property.value
                address = property.address
                createdAt = property.createdAt
                updatedAt = LocalDateTime.now()
            }
            val updatedProperty = repository.save(existingProperty)
            logger.info("Property updated successfully: $updatedProperty")
            ResponseEntity.ok(updatedProperty)
        } catch (ex: ItemNotFoundException) {
            logger.warn(ex.message)
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(message = ex.message ?: "Property not found", status = HttpStatus.NOT_FOUND.value())
            )
        } catch (e: Exception) {
            logger.error("Error while updating property: ${e.message}", e)
            val errorMessage = mapOf("error" to "Error while updating property", "details" to e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage)
        }
    }

    fun findByStatus(status: String): ResponseEntity<Any> {
        return try {
            val properties = repository.findByStatus(status).map { property ->
                PropertyVO(
                    propertyNumber = property.propertyNumber,
                    typeProperty = property.typeProperty,
                    owner = property.owner.name,
                    status = property.status,
                    value = property.value,
                    createdAt = property.createdAt.toString()
                )
            }
            logger.info("Found ${properties.size} properties with status: $status")
            ResponseEntity.ok(properties)
        } catch (e: Exception) {
            logger.error("Error while fetching properties by status: ${e.message}", e)
            val errorMessage = mapOf("error" to "Error while fetching properties by status", "details" to e.message)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage)
        }
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
}
