package com.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String resourceField, Object id){
        super(resourceField+" not found with id : "+id);
    }
}
