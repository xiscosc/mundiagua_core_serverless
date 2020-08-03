package com.xsc.mundiagua.service

import com.xsc.mundiagua.model.customer.Customer
import com.xsc.mundiagua.repository.CustomerRepository
import com.xsc.mundiagua.repository.model.customer.DynamoDBCustomer

class CustomerService {
    val customerRepository = CustomerRepository()

    fun saveCustomer(customer: Customer): Customer {
        val savedCustomer = customerRepository.saveNewCustomer(DynamoDBCustomer.adaptToDbRecord(customer))
        return DynamoDBCustomer.adaptToModel(savedCustomer)
    }

    fun getCustomer(customerId: Int): Customer? {
        val customer = customerRepository.getCustomer(customerId) ?: return null
        return DynamoDBCustomer.adaptToModel(customer)
    }

    fun getCustomer(customerUuid: String): Customer? {
        val customer = customerRepository.getCustomer(customerUuid) ?: return null
        return DynamoDBCustomer.adaptToModel(customer)
    }
}