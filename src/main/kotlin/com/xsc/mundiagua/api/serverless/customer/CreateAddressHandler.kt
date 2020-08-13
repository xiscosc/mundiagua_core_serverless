package com.xsc.mundiagua.api.serverless.customer

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.google.gson.Gson
import com.xsc.mundiagua.api.dto.customer.RequestAddress
import com.xsc.mundiagua.api.serverless.ServerlessResponse
import com.xsc.mundiagua.service.CustomerService

class CreateAddressHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    override fun handleRequest(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val body = event.body
        val uuid = event.pathParameters["uuid"]

        if (body.isNullOrBlank()) {
            return ServerlessResponse.badRequest<RequestAddress>("Missing body")
        }

        if (uuid.isNullOrBlank()) {
            return ServerlessResponse.badRequest<RequestAddress>("Invalid uuid")
        }

        return try {
            val requestAddress = Gson().fromJson(body, RequestAddress::class.java)
            val createdAddress = CustomerService().saveAddress(uuid, RequestAddress.adaptToModel(requestAddress))
                ?: throw Exception("Error creating address")
            ServerlessResponse.created(listOf(RequestAddress.adaptFromModel(createdAddress)))
        } catch (e: Exception) {
            ServerlessResponse.error<RequestAddress>(e)
        }
    }
}