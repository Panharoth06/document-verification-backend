package com.raidenz.doucmentverification.exception.customException;

public class ResourceNotFoundException extends ServiceException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
