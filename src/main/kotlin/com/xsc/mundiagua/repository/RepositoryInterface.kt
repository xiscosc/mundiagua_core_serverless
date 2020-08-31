package com.xsc.mundiagua.repository

interface RepositoryInterface<T> {

    fun getByUUID(uuid: String): T?

    fun getById(id: Int): T?

    fun save(model: T): T

    fun getList(scanForward: Boolean, lastEvaluatedKey: Int?, limit: Int): List<T>
}