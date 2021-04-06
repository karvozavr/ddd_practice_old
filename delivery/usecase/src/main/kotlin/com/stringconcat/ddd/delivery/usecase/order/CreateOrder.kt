package com.stringconcat.ddd.delivery.usecase.order

import arrow.core.Either
import com.stringconcat.ddd.common.types.common.Address

interface CreateOrder {
    fun execute(request: CreateOrderRequest): Either<CreateOrderUseCaseError, Unit>
}

data class CreateOrderRequest(
    val id: Long,
    val address: Address,
    val items: List<OrderItemData>
) {
    data class OrderItemData(val mealName: String, val count: Int)
}

sealed class CreateOrderUseCaseError {
    data class InvalidCount(val message: String) : CreateOrderUseCaseError()
    data class InvalidMealName(val message: String) : CreateOrderUseCaseError()
    object EmptyOrder : CreateOrderUseCaseError()
}
