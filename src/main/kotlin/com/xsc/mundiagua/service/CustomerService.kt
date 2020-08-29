package com.xsc.mundiagua.service

import com.xsc.mundiagua.service.model.customer.Customer
import com.xsc.mundiagua.repository.CustomerRepository
import com.xsc.mundiagua.service.model.customer.Address
import com.xsc.mundiagua.service.model.customer.Phone

class CustomerService {
    private val customerRepository = CustomerRepository()

    fun saveCustomer(customer: Customer): Customer {
        return customerRepository.saveNewCustomer(customer)
    }

    fun getCustomer(customerId: Int): Customer? {
        return customerRepository.getCustomer(customerId) ?: return null
    }

    fun getCustomer(customerUuid: String): Customer? {
        return customerRepository.getCustomer(customerUuid) ?: return null
    }

    fun savePhone(customerUuid: String, phone: Phone): Phone? {
        customerRepository.saveNewPhone(customerUuid, phone) ?: return null
        return phone
    }

    fun saveAddress(customerUuid: String, address: Address): Address? {
        customerRepository.saveNewAddress(customerUuid, address) ?: return null
        return address
    }

    fun getCustomerList(oldFirst: Boolean, lastId: Int?, limit: Int?): List<Customer> {
        return customerRepository.getCustomerList(oldFirst, lastId?.toString(), limit ?: DEFAULT_LIST_ITEMS)
    }
}