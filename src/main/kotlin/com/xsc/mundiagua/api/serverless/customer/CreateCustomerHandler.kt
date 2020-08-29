package com.xsc.mundiagua.api.serverless.customer

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.google.gson.Gson
import com.xsc.mundiagua.api.dto.customer.RequestCustomer
import com.xsc.mundiagua.api.serverless.ServerlessResponse

class CreateCustomerHandler : CustomerHandler() {
    override fun handleRequest(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val body = event.body
        if (body.isNullOrBlank()) {
            return ServerlessResponse.badRequest<RequestCustomer>("Missing body")
        }

        return try {
            val requestCustomer = Gson().fromJson(body, RequestCustomer::class.java)
            val createdCustomer = this.customerService.saveCustomer(RequestCustomer.adaptToModel(requestCustomer))
            ServerlessResponse.created(listOf(RequestCustomer.adaptFromModel(createdCustomer)))
        } catch (e: Exception) {
            ServerlessResponse.error<RequestCustomer>(e)
        }
    }
}