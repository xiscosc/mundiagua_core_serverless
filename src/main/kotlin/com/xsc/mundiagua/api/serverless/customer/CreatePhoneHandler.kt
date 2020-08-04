package com.xsc.mundiagua.api.serverless.customer

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.google.gson.Gson
import com.xsc.mundiagua.api.model.customer.RequestCustomer
import com.xsc.mundiagua.api.model.customer.RequestPhone
import com.xsc.mundiagua.api.serverless.ServerlessResponse
import com.xsc.mundiagua.service.CustomerService

class CreatePhoneHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    override fun handleRequest(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val body = event.body
        val uuid = event.pathParameters["uuid"]

        if (body.isNullOrBlank()) {
            return ServerlessResponse.badRequest<RequestCustomer>("Missing body")
        }

        if (uuid.isNullOrBlank()) {
            return ServerlessResponse.badRequest<RequestCustomer>("Invalid uuid")
        }

        return try {
            val requestPhone = Gson().fromJson(body, RequestPhone::class.java)
            val createdPhone = CustomerService().savePhone(uuid, RequestPhone.adaptToModel(requestPhone))
                ?: throw Exception("Error creating phone")
            ServerlessResponse.created(listOf(RequestPhone.adaptFromModel(createdPhone)))
        } catch (e: Exception) {
            ServerlessResponse.error<RequestCustomer>(e)
        }
    }
}