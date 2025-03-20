package com.apps.biteandsip.exceptions;

public class AuthorizationHeaderNotFoundException extends RuntimeException{
    public AuthorizationHeaderNotFoundException(String message){
        super(message);
    }
}
