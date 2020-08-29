package com.xsc.mundiagua.repository.dynamodb.di

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DynamoDBClientProviderModule {

    @Singleton
    @Provides
    fun provideDynamoDBClient(): AmazonDynamoDB {
        return AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_3).build()
    }
}