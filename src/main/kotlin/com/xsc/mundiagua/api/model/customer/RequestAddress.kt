package com.xsc.mundiagua.api.model.customer

import com.xsc.mundiagua.model.customer.Address

data class RequestAddress(
        val id: String,
        val alias: String,
        val address: String,
        val latitude: String?,
        val longitude: String?,
        val defaultZoneId: Int?
) {
    companion object {
        fun adaptFromModel(model: Address): RequestAddress {
            return RequestAddress(
                model.id,
                model.alias,
                model.address,
                model.latitude,
                model.longitude,
                model.defaultZoneId
            )
        }

        fun adaptToModel(request: RequestAddress): Address {
            return Address(
                request.id,
                request.alias,
                request.address,
                request.latitude,
                request.longitude,
                request.defaultZoneId
            )
        }
    }
}