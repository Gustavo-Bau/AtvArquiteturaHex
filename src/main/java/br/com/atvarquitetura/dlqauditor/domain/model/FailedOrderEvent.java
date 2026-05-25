package br.com.atvarquitetura.dlqauditor.domain.model;

import java.time.Instant;
import java.util.List;

public record FailedOrderEvent(
        String zipCode,
        Long customerId,
        List<OrderItem> orderItems,
        String origin,
        Instant occurredAt
) {

    public int totalItems() {
        return orderItems == null ? 0 : orderItems.stream().mapToInt(OrderItem::amount).sum();
    }
}
