package com.xsc.mundiagua.repository

interface RepositoryInterface<T> {

    fun getByUUID(uuid: String): T?

    fun getById(id: Int): T?

    fun save(model: T): T

    fun getList(scanForward: Boolean, lastEvaluatedKey: String?, limit: Int): List<T>
}