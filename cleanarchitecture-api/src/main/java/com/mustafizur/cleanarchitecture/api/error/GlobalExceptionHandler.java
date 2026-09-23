package com.mustafizur.cleanarchitecture.api.error;

import com.mustafizur.cleanarchitecture.core.exception.ConflictException;
import com.mustafizur.cleanarchitecture.core.exception.DomainException;
import com.mustafizur.cleanarchitecture.core.exception.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail handleNotFound(NotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Resource not found", exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    ProblemDetail handleConflict(ConflictException exception) {
        return problem(HttpStatus.CONFLICT, "Conflict", exception.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    ProblemDetail handleDomain(DomainException exception) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Domain rule violation", exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrity(DataIntegrityViolationException exception) {
        return problem(
                HttpStatus.CONFLICT,
                "Data conflict",
                "The requested change conflicts with an existing resource or database constraint");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ProblemDetail handleOptimisticLocking(ObjectOptimisticLockingFailureException exception) {
        return problem(
                HttpStatus.CONFLICT,
                "Concurrent modification",
                "The resource changed while this request was being processed; reload it and retry");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleUnreadableBody(HttpMessageNotReadableException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Malformed request", "The request body could not be parsed");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolation(ConstraintViolationException exception) {
        var errors = exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (left, right) -> left));
        var detail = problem(HttpStatus.BAD_REQUEST, "Validation failed", "One or more values are invalid");
        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleBodyValidation(MethodArgumentNotValidException exception) {
        var errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() == null ? "invalid value" : error.getDefaultMessage(),
                        (left, right) -> left));
        var detail = problem(HttpStatus.BAD_REQUEST, "Validation failed", "Request body is invalid");
        detail.setProperty("errors", errors);
        return detail;
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        var problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("urn:cleanarchitecture:problem:" + status.value()));
        return problem;
    }
}
