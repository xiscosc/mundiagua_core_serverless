package com.xsc.mundiagua.repository.dynamodb.model.customer

import com.xsc.mundiagua.service.model.customer.Phone
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@DynamoDbBean
class DynamoDBPhone() {
    var id: String? = null
    var alias: String? = null
    var countryCode: Int? = null
    var phoneNumber: Int? = null

    fun toValueMap(): Map<String, AttributeValue>
    {
        return mapOf(
            "id" to AttributeValue.builder().s(id ?: "").build(),
            "alias" to AttributeValue.builder().s(alias ?: "").build(),
            "countryCode" to AttributeValue.builder().n(countryCode?.toString() ?: "0").build(),
            "phoneNumber" to AttributeValue.builder().n(phoneNumber?.toString() ?: "0").build(),
        )
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