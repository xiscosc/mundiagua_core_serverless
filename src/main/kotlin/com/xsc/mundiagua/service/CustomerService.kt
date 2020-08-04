package com.xsc.mundiagua.service

import com.xsc.mundiagua.service.model.customer.Customer
import com.xsc.mundiagua.repository.CustomerRepository
import com.xsc.mundiagua.repository.model.customer.DynamoDBCustomer
import com.xsc.mundiagua.repository.model.customer.DynamoDBPhone
import com.xsc.mundiagua.service.model.customer.Phone

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

    fun savePhone(customerUuid: String, phone: Phone): Phone? {
        customerRepository.saveNewPhone(customerUuid, DynamoDBPhone.adaptToDbRecord(phone)) ?: return null
        return phone
    }
}