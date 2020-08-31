package com.xsc.mundiagua.repository.dynamodb.di

import dagger.Module
import dagger.Provides
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import javax.inject.Singleton

@Module
class DynamoDBClientProviderModule {

    @Singleton
    @Provides
    fun provideDynamoDBClient(): DynamoDbClient {
        return DynamoDbClient.builder().region(Region.EU_WEST_3).build()
    }
}