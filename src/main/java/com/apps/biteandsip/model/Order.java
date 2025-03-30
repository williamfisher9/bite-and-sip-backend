package com.apps.biteandsip.model;

import com.apps.biteandsip.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.stripe.model.Customer;
import jakarta.persistence.*;
import jakarta.validation.Valid;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order implements Serializable {
    private static final Long serialVersionUUID = -2819L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    private OrderStatus status;
    private String paymentId;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateUpdate;

    private BigDecimal tax;
    private BigDecimal deliveryFee;
    private String coupon;
    private BigDecimal couponAmount;
    private BigDecimal totalPrice;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private User customer;



    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderItem> items;

    public Order() {
    }


    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastUpdateUpdate() {
        return lastUpdateUpdate;
    }

    public void setLastUpdateUpdate(LocalDateTime lastUpdateUpdate) {
        this.lastUpdateUpdate = lastUpdateUpdate;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    public void setItems(Set<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
