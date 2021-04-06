package com.stringconcat.ddd.delivery.usecase.order

import arrow.core.Either
import arrow.core.extensions.either.apply.tupled
import arrow.core.left
import arrow.core.right
import com.stringconcat.ddd.common.types.common.Count
import com.stringconcat.ddd.common.types.common.NegativeValueError
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrder
import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderId
import com.stringconcat.ddd.delivery.domain.order.EmptyMealNameError
import com.stringconcat.ddd.delivery.domain.order.Meal
import com.stringconcat.ddd.delivery.domain.order.OrderItem
import com.stringconcat.ddd.delivery.domain.order.OrderWithNoItems

class CreateOrderHandler(
    private val orderExtractor: DeliveryOrderExtractor,
    private val orderPersister: DeliveryOrderPersister
) : CreateOrder {

    override fun execute(request: CreateOrderRequest): Either<CreateOrderUseCaseError, Unit> {
        val orderId = DeliveryOrderId(request.id)
        return if (orderExtractor.getById(orderId) == null) {
            createNewOrder(request)
        } else {
            Unit.right()
        }
    }

    private fun createNewOrder(request: CreateOrderRequest): Either<CreateOrderUseCaseError, Unit> {

        val items = request.items.map {
            tupled(
                transform(it.count),
                transform(it.mealName)
            ).map { sourceItem -> OrderItem(sourceItem.b, sourceItem.a) }
        }.map {
            it.mapLeft { e -> return@createNewOrder e.left() }
        }.mapNotNull { it.orNull() }

        return DeliveryOrder.create(
            id = DeliveryOrderId(request.id),
            deliveryAddress = request.address,
            orderItems = items
        )
            .mapLeft { it.toError() }
            .map { order ->
                orderPersister.save(order)
            }
    }

    private fun transform(count: Int): Either<CreateOrderUseCaseError, Count> {
        return Count.from(count).mapLeft { it.toError() }
    }

    private fun transform(mealName: String): Either<CreateOrderUseCaseError, Meal> {
        return Meal.from(mealName).mapLeft { it.toError() }
    }
}

fun EmptyMealNameError.toError() = CreateOrderUseCaseError.InvalidMealName("Meal name is empty")
fun NegativeValueError.toError() = CreateOrderUseCaseError.InvalidCount("Negative value")
fun OrderWithNoItems.toError() = CreateOrderUseCaseError.EmptyOrder
