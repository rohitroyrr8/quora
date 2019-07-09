package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(SignUpRestrictedException ex, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse()
                        .code(ex.getCode())
                        .message(ex.getErrorMessage())
                , HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException ex, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse()
                        .code(ex.getCode())
                        .message(ex.getErrorMessage())
                , HttpStatus.UNAUTHORIZED);
    }
}
