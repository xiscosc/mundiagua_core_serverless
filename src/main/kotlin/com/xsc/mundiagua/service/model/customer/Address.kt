package com.xsc.mundiagua.service.model.customer

data class Address(
        val id: String,
        val alias: String,
        val address: String,
        val latitude: String?,
        val longitude: String?,
        val defaultZoneId: Int?
)