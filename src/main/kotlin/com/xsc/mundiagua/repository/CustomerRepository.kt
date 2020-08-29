package com.xsc.mundiagua.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.amazonaws.services.dynamodbv2.document.utils.NameMap
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition
import com.amazonaws.services.dynamodbv2.model.ReturnValue
import com.xsc.mundiagua.repository.dynamodb.id.DynamoIdManager
import com.xsc.mundiagua.repository.dynamodb.model.customer.DynamoDBAddress
import com.xsc.mundiagua.repository.dynamodb.model.customer.DynamoDBCustomer
import com.xsc.mundiagua.repository.dynamodb.model.customer.DynamoDBPhone
import com.xsc.mundiagua.service.model.customer.Address
import com.xsc.mundiagua.service.model.customer.Customer
import com.xsc.mundiagua.service.model.customer.Phone
import java.util.*
import javax.inject.Inject

class CustomerRepository @Inject constructor(
    private val client: AmazonDynamoDB,
    private val idManager: DynamoIdManager
): RepositoryInterface<Customer> {
    private val tableName = System.getenv(CUSTOMER_TABLE_NAME_ENV)
    private val config = DynamoDBMapperConfig
        .Builder()
        .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(tableName))
        .build()
    private val mapper = DynamoDBMapper(client, config)

    override fun getByUUID(uuid: String): Customer? {
        val dynamoCustomer = mapper.load(DynamoDBCustomer::class.java, uuid, config) ?: return null
        return DynamoDBCustomer.adaptToModel(dynamoCustomer)
    }

    override fun getById(id: Int): Customer? {
        val customer = DynamoDBCustomer()
        customer.id = id
        val queryExpression= DynamoDBQueryExpression<DynamoDBCustomer>()
            .withConsistentRead(false)
            .withHashKeyValues(customer)
            .withIndexName(DynamoDBCustomer.SECONDARY_INDEX_NAME)

        val items = mapper.query(DynamoDBCustomer::class.java, queryExpression)
        return if (items.size != 1) {
            null
        } else {
            DynamoDBCustomer.adaptToModel(items[0])
        }
    }

    override fun getList(scanForward: Boolean, lastEvaluatedKey: String?, limit: Int): List<Customer> {
        val queryExpression= DynamoDBQueryExpression<DynamoDBCustomer>()
            .withConsistentRead(false)
            .withScanIndexForward(scanForward)
            .withIndexName(DynamoDBCustomer.SECONDARY_INDEX_NAME)
            .withHashKeyValues(DynamoDBCustomer())
            .withLimit(limit)

        if (!(lastEvaluatedKey.isNullOrBlank() || lastEvaluatedKey.isNullOrEmpty())) {
            val operator = if (scanForward) ComparisonOperator.GT else ComparisonOperator.LT
            val condition = Condition()
                .withComparisonOperator(operator)
                .withAttributeValueList(AttributeValue().withN(lastEvaluatedKey))
            queryExpression.withRangeKeyCondition("id", condition)
        }

        val queryResult = mapper.queryPage(DynamoDBCustomer::class.java, queryExpression)
        return queryResult.results.map { DynamoDBCustomer.adaptToModel(it) }
    }

    override fun save(model: Customer): Customer {
        val dynamoCustomer = DynamoDBCustomer.adaptFromModel(model)
        dynamoCustomer.uuid = UUID.randomUUID().toString()
        dynamoCustomer.id = getNewCustomerId()
        mapper.save(dynamoCustomer)
        return DynamoDBCustomer.adaptToModel(dynamoCustomer)
    }

    fun saveNewPhone(uuid: String, phone: Phone): Phone? {
        val dynamoPhone = DynamoDBPhone.adaptFromModel(phone)
        val phoneId = dynamoPhone.id!!
        val dynamodb = DynamoDB(client)
        val customerTable = dynamodb.getTable(tableName)
        val updateItemSpec = UpdateItemSpec()
            .withPrimaryKey(DynamoDBCustomer.PRIMARY_INDEX_HASH_KEY, uuid)
            .withUpdateExpression("set phones.#phoneid = :phone")
            .withNameMap(NameMap().with("#phoneid", phoneId))
            .withValueMap(ValueMap().withMap(":phone", dynamoPhone.toValueMap()))
            .withReturnValues(ReturnValue.ALL_NEW)

        customerTable.updateItem(updateItemSpec)
        return phone
    }

    fun saveNewAddress(uuid: String, address: Address): Address? {
        val dynamoAddress = DynamoDBAddress.adaptFromModel(address)
        val addressId = dynamoAddress.id!!
        val dynamodb = DynamoDB(client)
        val customerTable = dynamodb.getTable(tableName)
        val updateItemSpec = UpdateItemSpec()
            .withPrimaryKey(DynamoDBCustomer.PRIMARY_INDEX_HASH_KEY, uuid)
            .withUpdateExpression("set addresses.#addressid = :address")
            .withNameMap(NameMap().with("#addressid", addressId))
            .withValueMap(ValueMap().withMap(":address", dynamoAddress.toValueMap()))
            .withReturnValues(ReturnValue.ALL_NEW)

        customerTable.updateItem(updateItemSpec)
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