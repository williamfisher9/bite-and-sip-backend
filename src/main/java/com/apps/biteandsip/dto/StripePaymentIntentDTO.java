package com.apps.biteandsip.dto;

public class StripePaymentIntentDTO {
    Long amount;

    public StripePaymentIntentDTO(Long amount) {
        this.amount = amount;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
