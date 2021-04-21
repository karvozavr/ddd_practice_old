package com.stringconcat.dev.course.app.configuration

import com.stringconcat.ddd.delivery.usecase.order.CreateOrder
import com.stringconcat.ddd.kitchen.usecase.order.CreateOrderHandler
import com.stringconcat.ddd.order.usecase.menu.MealExtractor
import com.stringconcat.ddd.order.usecase.order.CompleteOrder
import com.stringconcat.ddd.order.usecase.order.CustomerOrderExtractor
import com.stringconcat.ddd.order.usecase.payment.ExportPaymentData
import com.stringconcat.dev.course.app.event.EventPublisherImpl
import com.stringconcat.dev.course.app.listeners.CompleteOrderWhenDeliveredRule
import com.stringconcat.dev.course.app.listeners.ExportSuccessfulPaymentToCrmRule
import com.stringconcat.dev.course.app.listeners.SendOrderToDeliveryWhenCookedRule
import com.stringconcat.dev.course.app.listeners.SendOrderToKitchenAfterConfirmationRule
import com.stringconcat.integration.crm.SimpleCRMConnector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ContextsIntegration {

    @Bean
    fun customerOrderConfirmedListener(
        customerOrderExtractor: CustomerOrderExtractor,
        mealExtractor: MealExtractor,
        createOrderHandler: CreateOrderHandler,
        domainEventPublisher: EventPublisherImpl
    ): SendOrderToKitchenAfterConfirmationRule {

        val listener = SendOrderToKitchenAfterConfirmationRule(
            customerOrderExtractor = customerOrderExtractor,
            mealExtractor = mealExtractor,
            createOrder = createOrderHandler
        )

        domainEventPublisher.registerListener(listener)
        return listener
    }

    @Bean
    fun exportSuccessfulPaymentToCrmRule(
        exportPaymentData: ExportPaymentData,
        domainEventPublisher: EventPublisherImpl
    ): ExportSuccessfulPaymentToCrmRule {
        val listener = ExportSuccessfulPaymentToCrmRule(
            exportPaymentData = exportPaymentData
        )

        domainEventPublisher.registerListener(listener)
        return listener
    }

    @Bean
    fun sendOrderToDeliveryWhenCookedRule(
        customerOrderExtractor: CustomerOrderExtractor,
        createOrderUseCase: CreateOrder,
        domainEventPublisher: EventPublisherImpl
    ): SendOrderToDeliveryWhenCookedRule {
        val listener = SendOrderToDeliveryWhenCookedRule(
            customerOrderExtractor = customerOrderExtractor,
            createOrderUseCase = createOrderUseCase
        )

        domainEventPublisher.registerListener(listener)
        return listener
    }

    @Bean
    fun sendOrderToDeliveryWhenCookedRule(
        completeOrderUseCase: CompleteOrder,
        domainEventPublisher: EventPublisherImpl
    ): CompleteOrderWhenDeliveredRule {
        val listener = CompleteOrderWhenDeliveredRule(
            completeOrderUseCase = completeOrderUseCase
        )

        domainEventPublisher.registerListener(listener)
        return listener
    }

    @Bean
    fun simpleCRMConnector() = SimpleCRMConnector()
}