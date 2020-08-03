package com.xsc.mundiagua.api.model.customer

import com.xsc.mundiagua.service.model.customer.Customer

data class RequestCustomer(
    val uuid: String?,
    val id: Int?,
    val name: String,
    val email: String?,
    val internalCode: String?,
    val nationalId: String?,
    val addresses: List<RequestAddress> = listOf(),
    val phones: List<RequestPhone> = listOf()
) {
    companion object {
        fun adaptFromModel(model: Customer): RequestCustomer {
            return RequestCustomer(
                model.uuid,
                model.id,
                model.name,
                model.email,
                model.internalCode,
                model.nationalId,
                model.addresses.map { RequestAddress.adaptFromModel(it) },
                model.phones.map { RequestPhone.adaptFromModel(it) }
            )
        }

        fun adaptToModel(request: RequestCustomer): Customer {
            return Customer(
                request.uuid,
                request.id,
                request.name,
                request.email,
                request.internalCode,
                request.nationalId,
                request.addresses.map { RequestAddress.adaptToModel(it) },
                request.phones.map { RequestPhone.adaptToModel(it) }
            )
        }
    }
}