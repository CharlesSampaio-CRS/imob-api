package com.payloc.imob.repository

import com.payloc.imob.model.entity.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository: MongoRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
}