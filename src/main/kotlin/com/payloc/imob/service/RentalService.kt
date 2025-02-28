package com.payloc.imob.service

import com.payloc.imob.constants.Constants.Companion.INITIAL_ELEMENT_NUMBER
import com.payloc.imob.model.dto.RentalDTO
import com.payloc.imob.exception.ItemAlreadyExistsException
import com.payloc.imob.exception.ItemNotFoundException
import com.payloc.imob.model.entity.Rental
import com.payloc.imob.model.enumerate.PropertyStatus
import com.payloc.imob.model.enumerate.RentalStatus
import com.payloc.imob.repository.PropertyRepository
import com.payloc.imob.repository.RentalRepository
import com.payloc.imob.repository.TenantRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RentalService @Autowired constructor(
    private val rentalRepository: RentalRepository,
    private val propertyRepository: PropertyRepository,
    private val tenantRepository: TenantRepository
) {
    private val logger = LoggerFactory.getLogger(RentalService::class.java)

    fun create(rentalVo: RentalDTO): ResponseEntity<Any> {
        return try {
            val property = propertyRepository.findByPropertyNumber(rentalVo.propertyNumber).firstOrNull()
                ?: throw IllegalStateException("Property not found.")

            val tenant = tenantRepository.findByTenantNumber(rentalVo.tenantNumber)
                .orElseThrow { ItemNotFoundException("Tenant not found.") }

            if (property.status != PropertyStatus.AVAILABLE) {
                throw ItemAlreadyExistsException("Property is not available for rental.")
            }

            property.apply {
                status = PropertyStatus.RENTED
                updatedAt = LocalDateTime.now()
            }

            val rental = Rental(
                rentalNumber = rentalRepository.count().plus(INITIAL_ELEMENT_NUMBER).toString(),
                tenant = tenant,
                property = property,
                rentalValue = rentalVo.rentalValue,
                inputValue = rentalVo.inputValue,
                adminValue = rentalVo.adminValue,
                dueDate = rentalVo.dueDate,
                paid = rentalVo.paid,
                initDate = rentalVo.initDate,
                finalDate = rentalVo.finalDate,
                typeWarranty = rentalVo.typeWarranty,
                status = RentalStatus.ACTIVE,
                createdAt = LocalDateTime.now(),
                updatedAt = null,
                id = null
            )

            tenant.apply {
                updatedAt = LocalDateTime.now()
                rentalNumber = rentalNumber?.plus(rental.rentalNumber.toString())
            }

            propertyRepository.save(property)
            tenantRepository.save(tenant)
            rentalRepository.save(rental)

            logger.info("Rental created successfully: $rental")
            ResponseEntity.ok(rentalVo)
        } catch (ex: ItemAlreadyExistsException) {
            logger.warn("Property is not available for rental: ${rentalVo.propertyNumber}")
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                mapOf("error" to "Conflict", "message" to ex.message)
            )
        } catch (e: Exception) {
            logger.error("Error while creating rental: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf("error" to "Internal server error", "details" to e.message)
            )
        }
    }

    fun findAll(): ResponseEntity<Any> {
        return try {
            val rentals = rentalRepository.findAll().map { rental ->
                RentalDTO(
                    rentalNumber = rental.rentalNumber,
                    tenantNumber = rental.tenant.tenantNumber,
                    tenantName = rental.tenant.person.name,
                    propertyNumber = rental.property.propertyNumber,
                    rentalValue = rental.rentalValue,
                    inputValue = rental.inputValue,
                    adminValue = rental.adminValue,
                    dueDate = rental.dueDate,
                    paid = rental.paid,
                    initDate = rental.initDate,
                    finalDate = rental.finalDate,
                    typeWarranty = rental.typeWarranty,
                    status = rental.status,
                    createdAt = rental.createdAt,
                    updatedAt = rental.updatedAt
                )
            }
            logger.info("Retrieved ${rentals.size} rentals")
            ResponseEntity.ok(rentals)
        } catch (e: Exception) {
            logger.error("Error while fetching rentals: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf("error" to "Internal server error", "details" to e.message)
            )
        }
    }

    fun findByRentalNumber(rentalNumber: String?): ResponseEntity<Any> {
        return try {
            val rental = rentalRepository.findByRentalNumber(rentalNumber)
                .orElseThrow { ItemNotFoundException("Rental not found.") }
            logger.info("Rental retrieved successfully: $rental")
            ResponseEntity.ok(rental)
        } catch (ex: ItemNotFoundException) {
            logger.warn("Rental not found: $rentalNumber")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf("error" to "Not found", "message" to ex.message)
            )
        } catch (e: Exception) {
            logger.error("Error while fetching rental: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf("error" to "Internal server error", "details" to e.message)
            )
        }
    }

    fun updateRental(rental: Rental): ResponseEntity<Any> {
        return try {
            val existingRental = rentalRepository.findByRentalNumber(rental.rentalNumber)
                .orElseThrow { ItemNotFoundException("Rental not found.") }

            val propertyDb = propertyRepository.findByPropertyNumber(rental.property.propertyNumber).firstOrNull()
                ?: throw IllegalStateException("Property not found.")

            val tenantDb = tenantRepository.findByTenantNumber(rental.tenant.tenantNumber)
                .orElseThrow { ItemNotFoundException("Tenant not found.") }

            existingRental.apply {
                id = existingRental.id
                rentalNumber = rental.rentalNumber
                property = propertyDb
                tenant = tenantDb
                rentalValue = rental.rentalValue
                createdAt = rental.createdAt
                updatedAt = LocalDateTime.now()
            }

            rentalRepository.save(existingRental)
            logger.info("Rental updated successfully: $existingRental")

            ResponseEntity.ok(
                RentalDTO(
                    rentalNumber = existingRental.rentalNumber,
                    tenantNumber = existingRental.tenant.tenantNumber,
                    tenantName = rental.tenant.person.name,
                    propertyNumber = existingRental.property.propertyNumber,
                    rentalValue = existingRental.rentalValue,
                    inputValue = existingRental.inputValue,
                    adminValue = existingRental.adminValue,
                    dueDate = existingRental.dueDate,
                    paid = existingRental.paid,
                    initDate = existingRental.initDate,
                    finalDate = existingRental.finalDate,
                    typeWarranty = existingRental.typeWarranty,
                    status = existingRental.status,
                    createdAt = existingRental.createdAt,
                    updatedAt = existingRental.updatedAt
                )
            )
        } catch (ex: ItemNotFoundException) {
            logger.warn(ex.message)
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf("error" to "Not found", "message" to ex.message)
            )
        } catch (e: Exception) {
            logger.error("Error while updating rental: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf("error" to "Internal server error", "details" to e.message)
            )
        }
    }

    fun findByStatus(status: String): ResponseEntity<Any> {
        return try {
            val rentals = rentalRepository.findByStatus(status).map { rental ->
                RentalDTO(
                    rentalNumber = rental.rentalNumber,
                    tenantNumber = rental.tenant.tenantNumber,
                    tenantName = rental.tenant.person.name,
                    propertyNumber = rental.property.propertyNumber,
                    rentalValue = rental.rentalValue,
                    inputValue = rental.inputValue,
                    adminValue = rental.adminValue,
                    dueDate = rental.dueDate,
                    paid = rental.paid,
                    initDate = rental.initDate,
                    finalDate = rental.finalDate,
                    typeWarranty = rental.typeWarranty,
                    status = rental.status,
                    createdAt = rental.createdAt,
                    updatedAt = rental.updatedAt
                )
            }
            logger.info("Found ${rentals.size} rentals with status: $status")
            ResponseEntity.ok(rentals)
        } catch (e: Exception) {
            logger.error("Error while fetching rentals by status: ${e.message}", e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                mapOf("error" to "Internal server error", "details" to e.message)
            )
        }
    }
}
