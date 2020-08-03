package com.xsc.mundiagua.repository.model.customer

import com.xsc.mundiagua.model.customer.Address

class DynamoDBAddress() {
    var id: String? = null
    var alias: String? = null
    var address: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var defaultZoneId: Int? = null

    companion object {
        fun adaptToModel(dbrecord: DynamoDBAddress): Address {
            return Address(
                dbrecord.id ?: "",
                dbrecord.alias ?: "",
                dbrecord.address ?: "",
                dbrecord.latitude,
                dbrecord.longitude,
                dbrecord.defaultZoneId
            )
        }

        fun adaptToDbRecord(model: Address): DynamoDBAddress {
            val record =  DynamoDBAddress()
            record.id = model.id
            record.alias = model.alias
            record.address = model.address
            record.latitude = model.latitude
            record.longitude = model.longitude
            record.defaultZoneId = model.defaultZoneId
            return record
        }
    }
}
