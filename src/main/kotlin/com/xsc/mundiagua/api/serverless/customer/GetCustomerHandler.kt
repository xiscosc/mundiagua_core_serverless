package com.xsc.mundiagua.api.serverless.customer

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.xsc.mundiagua.api.dto.customer.RequestCustomer
import com.xsc.mundiagua.api.serverless.ServerlessResponse

class GetCustomerHandler : CustomerHandler() {
    override fun handleRequest(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val uuid = event.pathParameters["uuid"]
        if (uuid.isNullOrBlank()) {
            return ServerlessResponse.badRequest<RequestCustomer>("Invalid uuid")
        }

        val customer = this.customerService.getCustomer(uuid) ?: return ServerlessResponse.notFound<RequestCustomer>()
        val requestCustomer = RequestCustomer.adaptFromModel(customer)
        return ServerlessResponse.ok(listOf(requestCustomer))
    }
}