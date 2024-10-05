package com.electronicstore.exceptions;

public class BadApiRequestException extends RuntimeException{
    public BadApiRequestException(String msg){
        super(msg);
    }

    public BadApiRequestException(){
        super("Bad request...");
    }
}
