package com.example.demo.model.enums;

public enum OrderStatus {
    PENDING("Đang chờ"),
    CONFIRMED("Đã xác nhận"),
    SHIPPED("Đã giao hàng"),
    DELIVERED("Đã nhận hàng"),
    CANCELLED("Đã huỷ");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
