package com.xsc.mundiagua.api.serverless.customer

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.google.gson.Gson
import com.xsc.mundiagua.api.dto.customer.RequestPhone
import com.xsc.mundiagua.api.serverless.ServerlessResponse

class CreatePhoneHandler : CustomerHandler() {
    override fun handleRequest(event: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val body = event.body
        val uuid = event.pathParameters["uuid"]

        if (body.isNullOrBlank()) {
            return ServerlessResponse.badRequest<RequestPhone>("Missing body")
        }

        if (uuid.isNullOrBlank()) {
            return ServerlessResponse.badRequest<RequestPhone>("Invalid uuid")
        }

        return try {
            val requestPhone = Gson().fromJson(body, RequestPhone::class.java)
            val createdPhone = this.customerService.savePhone(uuid, RequestPhone.adaptToModel(requestPhone))
                ?: throw Exception("Error creating phone")
            ServerlessResponse.created(listOf(RequestPhone.adaptFromModel(createdPhone)))
        } catch (e: Exception) {
            ServerlessResponse.error<RequestPhone>(e)
        }
    }
}