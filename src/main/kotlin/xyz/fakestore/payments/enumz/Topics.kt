package xyz.fakestore.payments.enumz


object Topics {
    object Orders {
        const val PAYMENTS_PAYMENTREQUESTED = "orders.payments.paymentrequested"
        const val ORDER_CREATED = "orders.order.created"
        const val ORDER_UPDATED = "orders.order.updated"
        const val ORDER_CANCELLED = "orders.order.cancelled"
    }

    object Payments {
        const val PAYMENT_PROCESSED = "payments.payment.processed"
        const val PAYMENT_REJECTED = "payments.payment.rejected"
        const val PAYMENT_FAILED = "payments.payment.failed"
    }

    object Users {
        const val USER_CREATED = "users.user.created"
        const val USER_UPDATED = "users.user.updated"
        const val USER_DELETED = "users.user.deleted"
    }

    object Catalog {
        const val PRODUCT_CREATED = "catalog.product.created"
        const val PRODUCT_UPDATED = "catalog.product.updated"
        const val PRODUCT_DELETED = "catalog.product.deleted"
        const val PRICING_UPDATED = "catalog.pricing.updated"
    }

    object Shipments {
        const val SHIPMENT_CREATED = "shipments.shipment.created"
        const val SHIPMENT_UPDATED = "shipments.shipment.updated"
        const val SHIPMENT_DELIVERED = "shipments.shipment.delivered"
        const val SHIPMENT_FAILED = "shipments.shipment.failed"
    }
}
