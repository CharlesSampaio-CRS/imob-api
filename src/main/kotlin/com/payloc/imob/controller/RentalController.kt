package com.payloc.imob.controller

import com.payloc.imob.controller.vo.RentalVO
import com.payloc.imob.model.entity.Rental
import com.payloc.imob.service.RentalService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rental")
class RentalController(
    private val service: RentalService
) {

    @PostMapping
    fun create(@RequestBody rental: RentalVO): ResponseEntity<Any> {
        return service.create(rental)
    }

    @GetMapping
    fun findAll(): ResponseEntity<Any> {
        return service.findAll()
    }

    @GetMapping("/{rentalNumber}")
    fun findByRentalNumber(@PathVariable rentalNumber: String): ResponseEntity<Any> {
       return service.findByRentalNumber(rentalNumber)
    }

    @PutMapping("/update")
    fun update(@RequestBody rental: Rental): ResponseEntity<Any> {
        return service.updateRental(rental)
    }

    @GetMapping("/status/{status}")
    fun findByStatus(@PathVariable status: String): ResponseEntity<Any> {
        return service.findByStatus(status)
    }
}
