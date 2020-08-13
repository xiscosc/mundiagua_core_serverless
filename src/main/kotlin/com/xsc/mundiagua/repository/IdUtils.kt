package com.xsc.mundiagua.repository

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec
import com.amazonaws.services.dynamodbv2.document.utils.NameMap
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException
import com.amazonaws.services.dynamodbv2.model.ReturnValue


fun getNewId(hashKey: String, dynamoDBClient: AmazonDynamoDB): Int? {
    val dynamoDB = DynamoDB(dynamoDBClient);
    val tableName = System.getenv(ID_TABLE_NAME_ENV)
    val table = dynamoDB.getTable(tableName)
    return try {
        val lastId = getLastId(table, hashKey)
        val newId = lastId + 1
        val updateItemSpec = UpdateItemSpec().withPrimaryKey(ID_HASH_KEY_NAME, hashKey)
            .withReturnValues(ReturnValue.ALL_NEW).withUpdateExpression("set #p = :val1")
            .withConditionExpression("#p = :val2").withNameMap(NameMap().with("#p", "id"))
            .withValueMap(ValueMap().withNumber(":val1", newId).withNumber(":val2", lastId))
        table.updateItem(updateItemSpec);
        newId
    } catch (e: ConditionalCheckFailedException) {
        null
    }
}

private fun getLastId(table: Table, hashKey: String): Int {
    val item = table.getItem(ID_HASH_KEY_NAME, hashKey, "id", null)
    return item.getNumber("id").toInt()
}