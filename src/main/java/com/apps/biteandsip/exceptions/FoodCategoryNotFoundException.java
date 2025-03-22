package com.apps.biteandsip.exceptions;

public class FoodCategoryNotFoundException extends RuntimeException{
    public FoodCategoryNotFoundException(String message){
        super(message);
    }
}
