package com.stringconcat.ddd.delivery.domain

import com.stringconcat.ddd.delivery.address
import com.stringconcat.ddd.delivery.orderId
import com.stringconcat.ddd.delivery.orderItem
import com.stringconcat.ddd.delivery.version
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class DeliveryOrderRestorerTest {

    @Test
    fun `should restore the order`() {
        val id = orderId()
        val address = address()
        val item = orderItem()
        val items = listOf(item)
        val delivered = true
        val version = version()

        val order = DeliveryOrderRestorer.restoreOrder(
            id = id,
            address = address,
            orderItems = items,
            delivered = delivered,
            version = version
        )

        order.id shouldBe id
        order.deliveryAddress shouldBe address
        order.orderItems shouldContainExactly items
        order.delivered shouldBe true
        order.version shouldBe version
        order.popEvents().shouldBeEmpty()
    }
}