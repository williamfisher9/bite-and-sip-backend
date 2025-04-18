package com.apps.biteandsip.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "coupons", uniqueConstraints = {@UniqueConstraint(columnNames = "code")})
public class Coupon {
    private static final Long serialVersionUUID = -37712328L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private boolean active;
    private LocalDate fromDate;
    private LocalDate toDate;
    private float amount;

    public Coupon() {
    }

    public Coupon(String code, boolean active, LocalDate fromDate, LocalDate toDate, float amount) {
        this.code = code;
        this.active = active;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coupon coupon = (Coupon) o;
        return active == coupon.active && Float.compare(amount, coupon.amount) == 0 && Objects.equals(id, coupon.id) && Objects.equals(code, coupon.code) && Objects.equals(fromDate, coupon.fromDate) && Objects.equals(toDate, coupon.toDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, active, fromDate, toDate, amount);
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", active=" + active +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", amount=" + amount +
                '}';
    }
}
