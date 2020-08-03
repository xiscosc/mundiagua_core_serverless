package com.xsc.mundiagua.service.model.customer

data class Customer(
        val uuid: String?,
        val id: Int?,
        val name: String,
        val email: String?,
        val internalCode: String?,
        val nationalId: String?,
        val addresses: List<Address>,
        val phones: List<Phone>
)