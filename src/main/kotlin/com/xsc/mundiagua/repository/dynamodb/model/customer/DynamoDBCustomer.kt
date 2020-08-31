package com.xsc.mundiagua.repository.dynamodb.model.customer

import com.xsc.mundiagua.service.model.customer.Customer
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey

@DynamoDbBean
class DynamoDBCustomer {
    @get:DynamoDbSecondaryPartitionKey(indexNames = [SECONDARY_INDEX_NAME])
    var keyType: String = SECONDARY_INDEX_HASH_KEY

    @get:DynamoDbSecondarySortKey(indexNames = [SECONDARY_INDEX_NAME])
    var id: Int? = null

    @get:DynamoDbPartitionKey
    var uuid: String? = null

    var name: String? = null

    var email: String? = null

    var internalCode: String? = null

    var nationalId: String? = null

    var phones: MutableMap<String, DynamoDBPhone>? = null

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

