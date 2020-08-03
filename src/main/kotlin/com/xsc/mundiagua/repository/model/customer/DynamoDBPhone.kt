package com.xsc.mundiagua.repository.model.customer

import com.xsc.mundiagua.model.customer.Phone

class DynamoDBPhone() {
    var id: String? = null
    var alias: String? = null
    var countryCode: Int? = null
    var phoneNumber: Int? = null

    companion object {
        fun adaptToModel(dbrecord: DynamoDBPhone): Phone {
            return Phone(
                dbrecord.id ?: "",
                dbrecord.alias ?: "",
                dbrecord.countryCode ?: 0,
                dbrecord.phoneNumber ?: 0
            )
        }

        fun adaptToDbRecord(model: Phone): DynamoDBPhone {
            val record =  DynamoDBPhone()
            record.id = model.id
            record.alias = model.alias
            record.countryCode = model.countryCode
            record.phoneNumber = model.phoneNumber
            return record
        }
    }
}