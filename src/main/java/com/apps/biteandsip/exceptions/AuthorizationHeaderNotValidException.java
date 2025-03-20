package com.apps.biteandsip.exceptions;

public class AuthorizationHeaderNotValidException extends RuntimeException {
    public AuthorizationHeaderNotValidException(String message){
        super(message);
    }
}
