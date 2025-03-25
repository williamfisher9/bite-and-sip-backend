package com.apps.biteandsip.exceptions;

public class FoodItemNotFoundException extends RuntimeException{
    public FoodItemNotFoundException(String message){
        super(message);
    }
}
