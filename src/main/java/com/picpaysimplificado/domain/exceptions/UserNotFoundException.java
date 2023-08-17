package com.picpaysimplificado.domain.exceptions;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String mensagem) {
        super(mensagem);
    }

    public UserNotFoundException(Long id) {
        this(String.format("Não existe um usuário para o id: %d", id));
    }

}
