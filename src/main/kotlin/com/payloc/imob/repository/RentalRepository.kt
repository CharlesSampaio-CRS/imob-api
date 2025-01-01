package com.payloc.imob.repository

import com.payloc.imob.model.entity.Rental
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RentalRepository : MongoRepository<Rental, ObjectId>{
    fun findByRentalNumber(rentalNumber: String?): Optional<Rental>

    fun findByStatus(status: String): List<Rental>
}
