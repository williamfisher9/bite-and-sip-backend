package com.apps.biteandsip.exceptions;

import com.apps.biteandsip.dto.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@RestControllerAdvice
public class ControllerExceptionsHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ResponseMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        List<String> errors = exc.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ResponseMessage responseMessage = new ResponseMessage(errors, 400);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ResponseMessage> handleDuplicateUsernameException(DuplicateUsernameException exc){
        ResponseMessage responseMessage = new ResponseMessage(exc.getMessage(), 400);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }
}
