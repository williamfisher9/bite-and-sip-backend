package com.apps.biteandsip.exceptions;

public class CouponNotFoundException extends RuntimeException{
    public CouponNotFoundException(String message){
        super(message);
    }
}
