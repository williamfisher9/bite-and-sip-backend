package com.apps.biteandsip.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem implements Serializable {
    private static final Long serialVersionUUID = -12819L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "item_id")
    private FoodItem item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    private int quantity;
    private BigDecimal pricePerItem;
    private BigDecimal totalPricePerItem;

    public OrderItem() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public FoodItem getItem() {
        return item;
    }

    public void setItem(FoodItem item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "uuid=" + uuid +
                ", order=" + order +
                ", item=" + item +
                ", quantity=" + quantity +
                '}';
    }

    public BigDecimal getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(BigDecimal pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public BigDecimal getTotalPricePerItem() {
        return totalPricePerItem;
    }

    public void setTotalPricePerItem(BigDecimal totalPricePerItem) {
        this.totalPricePerItem = totalPricePerItem;
    }
}
