package com.example.demo.model.enums;

public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    CREDIT_CARD("Thẻ tín dụng"),
    BANK_TRANSFER("Chuyển khoản");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { // PHẢI public
        return displayName;
    }
}



