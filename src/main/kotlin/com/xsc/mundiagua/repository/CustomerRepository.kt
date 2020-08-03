package com.xsc.mundiagua.repository

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.xsc.mundiagua.repository.model.customer.DynamoDBCustomer
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
            .withIndexName(DynamoDBCustomer.SECONDARY_UUID_INDEX_NAME)

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

}