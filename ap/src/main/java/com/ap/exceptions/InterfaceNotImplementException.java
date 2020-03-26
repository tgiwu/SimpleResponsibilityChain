package com.ap.exceptions;

public class InterfaceNotImplementException extends RuntimeException {

    public InterfaceNotImplementException(String fullName) {
        super("A Interceptor should implement com.mine.libbackup.responsibility.Interceptor! \n" + fullName);
    }
}
