package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException ex, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse()
                        .code(ex.getCode())
                        .message(ex.getErrorMessage())
                , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException ex, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse()
                        .code(ex.getCode())
                        .message(ex.getErrorMessage())
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> signOutRestrictedException(SignOutRestrictedException ex, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse()
                        .code(ex.getCode())
                        .message(ex.getErrorMessage())
                , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException ex, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse()
                        .code(ex.getCode())
                        .message(ex.getErrorMessage())
                , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException ex, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse()
                        .code(ex.getCode())
                        .message(ex.getErrorMessage())
                , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequestException(BadRequestException ex, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse()
                        .code(ex.getCode())
                        .message(ex.getErrorMessage())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> badRequestException(InvalidQuestionException ex, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse()
                        .code(ex.getCode())
                        .message(ex.getErrorMessage())
                , HttpStatus.NOT_FOUND);
    }

}
