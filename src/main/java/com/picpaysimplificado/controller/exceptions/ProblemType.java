package com.picpaysimplificado.controller.exceptions;

import lombok.Getter;

@Getter
public enum ProblemType {

    ERRO_DE_SISTEMA("/erro-de-sistema", "Erro de sistema"),
    PARAMETRO_INVALIDO("/parametro-invalido", "Parâmetro inválido"),
    MENSAGEM_INCOMPREENSIVEL("/mensagem-incompreensivel", "Mensagem incompreensível"),
    RECURSO_NAO_ENCONTRADO("/recurso-nao-encontrado", "Recurso não encontrado"),
    ENTIDADE_EM_USO("/entidade-em-uso", "Entidade em uso"),
    ENTIDADE_JA_CADASTRADA("/entidade-ja-cadastrada", "Entidade já cadastrada"),
    ENTIDADE_NAO_ENCONTRADA("/entidade-nao-encontrada", "Entidade não encontrada"),
    USUARIO_SEM_SALDO("/usuario-sem-saldo", "Usuário sem saldo disponível"),
    USUARIO_SEM_PERMISSAO("/usuario-sem-saldo", "Usuário sem permissão");

    private String title;
    private String uri;

    ProblemType(String path, String title){
        this.uri = "https://picpay.com" + path;
        this.title = title;
    }
}
