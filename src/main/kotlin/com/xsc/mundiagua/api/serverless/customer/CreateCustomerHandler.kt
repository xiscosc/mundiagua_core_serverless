package com.xsc.mundiagua.api.serverless.customer

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.google.gson.Gson
import com.xsc.mundiagua.api.model.customer.RequestCustomer
import com.xsc.mundiagua.api.serverless.ServerlessResponse
import com.xsc.mundiagua.service.CustomerService

class CreateCustomerHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    override fun handleRequest(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val body = event.body
        if (body.isNullOrBlank()) {
            return ServerlessResponse.badRequest<RequestCustomer>("Missing body")
        }

        return try {
            val requestCustomer = Gson().fromJson(body, RequestCustomer::class.java)
            val service = CustomerService()
            val createdCustomer = service.saveCustomer(RequestCustomer.adaptToModel(requestCustomer))
            ServerlessResponse.created(listOf(RequestCustomer.adaptFromModel(createdCustomer)))
        } catch (e: Exception) {
            ServerlessResponse.error<RequestCustomer>(e)
        }
    }
}