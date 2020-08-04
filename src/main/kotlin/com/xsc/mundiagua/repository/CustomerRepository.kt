package com.xsc.mundiagua.repository

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.amazonaws.services.dynamodbv2.document.utils.NameMap
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.ReturnValue
import com.xsc.mundiagua.repository.model.customer.DynamoDBCustomer
import com.xsc.mundiagua.repository.model.customer.DynamoDBPhone
import java.util.*


class CustomerRepository {
    private val client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_3).build()
    private val tableName = System.getenv("CUSTOMER_TABLE")
    private val config = DynamoDBMapperConfig
        .Builder()
        .withTableNameOverride(
            DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName)
        )
        .build()
    private val partialUpadteConfig = DynamoDBMapperConfig
        .Builder()
        .withTableNameOverride(
            DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName)
        )
        .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.APPEND_SET)
        .build()
    private val mapper = DynamoDBMapper(client, config)

    fun getCustomer(uuid: String): DynamoDBCustomer? {
        return mapper.load(DynamoDBCustomer::class.java, uuid, config)
    }

    fun getCustomer(id: Int): DynamoDBCustomer? {
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
            items[0]
        }
    }

    fun saveNewCustomer(customer: DynamoDBCustomer): DynamoDBCustomer {
        customer.uuid = UUID.randomUUID().toString()
        mapper.save(customer)
        return customer
    }

    fun saveNewPhone(uuid: String, phone: DynamoDBPhone): DynamoDBPhone? {
        if (phone.id.isNullOrBlank()) {
            return null
        }

        val phoneId = phone.id!!
        val dynamodb = DynamoDB(client)
        val customerTable = dynamodb.getTable(tableName)
        val updateItemSpec = UpdateItemSpec()
            .withPrimaryKey(DynamoDBCustomer.PRIMARY_INDEX_HASH_KEY, uuid)
            .withUpdateExpression("set phones.#phoneid = :phone")
            .withNameMap(NameMap().with("#phoneid", phoneId))
            .withValueMap(ValueMap().withMap(":phone", phone.toValueMap()))
            .withReturnValues(ReturnValue.ALL_NEW);

        customerTable.updateItem(updateItemSpec)
        return phone
    }

}