package com.xsc.mundiagua.api.dto.customer

import com.xsc.mundiagua.service.model.customer.Phone

data class RequestPhone(
        val id: String,
        val alias: String,
        val countryCode: Int,
        val phoneNumber: Int
) {
    companion object {
        fun adaptFromModel(model: Phone): RequestPhone {
            return RequestPhone(
                model.id,
                model.alias,
                model.countryCode,
                model.phoneNumber
            )
        }

        fun adaptToModel(request: RequestPhone): Phone {
            return Phone(
                request.id,
                request.alias,
                request.countryCode,
                request.phoneNumber
            )
        }
    }
}