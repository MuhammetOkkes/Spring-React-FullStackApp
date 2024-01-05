package com.hoaxify.ws.user;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hoaxify.ws.error.ApiError;
import com.hoaxify.ws.shared.GenericMessage;

import jakarta.validation.Valid;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/api/v1/users")
    GenericMessage createUser(@Valid @RequestBody User user) {
        userService.save(user);
        return new GenericMessage("User is created");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> metod(MethodArgumentNotValidException exception) {
        ApiError apiError = new ApiError();
        apiError.setPath("/api/v1/users");
        apiError.setMessage("Validation Errors");
        apiError.setStatus(400);
        /*
         * Map<String,String> validationErrors = new HashMap<>();
         * 
         * for (var error : exception.getBindingResult().getAllErrors()) {
         * if(error instanceof FieldError) {
         * FieldError fieldError = (FieldError) error;
         * validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
         * }
         * }
         */

        var validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (existing, replacing) -> existing));

        apiError.setValidationErrors(validationErrors);
        return ResponseEntity.badRequest().body(apiError);
    }

}
