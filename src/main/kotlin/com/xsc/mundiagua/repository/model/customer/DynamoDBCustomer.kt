package com.xsc.mundiagua.repository.model.customer

import com.amazonaws.services.dynamodbv2.datamodeling.*
import com.xsc.mundiagua.service.model.customer.Customer
import com.xsc.mundiagua.repository.dynamodbconverter.DynamoDBAddressTypedConverter
import com.xsc.mundiagua.repository.dynamodbconverter.DynamoDBPhoneTypedConverter

class DynamoDBCustomer {
    @get:DynamoDBIndexHashKey(attributeName = "key", globalSecondaryIndexName = SECONDARY_UUID_INDEX_NAME)
    var key: String = PARTITION_KEY

    @get:DynamoDBIndexRangeKey(attributeName = "id", globalSecondaryIndexName = SECONDARY_UUID_INDEX_NAME)
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

    @get:DynamoDBTypeConverted(converter = DynamoDBPhoneTypedConverter::class)
    @get:DynamoDBAttribute(attributeName = "phones")
    var phones: MutableMap<String, DynamoDBPhone>? = mutableMapOf()

    @get:DynamoDBTypeConverted(converter = DynamoDBAddressTypedConverter::class)
    @get:DynamoDBAttribute(attributeName = "addresses")
    var addresses: MutableMap<String, DynamoDBAddress>? = mutableMapOf()

    companion object {
        const val PARTITION_KEY = "CUSTOMER"
        const val SECONDARY_UUID_INDEX_NAME ="mundiaguaId"

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

        fun adaptToDbRecord(model: Customer): DynamoDBCustomer {
            val record = DynamoDBCustomer()
            record.id = model.id
            record.uuid = model.uuid
            record.name = model.name
            record.email = model.email
            record.internalCode = model.internalCode
            record.nationalId = model.nationalId
            record.addresses = model.addresses.map { it.id to DynamoDBAddress.adaptToDbRecord(it) }.toMap().toMutableMap()
            record.phones = model.phones.map { it.id to DynamoDBPhone.adaptToDbRecord(it) }.toMap().toMutableMap()
            return record
        }
    }
}

