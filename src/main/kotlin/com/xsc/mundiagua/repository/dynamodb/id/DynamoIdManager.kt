package com.xsc.mundiagua.repository.dynamodb.id

import com.xsc.mundiagua.repository.ID_HASH_KEY_NAME
import com.xsc.mundiagua.repository.ID_TABLE_NAME_ENV
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest
import javax.inject.Inject


class DynamoIdManager @Inject constructor(private val client: DynamoDbClient) {
    private val tableName = System.getenv(ID_TABLE_NAME_ENV)

    fun getNewId(hashKey: String): Int? {
        return try {
            val lastId = getLastId(hashKey)
            val newId = lastId + 1
            val request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(mapOf(ID_HASH_KEY_NAME to AttributeValue.builder().s(hashKey).build()))
                .updateExpression("set #p = :val1")
                .conditionExpression("#p = :val2")
                .expressionAttributeNames(mapOf("#p" to "id"))
                .expressionAttributeValues(mapOf(
                    ":val1" to AttributeValue.builder().n(newId.toString()).build(),
                    ":val2" to AttributeValue.builder().n(lastId.toString()).build()
                ))
                .build()

            client.updateItem(request)
            newId
        } catch (e: ConditionalCheckFailedException) {
            null
        }
    }

    private fun getLastId(hashKey: String): Int {
        val request = GetItemRequest.builder()
            .key(mapOf(ID_HASH_KEY_NAME to AttributeValue.builder().s(hashKey).build()))
            .tableName(tableName)
            .build()

        val item = client.getItem(request).item();
        return item["id"]!!.n().toInt()
    }
}