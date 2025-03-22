package com.apps.biteandsip.dto;

public class FoodCategoryDTO {
    private String name;
    private String imageSource;

    public FoodCategoryDTO(String name, String imageSource) {
        this.name = name;
        this.imageSource = imageSource;
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


}
