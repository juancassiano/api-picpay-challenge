package com.picpaysimplificado.domain.exceptions;

public class UserWithoutPermissionException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public UserWithoutPermissionException(String message) {
        super(message);
    }
}
