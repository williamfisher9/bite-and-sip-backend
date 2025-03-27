package com.apps.biteandsip.dto;

public class CouponDTO {
    private String code;
    private String fromDate;
    private String toDate;
    private float amount;
    private boolean active;

    public CouponDTO(String code, String fromDate, String toDate, float amount, boolean active) {
        this.code = code;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.amount = amount;
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "CouponDTO{" +
                "code='" + code + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                ", amount=" + amount +
                ", active=" + active +
                '}';
    }
}
