package com.seniorway.seniorway.exception;

import com.seniorway.seniorway.enums.user.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, String>> buildResponse(ErrorCode code, String message) {
        return ResponseEntity.status(code.getHttpStatus())
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(Map.of(
                        "code", code.name(),
                        "message", message
                ));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex) {
        return buildResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}