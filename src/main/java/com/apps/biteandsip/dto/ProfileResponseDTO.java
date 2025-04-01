package com.apps.biteandsip.dto;

public class ProfileResponseDTO {
    private String username;
    private String imageSource;
    private String firstName;
    private String lastName;

    public ProfileResponseDTO() {
    }

    public ProfileResponseDTO(String username, String imageSource, String firstName, String lastName) {
        this.username = username;
        this.imageSource = imageSource;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
