package com.apps.biteandsip.dto;

public class CartItemDTO {
    private Long foodItemId;
    private int quantity;

    public CartItemDTO() {
    }

    public CartItemDTO(Long foodItemId, int quantity) {
        this.foodItemId = foodItemId;
        this.quantity = quantity;
    }

    public Long getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(Long foodItemId) {
        this.foodItemId = foodItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartItemDTO{" +
                "foodItemId=" + foodItemId +
                ", quantity=" + quantity +
                '}';
    }
}
