package com.payloc.imob.service

import com.payloc.imob.controller.vo.PropertyVO
import com.payloc.imob.model.entity.Person
import com.payloc.imob.model.entity.Property
import com.payloc.imob.model.enumerate.PropertyStatus
import com.payloc.imob.model.enumerate.TypeProperty
import com.payloc.imob.repository.PropertyRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.time.LocalDateTime

class PropertyServiceTest {

    private val repository: PropertyRepository = mock(PropertyRepository::class.java)
    private val service = PropertyService(repository)

    @Test
    fun `create should return ResponseEntity with PropertyVO when property is successfully created`() {
        val person = Person(
            status = null,
            cpf = "12345678900",
            name = "John Doe",
            phone = "123456789",
            email = "johndoe@example.com",
            birthDate = LocalDate.of(1990, 1, 1),
            naturalness = "City",
            nationality = "Country",
            maritalStatus = "Single",
            motherName = "Jane Doe",
            fatherName = "John Doe Sr."
        )

        val property = Property(
            propertyNumber = "1",
            typeProperty = TypeProperty.RESIDENTIAL,
            owner = person,
            status = PropertyStatus.AVAILABLE,
            value = 5000.0,
            address = mock(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            id = null
        )

        val propertyVO = PropertyVO(
            propertyNumber = "1",
            typeProperty = TypeProperty.RESIDENTIAL,
            owner = person.name,
            status = PropertyStatus.AVAILABLE,
            value = 5000.0,
            createdAt = LocalDateTime.now().toString()
        )

        `when`(repository.count()).thenReturn(0L)
        `when`(repository.save(any(Property::class.java))).thenReturn(property)

        val response = service.create(property)

        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body as PropertyVO
        assertEquals("1000001", body.propertyNumber)
        assertEquals(TypeProperty.RESIDENTIAL, body.typeProperty)
        assertEquals("John Doe", body.owner)
    }

    @Test
    fun `findAll should return ResponseEntity with list of PropertyVO when properties are retrieved`() {
        val person = Person(
            status = null,
            cpf = "12345678900",
            name = "John Doe",
            phone = "123456789",
            email = "johndoe@example.com",
            birthDate = LocalDate.of(1990, 1, 1),
            naturalness = "City",
            nationality = "Country",
            maritalStatus = "Single",
            motherName = "Jane Doe",
            fatherName = "John Doe Sr."
        )

        val property = Property(
            propertyNumber = "1",
            typeProperty = TypeProperty.RESIDENTIAL,
            owner = person,
            status = PropertyStatus.AVAILABLE,
            value = 5000.0,
            address = mock(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            id = null
        )

        `when`(repository.findAll()).thenReturn(listOf(property))

        val response = service.findAll()

        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body as List<*>
        assertEquals(1, body.size)
        val firstProperty = body[0] as PropertyVO
        assertEquals("1", firstProperty.propertyNumber)
        assertEquals(TypeProperty.RESIDENTIAL, firstProperty.typeProperty)
        assertEquals("John Doe", firstProperty.owner)
    }

    @Test
    fun `findByPropertyNumber should return ResponseEntity with PropertyVO when property is found`() {
        val person = Person(
            status = null,
            cpf = "12345678900",
            name = "John Doe",
            phone = "123456789",
            email = "johndoe@example.com",
            birthDate = LocalDate.of(1990, 1, 1),
            naturalness = "City",
            nationality = "Country",
            maritalStatus = "Single",
            motherName = "Jane Doe",
            fatherName = "John Doe Sr."
        )

        val property = Property(
            propertyNumber = "1",
            typeProperty = TypeProperty.RESIDENTIAL,
            owner = person,
            status = PropertyStatus.AVAILABLE,
            value = 5000.0,
            address = mock(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            id = null
        )

        `when`(repository.findByPropertyNumber("1")).thenReturn(listOf(property))

        val response = service.findByPropertyNumber("1")

        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body as Property
        assertEquals("1", body.propertyNumber)
        assertEquals(TypeProperty.RESIDENTIAL, body.typeProperty)
        assertEquals("John Doe", body.owner.name)
    }

    @Test
    fun `updateProperty should return ResponseEntity with updated Property when property is successfully updated`() {
        val person = Person(
            status = null,
            cpf = "12345678900",
            name = "John Doe",
            phone = "123456789",
            email = "johndoe@example.com",
            birthDate = LocalDate.of(1990, 1, 1),
            naturalness = "City",
            nationality = "Country",
            maritalStatus = "Single",
            motherName = "Jane Doe",
            fatherName = "John Doe Sr."
        )

        val existingProperty = Property(
            propertyNumber = "1",
            typeProperty = TypeProperty.RESIDENTIAL,
            owner = person,
            status = PropertyStatus.AVAILABLE,
            value = 5000.0,
            address = mock(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            id = null
        )

        val updatedProperty = existingProperty.copy(
            value = 5500.0,
            updatedAt = LocalDateTime.now()
        )

        `when`(repository.findByPropertyNumber("1")).thenReturn(listOf(existingProperty))
        `when`(repository.save(any(Property::class.java))).thenReturn(updatedProperty)

        val response = service.updateProperty(updatedProperty)

        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body as Property
        assertEquals("1", body.propertyNumber)
        assertEquals(5500.0, body.value)
    }

    @Test
    fun `findByStatus should return ResponseEntity with list of PropertyVO when properties are found`() {
        val person = Person(
            status = null,
            cpf = "12345678900",
            name = "John Doe",
            phone = "123456789",
            email = "johndoe@example.com",
            birthDate = LocalDate.of(1990, 1, 1),
            naturalness = "City",
            nationality = "Country",
            maritalStatus = "Single",
            motherName = "Jane Doe",
            fatherName = "John Doe Sr."
        )

        val property = Property(
            propertyNumber = "1",
            typeProperty = TypeProperty.RESIDENTIAL,
            owner = person,
            status = PropertyStatus.AVAILABLE,
            value = 5000.0,
            address = mock(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            id = null
        )

        `when`(repository.findByStatus("AVAILABLE")).thenReturn(listOf(property))

        val response = service.findByStatus("AVAILABLE")

        assertEquals(HttpStatus.OK, response.statusCode)
        val body = response.body as List<*>
        assertEquals(1, body.size)
        val firstProperty = body[0] as PropertyVO
        assertEquals("1", firstProperty.propertyNumber)
        assertEquals(TypeProperty.RESIDENTIAL, firstProperty.typeProperty)
        assertEquals("John Doe", firstProperty.owner)
    }

}
