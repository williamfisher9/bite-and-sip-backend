package com.apps.biteandsip.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "food_items")
public class FoodItem implements Serializable {
    private static final Long serialVersionUUID = -37728L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String imageSource;
    private String description;
    private float price;
    private int rating;

    private boolean active;



    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @JsonIgnoreProperties("items")
    private FoodCategory category;

    public FoodItem() {
    }

    public FoodItem(String name, String imageSource, String description, float price, int rating, boolean active) {
        this.name = name;
        this.imageSource = imageSource;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public FoodCategory getCategory() {
        return category;
    }

    public void setCategory(FoodCategory category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItem foodItem = (FoodItem) o;
        return rating == foodItem.rating && active == foodItem.active && Objects.equals(id, foodItem.id) && Objects.equals(name, foodItem.name) && Objects.equals(imageSource, foodItem.imageSource) && Objects.equals(description, foodItem.description) && Objects.equals(price, foodItem.price) && Objects.equals(category, foodItem.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageSource, description, price, rating, active, category);
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageSource='" + imageSource + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", rating=" + rating +
                ", active=" + active +
                ", category=" + category +
                '}';
    }
}
