package com.xsc.mundiagua.repository.dynamomodel.customer

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.xsc.mundiagua.service.model.customer.Phone

@DynamoDBDocument
class DynamoDBPhone() {
    var id: String? = null
    var alias: String? = null
    var countryCode: Int? = null
    var phoneNumber: Int? = null

    fun toValueMap(): Map<String, Any?>
    {
        return ObjectMapper().convertValue<Map<String, Any?>>(this, object: TypeReference<Map<String, Any?>>() {})
    }

    companion object {
        fun adaptToModel(dbrecord: DynamoDBPhone): Phone {
            return Phone(
                dbrecord.id ?: "",
                dbrecord.alias ?: "",
                dbrecord.countryCode ?: 0,
                dbrecord.phoneNumber ?: 0
            )
        }

        fun adaptFromModel(model: Phone): DynamoDBPhone {
            val record = DynamoDBPhone()
            record.id = model.id
            record.alias = model.alias
            record.countryCode = model.countryCode
            record.phoneNumber = model.phoneNumber
            return record
        }
    }
}