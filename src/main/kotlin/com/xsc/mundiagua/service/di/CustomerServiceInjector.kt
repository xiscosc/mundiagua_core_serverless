package com.xsc.mundiagua.service.di

import com.xsc.mundiagua.api.serverless.customer.CustomerHandler
import com.xsc.mundiagua.repository.dynamodb.di.DynamoDBClientProviderModule
import com.xsc.mundiagua.service.CustomerService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DynamoDBClientProviderModule::class])
interface CustomerServiceInjector {
    fun service(): CustomerService
    fun inject(customerHandler: CustomerHandler)
}