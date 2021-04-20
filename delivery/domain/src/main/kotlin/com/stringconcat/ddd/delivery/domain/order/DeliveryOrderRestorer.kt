package com.stringconcat.ddd.delivery.domain.order

import com.stringconcat.ddd.common.types.base.Version
import com.stringconcat.ddd.common.types.common.Address

object DeliveryOrderRestorer {

    fun restoreOrder(
        id: DeliveryOrderId,
        address: Address,
        delivered: Boolean,
        version: Version
    ): DeliveryOrder {
        return DeliveryOrder(
            id = id,
            deliveryAddress = address,
            version = version
        ).apply {
            this.delivered = delivered
        }
    }
}
