package com.stringconcat.ddd.delivery.usecase.order

import com.stringconcat.ddd.delivery.domain.order.DeliveryOrderCreatedDomainEvent
import com.stringconcat.ddd.delivery.domain.order.OrderItem
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class CreateOrderHandlerTest {

    @Test
    fun `order doesn't exist - order created successfully`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()

        val handler = CreateOrderHandler(extractor, persister)

        val orderId = orderId()
        val meal = meal()
        val count = count()
        val address = address()

        val itemData = CreateOrderRequest.OrderItemData(
            mealName = meal.value,
            count = count.value
        )

        val request = CreateOrderRequest(
            id = orderId.value,
            address = address,
            items = listOf(itemData)
        )

        val result = handler.execute(request)
        result.shouldBeRight()

        val order = persister[orderId]
        order.shouldNotBeNull()
        order.id shouldBe orderId
        order.deliveryAddress shouldBe address
        order.orderItems shouldContainExactly listOf(OrderItem(meal, count))
        order.popEvents() shouldContainExactly listOf(DeliveryOrderCreatedDomainEvent(orderId))
    }

    @Test
    fun `order exists - order not created`() {

        val address = address()
        val existingOrder = order(address)

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor().apply {
            this[existingOrder.id] = existingOrder
        }

        val handler = CreateOrderHandler(extractor, persister)

        val request = CreateOrderRequest(
            id = existingOrder.id.value,
            address = address,
            items = emptyList()
        )

        val result = handler.execute(request)
        result.shouldBeRight()

        val order = persister[existingOrder.id]
        order.shouldBeNull()
    }

    @Test
    fun `invalid meal name`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()

        val handler = CreateOrderHandler(extractor, persister)

        val orderId = orderId()
        val count = count()
        val address = address()

        val itemData = CreateOrderRequest.OrderItemData(
            mealName = "",
            count = count.value
        )

        val request = CreateOrderRequest(
            id = orderId.value,
            address = address,
            items = listOf(itemData)
        )

        handler.execute(request) shouldBeLeft CreateOrderUseCaseError.InvalidMealName("Meal name is empty")
    }

    @Test
    fun `invalid count`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()

        val handler = CreateOrderHandler(extractor, persister)

        val orderId = orderId()
        val meal = meal()
        val address = address()

        val itemData = CreateOrderRequest.OrderItemData(
            mealName = meal.value,
            count = -12
        )

        val request = CreateOrderRequest(
            id = orderId.value,
            address = address,
            items = listOf(itemData)
        )

        handler.execute(request) shouldBeLeft CreateOrderUseCaseError.InvalidCount("Negative value")
    }

    @Test
    fun `empty order`() {

        val persister = TestDeliveryOrderPersister()
        val extractor = TestDeliveryOrderExtractor()

        val handler = CreateOrderHandler(extractor, persister)

        val orderId = orderId()
        val address = address()

        val request = CreateOrderRequest(
            id = orderId.value,
            address = address,
            items = emptyList()
        )

        handler.execute(request) shouldBeLeft CreateOrderUseCaseError.EmptyOrder
    }
}
