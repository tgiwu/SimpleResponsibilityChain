package com.ap.exceptions;

public class ClassAbstractException extends RuntimeException {
    public ClassAbstractException(String fullName) {
        super("class is abstract \n" + fullName);
    }
}
