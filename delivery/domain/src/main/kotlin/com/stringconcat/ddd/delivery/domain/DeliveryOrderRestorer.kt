package com.stringconcat.ddd.delivery.domain

import com.stringconcat.ddd.common.types.base.Version
import com.stringconcat.ddd.common.types.common.Address

object DeliveryOrderRestorer {

    fun restoreOrder(
        id: DeliveryOrderId,
        address: Address,
        orderItems: List<OrderItem>,
        delivered: Boolean,
        version: Version
    ): DeliveryOrder {
        return DeliveryOrder(
            id = id,
            deliveryAddress = address,
            orderItems = orderItems,
            version = version
        ).apply {
            this.delivered = delivered
        }
    }
}
