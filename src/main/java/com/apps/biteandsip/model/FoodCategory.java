package com.apps.biteandsip.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "categories")
public class FoodCategory implements Serializable {
    private static final Long serialVersionUUID = -82819L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String imageSource;
    private boolean active;

    private int sortingOrder;

    @OneToMany(mappedBy = "category")
    @JsonIgnoreProperties("category")
    private Set<FoodItem> items;

    public FoodCategory() {
    }

    public FoodCategory(String name, String imageSource) {
        this.name = name;
        this.imageSource = imageSource;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<FoodItem> getItems() {
        return items;
    }

    public void setItems(Set<FoodItem> items) {
        this.items = items;
    }

    public int getSortingOrder() {
        return sortingOrder;
    }

    public void setSortingOrder(int sortingOrder) {
        this.sortingOrder = sortingOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodCategory that = (FoodCategory) o;
        return active == that.active && sortingOrder == that.sortingOrder && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(imageSource, that.imageSource) && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, imageSource, active, items, sortingOrder);
    }

    @Override
    public String toString() {
        return "FoodCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageSource='" + imageSource + '\'' +
                ", active=" + active +
                ", items=" + items +
                ", sortingOrder=" + sortingOrder +
                '}';
    }
}
