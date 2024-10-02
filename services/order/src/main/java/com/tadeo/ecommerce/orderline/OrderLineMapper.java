package com.tadeo.ecommerce.orderline;

import com.tadeo.ecommerce.order.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderLineMapper {

    public OrderLine toOrderLine(OrderLineRequest request) {
        return OrderLine.builder()
                .id(request.id())
                .quantity(request.quantity())
                .order(Order.builder()
                        .id(request.orderId())
                        .build())
                .productId(request.productId())
                .build();
    }

    public OrderLineResponse toOrderLineResponse(OrderLineResponse orderLine) {
        return new OrderLineResponse(
                orderLine.id(), orderLine.quantity()
        );
    }
}
