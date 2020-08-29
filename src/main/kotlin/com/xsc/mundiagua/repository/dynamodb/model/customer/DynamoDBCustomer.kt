package com.xsc.mundiagua.repository.dynamodb.model.customer

import com.amazonaws.services.dynamodbv2.datamodeling.*
import com.xsc.mundiagua.repository.ID_HASH_KEY_NAME
import com.xsc.mundiagua.service.model.customer.Customer

class DynamoDBCustomer {
    @get:DynamoDBIndexHashKey(attributeName = ID_HASH_KEY_NAME, globalSecondaryIndexName = SECONDARY_INDEX_NAME)
    var keyType: String = SECONDARY_INDEX_HASH_KEY

    @get:DynamoDBIndexRangeKey(attributeName = "id", globalSecondaryIndexName = SECONDARY_INDEX_NAME)
    var id: Int? = null

    @get:DynamoDBHashKey(attributeName = "uuid")
    var uuid: String? = null

    @get:DynamoDBAttribute(attributeName = "name")
    var name: String? = null

    @get:DynamoDBAttribute(attributeName = "email")
    var email: String? = null

    @get:DynamoDBAttribute(attributeName = "internalCode")
    var internalCode: String? = null

    @get:DynamoDBAttribute(attributeName = "nationalId")
    var nationalId: String? = null

    @get:DynamoDBAttribute(attributeName = "phones")
    var phones: MutableMap<String, DynamoDBPhone>? = null

    @get:DynamoDBAttribute(attributeName = "addresses")
    var addresses: MutableMap<String, DynamoDBAddress>? = null

    companion object {
        const val PRIMARY_INDEX_HASH_KEY = "uuid"
        const val SECONDARY_INDEX_HASH_KEY = "CUSTOMER"
        const val SECONDARY_INDEX_NAME = "mundiaguaKeyType"

        fun adaptToModel(dbrecord: DynamoDBCustomer): Customer {
            return Customer(
                dbrecord.uuid,
                dbrecord.id,
                dbrecord.name ?: "",
                dbrecord.email,
                dbrecord.internalCode,
                dbrecord.nationalId,
                dbrecord.addresses?.map { DynamoDBAddress.adaptToModel(it.value) } ?: listOf(),
                dbrecord.phones?.map { DynamoDBPhone.adaptToModel(it.value) } ?: listOf()
            )
        }

        fun adaptFromModel(model: Customer): DynamoDBCustomer {
            val record = DynamoDBCustomer()
            record.id = model.id
            record.uuid = model.uuid
            record.name = model.name
            record.email = model.email
            record.internalCode = model.internalCode
            record.nationalId = model.nationalId
            record.addresses = model.addresses.map {it.id to DynamoDBAddress.adaptFromModel(it)}.toMap().toMutableMap()
            record.phones = model.phones.map { it.id to DynamoDBPhone.adaptFromModel(it) }.toMap().toMutableMap()
            return record
        }
    }
}

