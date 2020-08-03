package com.xsc.mundiagua.repository.dynamodbconverter

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.xsc.mundiagua.repository.model.customer.DynamoDBAddress

class DynamoDBAddressTypedConverter: DynamoDBTypeConverter<String, MutableMap<String, DynamoDBAddress>> {
    override fun unconvert(`object`: String?): MutableMap<String, DynamoDBAddress> {
        if (`object`.isNullOrBlank()) {
            return mutableMapOf()
        }

        val mapper = ObjectMapper()
        val factory = TypeFactory.defaultInstance();
        val type = factory.constructMapType(MutableMap::class.java, String::class.java, DynamoDBAddress::class.java)
        return  mapper.readValue<MutableMap<String, DynamoDBAddress>>(`object`, type);
    }

    override fun convert(`object`: MutableMap<String, DynamoDBAddress>?): String {
        if (`object`.isNullOrEmpty()) {
            return "{}"
        }

        val mapper = ObjectMapper()
        return mapper.writeValueAsString(`object`)
    }
}