package com.payloc.imob.repository

import com.payloc.imob.model.entity.Tenant
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TenantRepository : MongoRepository<Tenant, String>{

    fun findByPersonCpf(cpf: String): Optional<Tenant>

    fun findByTenantNumber(tenantNumber: Long?): Optional<Tenant>
}
