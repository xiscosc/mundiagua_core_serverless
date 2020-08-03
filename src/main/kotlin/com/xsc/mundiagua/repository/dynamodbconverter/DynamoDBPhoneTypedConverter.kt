package com.xsc.mundiagua.repository.dynamodbconverter

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.xsc.mundiagua.repository.model.customer.DynamoDBPhone

class DynamoDBPhoneTypedConverter: DynamoDBTypeConverter<String, MutableMap<String, DynamoDBPhone>> {
    override fun unconvert(`object`: String?): MutableMap<String, DynamoDBPhone> {
        if (`object`.isNullOrBlank()) {
            return mutableMapOf()
        }

        val mapper = ObjectMapper()
        val factory = TypeFactory.defaultInstance();
        val type = factory.constructMapType(MutableMap::class.java, String::class.java, DynamoDBPhone::class.java)
        return  mapper.readValue<MutableMap<String, DynamoDBPhone>>(`object`, type);
    }

    override fun convert(`object`: MutableMap<String, DynamoDBPhone>?): String {
        if (`object`.isNullOrEmpty()) {
            return "{}"
        }

        val mapper = ObjectMapper()
        return mapper.writeValueAsString(`object`)
    }
}