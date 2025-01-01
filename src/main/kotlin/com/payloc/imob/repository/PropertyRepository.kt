package com.payloc.imob.repository

import com.payloc.imob.model.entity.Property
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PropertyRepository : MongoRepository<Property, String>{

    @Query("{ 'address.street': ?0, 'address.number': ?1, 'address.complement': ?2 }")
    fun findByAddress(street: String, number: String, complement: String): List<Property>

    fun findByPropertyNumber(propertyNumber: Long): List<Property>

    fun findByStatus(status: String): List<Property>
}
