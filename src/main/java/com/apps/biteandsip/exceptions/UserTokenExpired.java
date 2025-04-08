package com.apps.biteandsip.exceptions;

public class UserTokenExpired extends UserTokenException{
    public UserTokenExpired(String message){
        super(message);
    }
}
