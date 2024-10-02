package com.tadeo.ecommerce.kafka;

import com.tadeo.ecommerce.customer.CustomerResponse;
import com.tadeo.ecommerce.order.PaymentMethod;
import com.tadeo.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,

        BigDecimal totalAmount,

        PaymentMethod paymentMethod,

        CustomerResponse customer,

        List<PurchaseResponse> products
) {
}
