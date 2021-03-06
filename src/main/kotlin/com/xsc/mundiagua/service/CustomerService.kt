package com.xsc.mundiagua.service

import com.xsc.mundiagua.service.model.customer.Customer
import com.xsc.mundiagua.repository.CustomerRepository
import com.xsc.mundiagua.service.model.customer.Address
import com.xsc.mundiagua.service.model.customer.Phone
import javax.inject.Inject

class CustomerService @Inject constructor(private val customerRepository: CustomerRepository) {

    fun saveCustomer(customer: Customer): Customer {
        return customerRepository.save(customer)
    }

    fun getCustomer(customerId: Int): Customer? {
        return customerRepository.getById(customerId)
    }

    fun getCustomer(customerUuid: String): Customer? {
        return customerRepository.getByUUID(customerUuid)
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
        return customerRepository.getList(oldFirst, lastId, limit ?: DEFAULT_LIST_ITEMS)
    }
}