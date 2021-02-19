package com.stringconcat.ddd.order.usecase.order

import com.stringconcat.ddd.order.domain.order.CustomerOrderHasBeenCancelledEvent
import com.stringconcat.ddd.order.usecase.menu.TestCustomerOrderExtractor
import com.stringconcat.ddd.order.usecase.menu.TestCustomerOrderPersister
import com.stringconcat.ddd.order.usecase.menu.orderNotReadyForCancel
import com.stringconcat.ddd.order.usecase.menu.orderReadyForCancel
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test

internal class CancelOrderUseCaseTest {

    private val order = orderReadyForCancel()
    private val extractor = TestCustomerOrderExtractor().apply {
        this[order.id] = order
    }
    private val persister = TestCustomerOrderPersister()

    @Test
    fun `successfully confirmed`() {
        val useCase = CancelOrderUseCase(extractor, persister)
        val result = useCase.cancelOrder(orderId = order.id.value)

        result.shouldBeRight()

        val customerOrder = persister[order.id]
        customerOrder.shouldNotBeNull()
        customerOrder.popEvents() shouldContainExactly listOf(CustomerOrderHasBeenCancelledEvent(order.id))
    }

    @Test
    fun `invalid state`() {

        val order = orderNotReadyForCancel()
        extractor[order.id] = order

        val useCase = CancelOrderUseCase(extractor, persister)
        val result = useCase.cancelOrder(orderId = order.id.value)
        result shouldBeLeft CancelOrderUseCaseError.InvalidOrderState
    }

    @Test
    fun `order not found`() {
        extractor.clear()
        val useCase = CancelOrderUseCase(extractor, persister)
        val result = useCase.cancelOrder(orderId = order.id.value)
        result shouldBeLeft CancelOrderUseCaseError.OrderNotFound
    }
}