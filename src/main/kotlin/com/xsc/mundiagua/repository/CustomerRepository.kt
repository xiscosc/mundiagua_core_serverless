package com.xsc.mundiagua.repository

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.amazonaws.services.dynamodbv2.document.utils.NameMap
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.ReturnValue
import com.xsc.mundiagua.repository.model.customer.DynamoDBAddress
import com.xsc.mundiagua.repository.model.customer.DynamoDBCustomer
import com.xsc.mundiagua.repository.model.customer.DynamoDBPhone
import com.xsc.mundiagua.service.model.customer.Address
import com.xsc.mundiagua.service.model.customer.Customer
import com.xsc.mundiagua.service.model.customer.Phone
import java.util.*


class CustomerRepository {
    private val client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_3).build()
    private val tableName = System.getenv(CUSTOMER_TABLE_NAME_ENV)
    private val config = DynamoDBMapperConfig
        .Builder()
        .withTableNameOverride(
            DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName)
        )
        .build()
    private val mapper = DynamoDBMapper(client, config)

    fun getCustomer(uuid: String): Customer? {
        val dynamoCustomer = mapper.load(DynamoDBCustomer::class.java, uuid, config) ?: return null
        return DynamoDBCustomer.adaptToModel(dynamoCustomer)
    }

    fun getCustomer(id: Int): Customer? {
        val customer = DynamoDBCustomer()
        customer.id = id
        val queryExpression= DynamoDBQueryExpression<DynamoDBCustomer>()
            .withConsistentRead(false)
            .withHashKeyValues(customer)
            .withIndexName(DynamoDBCustomer.SECONDARY_INDEX_NAME)

        val items = mapper.query(DynamoDBCustomer::class.java, queryExpression)
        return if (items.size == 0) {
            null
        } else {
            DynamoDBCustomer.adaptToModel(items[0])
        }
    }

    fun saveNewCustomer(customer: Customer): Customer {
        val dynamoCustomer = DynamoDBCustomer.adaptToDbRecord(customer)
        dynamoCustomer.uuid = UUID.randomUUID().toString()
        dynamoCustomer.id = getNewCustomerId()
        mapper.save(dynamoCustomer)
        return DynamoDBCustomer.adaptToModel(dynamoCustomer)
    }

    fun saveNewPhone(uuid: String, phone: Phone): Phone? {
        val dynamoPhone = DynamoDBPhone.adaptToDbRecord(phone)
        val phoneId = dynamoPhone.id!!
        val dynamodb = DynamoDB(client)
        val customerTable = dynamodb.getTable(tableName)
        val updateItemSpec = UpdateItemSpec()
            .withPrimaryKey(DynamoDBCustomer.PRIMARY_INDEX_HASH_KEY, uuid)
            .withUpdateExpression("set phones.#phoneid = :phone")
            .withNameMap(NameMap().with("#phoneid", phoneId))
            .withValueMap(ValueMap().withMap(":phone", dynamoPhone.toValueMap()))
            .withReturnValues(ReturnValue.ALL_NEW);

        customerTable.updateItem(updateItemSpec)
        return phone
    }

    fun saveNewAddress(uuid: String, address: Address): Address? {
        val dynamoAddress = DynamoDBAddress.adaptToDbRecord(address)
        val addressId = dynamoAddress.id!!
        val dynamodb = DynamoDB(client)
        val customerTable = dynamodb.getTable(tableName)
        val updateItemSpec = UpdateItemSpec()
            .withPrimaryKey(DynamoDBCustomer.PRIMARY_INDEX_HASH_KEY, uuid)
            .withUpdateExpression("set addresses.#addressid = :address")
            .withNameMap(NameMap().with("#addressid", addressId))
            .withValueMap(ValueMap().withMap(":address", dynamoAddress.toValueMap()))
            .withReturnValues(ReturnValue.ALL_NEW);

        customerTable.updateItem(updateItemSpec)
        return address
    }

    private fun getNewCustomerId(): Int {
        var attempts = 0
        var id: Int? = null
        while (attempts < NEW_ID_ATTEMPTS && id == null) {
            attempts++
            id = getNewId(DynamoDBCustomer.SECONDARY_INDEX_HASH_KEY, client)
        }

        return id ?: throw Exception()
    }

}