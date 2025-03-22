package com.apps.biteandsip.dto;

import java.math.BigDecimal;

public class FoodItemDTO {
    private String name;
    private String imageSource;
    private float price;
    private String description;
    private Long foodCategory;

    public FoodItemDTO(String name, String imageSource, float price, String description, Long foodCategory) {
        this.name = name;
        this.imageSource = imageSource;
        this.price = price;
        this.description = description;
        this.foodCategory = foodCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(Long foodCategory) {
        this.foodCategory = foodCategory;
    }
}
