package com.apps.biteandsip.exceptions;

public abstract class UserTokenException extends RuntimeException{
    public UserTokenException(String message){
        super(message);
    }
}
