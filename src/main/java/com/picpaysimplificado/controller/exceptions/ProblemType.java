package com.picpaysimplificado.controller.exceptions;

import lombok.Getter;

@Getter
public enum ProblemType {

    ERRO_DE_SISTEMA("/erro-de-sistema", "Erro de sistema"),
    PARAMETRO_INVALIDO("/parametro-invalido", "Parâmetro inválido"),
    MENSAGEM_INCOMPREENSIVEL("/mensagem-incompreensivel", "Mensagem incompreensível"),
    RECURSO_NAO_ENCONTRADO("/recurso-nao-encontrado", "Recurso não encontrado"),
    ENTITY_IN_USE("/entity-in-use", "Entidade em uso"),
    ENTITY_ALREADY_EXISTS("/entity-already-exist", "Entidade já cadastrada"),
    ENTITY_NOT_FOUND("/entity-not-found", "Entidade não encontrada"),
    USER_WITHOUT_BALANCE("/user-without-balance", "Usuário sem saldo disponível");

    private String title;
    private String uri;

    ProblemType(String path, String title){
        this.uri = "https://picpay.com" + path;
        this.title = title;
    }
}
