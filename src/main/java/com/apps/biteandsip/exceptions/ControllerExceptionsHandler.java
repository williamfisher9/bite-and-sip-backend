package com.apps.biteandsip.exceptions;

import com.apps.biteandsip.dto.ResponseMessage;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.relation.RoleNotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;
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

    @ExceptionHandler(FoodCategoryNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleFoodCategoryNotFoundException(FoodCategoryNotFoundException exc){
        ResponseMessage responseMessage = new ResponseMessage(exc.getMessage(), 404);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleCouponNotFoundException(CouponNotFoundException exc){
        ResponseMessage responseMessage = new ResponseMessage(exc.getMessage(), 404);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @ExceptionHandler(FoodItemNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleFoodItemNotFoundException(FoodItemNotFoundException exc){
        ResponseMessage responseMessage = new ResponseMessage(exc.getMessage(), 404);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @ExceptionHandler(UserTokenException.class)
    public ResponseEntity<ResponseMessage> handleUserTokenException(UserTokenException exc){
        ResponseMessage responseMessage = new ResponseMessage(exc.getMessage(), 400);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ResponseMessage> handleMessagingException(MessagingException exc){
        ResponseMessage responseMessage = new ResponseMessage(exc.getMessage(), 400);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ResponseMessage> handleStripeException(StripeException exc){
        ResponseMessage responseMessage = new ResponseMessage(exc.getMessage(), 400);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ResponseMessage> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exc){
        ResponseMessage responseMessage = new ResponseMessage(exc.getMessage().contains("Duplicate") ? "Record with the same identifier exists" : exc.getMessage(), 400);
        return new ResponseEntity<>(responseMessage, HttpStatus.valueOf(responseMessage.getStatus()));
    }

}
