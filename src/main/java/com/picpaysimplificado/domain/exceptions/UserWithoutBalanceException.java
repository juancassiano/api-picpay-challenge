package com.picpaysimplificado.domain.exceptions;

public class UserWithoutBalanceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public UserWithoutBalanceException(String message) {
        super(message);
    }
}
