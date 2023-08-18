package com.picpaysimplificado.controller.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;
import com.picpaysimplificado.domain.exceptions.UserNotFoundException;
import com.picpaysimplificado.domain.exceptions.UserWithoutBalanceException;
import com.picpaysimplificado.domain.exceptions.UserWithoutPermissionException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {


    public static final String MSG_GENERIC_ERROR = "Ocorreu um erro interno inesperado no sistema. Tente novamente e se "
            + "o problema persistir, entre em contato com o administrador do sistema.";

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                      HttpStatusCode status, WebRequest request) {

        if(body == null){
            body = Problem.builder()
                    .timestamp(LocalDateTime.now())
                    .title(status.toString())
                    .status(status.value())
                    .build();
        }else if(body instanceof String){
            body = Problem.builder()
                    .timestamp(LocalDateTime.now())
                    .title((String) body)
                    .status(status.value())
                    .build();
        }
        return super.handleExceptionInternal(ex,body,headers,status,request);

    }


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Throwable rootCause = ExceptionUtils.getRootCause(ex);

        if(rootCause instanceof InvalidFormatException){
            return handleInvalidFormat( (InvalidFormatException) rootCause, headers, status, request);
        }else if(rootCause instanceof PropertyBindingException){
            return handlePropertyBinding( (PropertyBindingException) rootCause, headers, status, request);
        }
        HttpStatus statusCode = HttpStatus.valueOf(status.value());
        ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
        String detail = "O corpo da requisição está inválido. Verifique erro de sintaxe.";

        Problem problem = createProblemBuilder(statusCode, problemType, detail).build();

        return handleExceptionInternal(ex, problem, headers, statusCode, request);
    }

    private ResponseEntity<Object> handlePropertyBinding(PropertyBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpStatus statusCode = HttpStatus.valueOf(status.value());
        String path = joinPath(ex.getPath());

        ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
        String detail = String.format("A propriedade '%s' não existe. "
                + "Corrija ou remova essa propriedade e tente novamente.", path);

        Problem problem = createProblemBuilder(statusCode, problemType, detail).build();

        return handleExceptionInternal(ex, problem, headers, statusCode, request);
    }

    private ResponseEntity<Object> handleInvalidFormat(InvalidFormatException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpStatus statusCode = HttpStatus.valueOf(status.value());

        String path = joinPath(ex.getPath());

        ProblemType problemType = ProblemType.MENSAGEM_INCOMPREENSIVEL;
        String detail = String.format("A propriedade '%s' recebeu o valor '%s', "
                + "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
        path, ex.getValue(), ex.getTargetType().getSimpleName());

        Problem problem = createProblemBuilder(statusCode, problemType, detail).build();

        return handleExceptionInternal(ex,problem, headers, statusCode,request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpStatus statusCode = HttpStatus.valueOf(status.value());
        ProblemType problemType = ProblemType.RECURSO_NAO_ENCONTRADO;
        String detail = String.format("O recurso %s, que você tentou acessar, é inexistente.",
                ex.getRequestURL());
        Problem problem = createProblemBuilder(statusCode, problemType, detail).build();
        return handleExceptionInternal(ex, problem, headers, statusCode, request);

    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if(ex instanceof MethodArgumentTypeMismatchException){
            return handleMethodArgumentTypeMismatch((MethodArgumentTypeMismatchException) ex, headers, (HttpStatus) status, request);
        }
        return super.handleTypeMismatch(ex, headers, status, request);
    }

    private ResponseEntity<Object> handleMethodArgumentTypeMismatch (MethodArgumentTypeMismatchException ex, HttpHeaders headers,
    HttpStatus status, WebRequest request){
        ProblemType problemType = ProblemType.PARAMETRO_INVALIDO;
        String detail = String.format("O parâmetro de URL '%s' recebeu o valor '%s', "
                        + "que é de um tipo inválido. Corrija e informe um valor compatível com o tipo %s.",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());

        Problem problem = createProblemBuilder(status,problemType,detail).build();
        return handleExceptionInternal(ex, problem, headers, status, request);

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleEntityAlreadyExists(DataIntegrityViolationException ex, WebRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        ProblemType problemType = ProblemType.ENTIDADE_JA_CADASTRADA;
        String detail = ex.getMessage();
        Problem problem = createProblemBuilder(status,problemType,detail).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaughtException(Exception ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemType problemType = ProblemType.ERRO_DE_SISTEMA;
        String detail = MSG_GENERIC_ERROR;
        ex.printStackTrace();

        Problem problem = createProblemBuilder(status, problemType, detail).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex, WebRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemType problemType = ProblemType.ENTIDADE_NAO_ENCONTRADA;
        String detail = ex.getMessage();
        Problem problem = createProblemBuilder(status,problemType,detail).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);

    }

    @ExceptionHandler(UserWithoutBalanceException.class)
    public ResponseEntity<Object> threatUserWithouwBalance(Exception ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.USUARIO_SEM_SALDO;
        String detail = ex.getMessage();
        Problem problem = createProblemBuilder(status,problemType,detail).build();

        return handleExceptionInternal(ex,problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(UserWithoutPermissionException.class)
    public ResponseEntity<Object> threatUserWithoutPermissionException(Exception ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.USUARIO_SEM_PERMISSAO;
        String detail = ex.getMessage();
        Problem problem = createProblemBuilder(status,problemType,detail).build();

        return handleExceptionInternal(ex,problem, new HttpHeaders(), status, request);
    }

    private Problem.ProblemBuilder createProblemBuilder(HttpStatus status, ProblemType problemType, String detail){
        return Problem.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .type(problemType.getUri())
                .title(problemType.getTitle())
                .detail(detail);
    }

    private String joinPath(List<Reference> references){
        return references.stream()
                .map(ref -> ref.getFieldName())
                .collect(Collectors.joining("."));
    }
}
