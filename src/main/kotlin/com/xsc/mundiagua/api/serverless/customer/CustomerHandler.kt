package com.xsc.mundiagua.api.serverless.customer

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.xsc.mundiagua.service.CustomerService
import com.xsc.mundiagua.service.di.DaggerCustomerServiceInjector
import javax.inject.Inject

abstract class CustomerHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Inject
    lateinit var customerService: CustomerService

    abstract override fun handleRequest(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent

    init {
        DaggerCustomerServiceInjector.builder().build().inject(this)
    }
}