package com.krs.exception;

import com.krs.service.sort.model.CheckStatus404Response;
import com.krs.service.sort.model.SubmitTask400Response;
import com.krs.service.sort.model.SubmitTask500Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(TaskDoesNotExistException.class)
    public ResponseEntity<CheckStatus404Response> handleTaskDoesNotExist(TaskDoesNotExistException ex) {
        CheckStatus404Response taskNotFoundResponse = new CheckStatus404Response(ex.getMessage());

        log.error("TaskDoesNotExistException: {}", taskNotFoundResponse.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(taskNotFoundResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SubmitTask400Response> handleInvalidSortingAlgorithm(IllegalArgumentException ex) {
        SubmitTask400Response invalidRequestResponse = new SubmitTask400Response(ex.getMessage());

        log.error("IllegalArgumentException: {}", invalidRequestResponse.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invalidRequestResponse);
    }

    @ExceptionHandler(InvalidThreadNumberException.class)
    public ResponseEntity<SubmitTask400Response> handleInvalidThreadNumber(InvalidThreadNumberException ex) {
        SubmitTask400Response invalidRequestResponse = new SubmitTask400Response(ex.getMessage());

        log.error("InvalidThreadNumberException: {}", invalidRequestResponse.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(invalidRequestResponse);
    }

    @ExceptionHandler(AwsException.class)
    public ResponseEntity<SubmitTask500Response> handleAwsException(AwsException ex) {
        SubmitTask500Response internalServerErrorResponse = new SubmitTask500Response(ex.getCause() + " " + ex.getMessage());

        log.error("AwsException: {}", internalServerErrorResponse.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(internalServerErrorResponse);
    }
}
