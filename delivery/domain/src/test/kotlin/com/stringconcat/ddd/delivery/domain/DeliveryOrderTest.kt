package com.stringconcat.ddd.delivery.domain

import com.stringconcat.ddd.delivery.address
import com.stringconcat.ddd.delivery.orderId
import com.stringconcat.ddd.delivery.orderItem
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class DeliveryOrderTest {

    @Test
    fun `should create delivery order`() {
        // Given
        val address = address()
        val id = orderId()
        val orderItem = orderItem()

        // When
        val result = DeliveryOrder.create(
            id = id,
            deliveryAddress = address,
            orderItems = listOf(orderItem)
        )

        // Then
        result.shouldBeRight { order ->
            order.id shouldBe id
            order.version.value shouldBe 0
            order.deliveryAddress shouldBe address
            order.orderItems.size shouldBe 1
            order.delivered shouldBe false
            val item = order.orderItems.first()
            item.meal shouldBe orderItem.meal
            item.count shouldBe orderItem.count

            order.popEvents() shouldContainExactly listOf(DeliveryOrderCreatedDomainEvent(id))
        }
    }

    @Test
    fun `should fail on no items in the order`() {
        // Given
        val address = address()
        val id = orderId()

        // When
        val result = DeliveryOrder.create(
            id = id,
            deliveryAddress = address,
            orderItems = emptyList()
        )

        // Then
        result.shouldBeLeft(OrderWithNoItems)
    }

    @Test
    fun `should deliver the order`() {
        // Given
        val address = address()
        val id = orderId()
        val orderItem = orderItem()
        val order = DeliveryOrder.create(
            id = id,
            deliveryAddress = address,
            orderItems = listOf(orderItem)
        ).orNull().shouldNotBeNull()
        order.popEvents() shouldContainExactly listOf(DeliveryOrderCreatedDomainEvent(id))

        // When
        order.deliver()

        // Then
        order.delivered shouldBe true
        order.popEvents() shouldContainExactly listOf(DeliveryOrderDeliveredDomainEvent(id))
    }
}
