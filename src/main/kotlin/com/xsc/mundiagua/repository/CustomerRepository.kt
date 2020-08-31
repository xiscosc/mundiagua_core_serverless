package com.xsc.mundiagua.repository

import com.xsc.mundiagua.repository.dynamodb.id.DynamoIdManager
import com.xsc.mundiagua.repository.dynamodb.model.customer.DynamoDBAddress
import com.xsc.mundiagua.repository.dynamodb.model.customer.DynamoDBCustomer
import com.xsc.mundiagua.repository.dynamodb.model.customer.DynamoDBPhone
import com.xsc.mundiagua.service.model.customer.Address
import com.xsc.mundiagua.service.model.customer.Customer
import com.xsc.mundiagua.service.model.customer.Phone
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import java.util.*
import javax.inject.Inject


class CustomerRepository @Inject constructor(
    private val client: DynamoDbClient,
    private val idManager: DynamoIdManager
): RepositoryInterface<Customer> {
    private val tableName = System.getenv(CUSTOMER_TABLE_NAME_ENV)
    var enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build()
    val customerTableSchema = TableSchema.fromBean(DynamoDBCustomer::class.java)
    var customerTable = enhancedClient.table(tableName, customerTableSchema)


    override fun getByUUID(uuid: String): Customer? {
        val dynamoCustomer = customerTable.getItem(Key.builder().partitionValue(uuid).build());
        return DynamoDBCustomer.adaptToModel(dynamoCustomer)
    }

    override fun getById(id: Int): Customer? {
        val index = customerTable.index(DynamoDBCustomer.SECONDARY_INDEX_NAME)
        val key = Key.builder().partitionValue(DynamoDBCustomer.SECONDARY_INDEX_HASH_KEY).sortValue(id).build()
        val queryConditional = QueryConditional.keyEqualTo(key)
        val queryEnhancedRequest = QueryEnhancedRequest.builder().queryConditional(queryConditional).limit(1).build()
        val iterator = index.query(queryEnhancedRequest).iterator()

        if (iterator.hasNext()) {
            val page = iterator.next()
            if (page.items().size == 0) {
                return null
            }

            return DynamoDBCustomer.adaptToModel(page.items()[0])
        }

        return null
    }

    override fun getList(scanForward: Boolean, lastEvaluatedKey: Int?, limit: Int): List<Customer> {
        val index = customerTable.index(DynamoDBCustomer.SECONDARY_INDEX_NAME)
        val queryEnhancedRequestBuilder = QueryEnhancedRequest
            .builder()
            .limit(limit)
            .scanIndexForward(scanForward)

        if (lastEvaluatedKey != null) {
            val key = Key
                .builder()
                .partitionValue(DynamoDBCustomer.SECONDARY_INDEX_HASH_KEY)
                .sortValue(lastEvaluatedKey)
                .build()
            val queryConditional = if (scanForward) {
                QueryConditional.sortGreaterThan(key)
            } else {
                QueryConditional.sortLessThan(key)
            }
            queryEnhancedRequestBuilder.queryConditional(queryConditional)
        } else {
            val key = Key.builder().partitionValue(DynamoDBCustomer.SECONDARY_INDEX_HASH_KEY).build()
            val queryConditional = QueryConditional.keyEqualTo(key)
            queryEnhancedRequestBuilder.queryConditional(queryConditional)
        }

        val queryEnhancedRequest = queryEnhancedRequestBuilder.build()
        val iterator = index.query(queryEnhancedRequest).iterator()
        val customers = mutableListOf<DynamoDBCustomer>()
        while (iterator.hasNext()) customers.addAll(iterator.next().items())
        return customers.map { DynamoDBCustomer.adaptToModel(it) }
    }

    override fun save(model: Customer): Customer {
        val dynamoCustomer = DynamoDBCustomer.adaptFromModel(model)
        dynamoCustomer.uuid = UUID.randomUUID().toString()
        dynamoCustomer.id = getNewCustomerId()
        customerTable.putItem(dynamoCustomer)
        return DynamoDBCustomer.adaptToModel(dynamoCustomer)
    }

    fun saveNewPhone(uuid: String, phone: Phone): Phone? {
        val dynamoPhone = DynamoDBPhone.adaptFromModel(phone)
        val request = UpdateItemRequest.builder()
            .tableName(tableName)
            .updateExpression("set phones.#phoneid = :phone")
            .key(mapOf("uuid" to AttributeValue.builder().s(uuid).build()))
            .expressionAttributeNames(mapOf("#phoneid" to dynamoPhone.id!!))
            .expressionAttributeValues(mapOf(":phone" to AttributeValue.builder().m(dynamoPhone.toValueMap()).build()))
            .build()

        client.updateItem(request)
        return phone
    }

    fun saveNewAddress(uuid: String, address: Address): Address? {
        val dynamoAddress = DynamoDBAddress.adaptFromModel(address)
        val request = UpdateItemRequest.builder()
            .tableName(tableName)
            .updateExpression("set addresses.#addressid = :address")
            .key(mapOf("uuid" to AttributeValue.builder().s(uuid).build()))
            .expressionAttributeNames(mapOf("#addressid" to dynamoAddress.id!!))
            .expressionAttributeValues(mapOf(":address" to AttributeValue.builder().m(dynamoAddress.toValueMap()).build()))
            .build()

        client.updateItem(request)
        return address
    }

    private fun getNewCustomerId(): Int {
        var attempts = 0
        var id: Int? = null
        while (attempts < NEW_ID_ATTEMPTS && id == null) {
            attempts++
            id = idManager.getNewId(DynamoDBCustomer.SECONDARY_INDEX_HASH_KEY)
        }

        return id ?: throw Exception()
    }

}