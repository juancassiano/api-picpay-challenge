package com.picpaysimplificado.controller.exceptions;

import com.picpaysimplificado.domain.exceptions.UserWithoutBalanceException;
import com.picpaysimplificado.domain.exceptions.UserWithoutPermissionException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {



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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleEntityAlreadyExists(DataIntegrityViolationException ex, WebRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        ProblemType problemType = ProblemType.ENTITY_ALREADY_EXISTS;
        String detail = ex.getMessage();
        Problem problem = createProblemBuilder(status,problemType,detail).build();

        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);

    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex, WebRequest request){

        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemType problemType = ProblemType.ENTITY_NOT_FOUND;
        String detail = ex.getMessage();
        Problem problem = createProblemBuilder(status,problemType,detail).build();

        return handleExceptionInternal(ex,problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(UserWithoutBalanceException.class)
    public ResponseEntity<Object> threatUserWithouwBalance(Exception ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.USER_WITHOUT_BALANCE;
        String detail = ex.getMessage();
        Problem problem = createProblemBuilder(status,problemType,detail).build();

        return handleExceptionInternal(ex,problem, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(UserWithoutPermissionException.class)
    public ResponseEntity<Object> threatUserWithoutPermissionException(Exception ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemType problemType = ProblemType.USER_WITHOUT_BALANCE;
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

}