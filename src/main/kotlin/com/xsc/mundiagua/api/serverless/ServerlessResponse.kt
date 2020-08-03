package com.xsc.mundiagua.api.serverless

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.google.gson.Gson


data class ServerlessResponse<T>(
    val count: Int,
    val elements: List<T>,
    val message: String?
) {

    private fun toApiGatewayResponse(statusCode: Int): APIGatewayProxyResponseEvent {
        return APIGatewayProxyResponseEvent().withStatusCode(statusCode).withBody(Gson().toJson(this))
    }

    companion object {
        fun <T> badRequest(body: String): APIGatewayProxyResponseEvent {
            return ServerlessResponse(0, listOf<T>(), body).toApiGatewayResponse(400)
        }

        fun <T> ok(objects: List<T>): APIGatewayProxyResponseEvent
        {
            return ServerlessResponse(objects.size, objects, null).toApiGatewayResponse(200)
        }

        fun <T> created(objects: List<T>): APIGatewayProxyResponseEvent
        {
            return ServerlessResponse(objects.size, objects, "Created").toApiGatewayResponse(201)
        }

        fun <T> notFound(): APIGatewayProxyResponseEvent
        {
            return ServerlessResponse(0, listOf<T>(), "Elements not found").toApiGatewayResponse(404)
        }

        fun <T> error(body: String): APIGatewayProxyResponseEvent
        {
            return ServerlessResponse(0, listOf<T>(), body).toApiGatewayResponse(500)
        }

        fun <T> error(e: Exception): APIGatewayProxyResponseEvent
        {
            return ServerlessResponse(0, listOf<T>(), e.toString()).toApiGatewayResponse(500)
        }
    }
}