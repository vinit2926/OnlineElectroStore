package com.electronicstore.exceptions;

import lombok.Builder;

@Builder
public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(){
        super("Resource not found");
    }
    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
