package com.xsc.mundiagua.repository.dynamodb.model.customer

import com.xsc.mundiagua.service.model.customer.Address
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@DynamoDbBean
class DynamoDBAddress() {
    var id: String? = null
    var alias: String? = null
    var address: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var defaultZoneId: Int? = null

    fun toValueMap(): Map<String, AttributeValue>
    {
        return mapOf(
            "id" to AttributeValue.builder().s(id ?: "").build(),
            "alias" to AttributeValue.builder().s(alias ?: "").build(),
            "address" to AttributeValue.builder().s(address ?: "").build(),
            "latitude" to AttributeValue.builder().s(latitude ?: "").build(),
            "longitude" to AttributeValue.builder().s(longitude ?: "").build(),
            "defaultZoneId" to AttributeValue.builder().n(defaultZoneId?.toString() ?: "0").build(),
        )
    }

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

        fun adaptFromModel(model: Address): DynamoDBAddress {
            val record = DynamoDBAddress()
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
