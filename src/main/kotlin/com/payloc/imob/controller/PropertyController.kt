package com.payloc.imob.controller

import com.payloc.imob.model.entity.Property
import com.payloc.imob.service.PropertyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/property")
class PropertyController(
    private val service: PropertyService
) {

    @PostMapping
    fun create(@RequestBody property: Property): ResponseEntity<Any> {
        return service.create(property)
    }

    @GetMapping
    fun findall(): ResponseEntity<Any> {
        return service.findAll()
    }

    @GetMapping("/{propertyNumber}")
    fun findByPropertyNumber(@PathVariable propertyNumber: Long): ResponseEntity<Any> {
       return service.findByPropertyNumber(propertyNumber)
    }

    @PutMapping("/update")
    fun update(@RequestBody property: Property): ResponseEntity<Any> {
        return service.updateProperty(property)
    }

    @GetMapping("/status/{status}")
    fun findByStatus(@PathVariable status: String): ResponseEntity<Any> {
        return service.findByStatus(status)
    }
}
