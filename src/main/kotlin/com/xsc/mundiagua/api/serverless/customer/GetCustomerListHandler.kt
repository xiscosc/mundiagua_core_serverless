package com.xsc.mundiagua.api.serverless.customer

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.xsc.mundiagua.api.dto.customer.RequestCustomer
import com.xsc.mundiagua.api.serverless.ServerlessResponse

class GetCustomerListHandler : CustomerHandler() {
    override fun handleRequest(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        if (event.queryStringParameters != null) {
            val limit = if (event.queryStringParameters.containsKey("limit")) {
                event.queryStringParameters["limit"]?.toInt()
            } else {
                null
            }

            val lastId = if (event.queryStringParameters.containsKey("lastId")) {
                event.queryStringParameters["lastId"]?.toInt()
            } else {
                null
            }

            val oldFirst = if (event.queryStringParameters.containsKey("oldFirst")) {
                event.queryStringParameters["oldFirst"]?.toBoolean() ?: false
            } else {
                false
            }

            val customers = this.customerService.getCustomerList(oldFirst, lastId, limit)
            return ServerlessResponse.ok(customers.map { RequestCustomer.adaptFromModel(it) })
        } else {
            val customers = this.customerService.getCustomerList(false, null, null)
            return ServerlessResponse.ok(customers.map { RequestCustomer.adaptFromModel(it) })
        }
    }
}