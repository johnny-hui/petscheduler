package com.johnny.petscheduler.exceptions.handlers;

import com.johnny.petscheduler.exceptions.*;
import com.johnny.petscheduler.exceptions.generic.ErrorCode;
import com.johnny.petscheduler.exceptions.generic.ErrorResponse;
import com.johnny.petscheduler.utils.ValidationUtil;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.Instant;

import static com.johnny.petscheduler.exceptions.generic.ErrorConstants.*;
import static com.johnny.petscheduler.security.configs.SecurityConstants.*;

/**
 * A global exception handler that catches and handles all exceptions that occur at controller,
 * service, DAO/repository and database level.
 *
 * @implNote Does not handle exceptions thrown at Spring Security filter levels.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(InvalidAvailabilityDateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAvailabilityDateException(final InvalidAvailabilityDateException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.INVALID_AVAILABILITY,
                ERROR_TITLE_INVALID_AVAILABILITY,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidJWTException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJWTException(final InvalidJWTException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ErrorCode.UNAUTHORIZED,
                UNAUTHORIZED_ERROR_TITLE,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJWTException(final JwtException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ErrorCode.UNAUTHORIZED,
                UNAUTHORIZED_ERROR_TITLE,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleMissingRefreshTokenException(final MissingRefreshTokenException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ErrorCode.UNAUTHORIZED,
                MISSING_JWT_REFRESH_ERROR_TITLE,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenNotFoundException(final RefreshTokenNotFoundException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ErrorCode.UNAUTHORIZED,
                JWT_REFRESH_NOT_FOUND_TITLE,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PetLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handlePetLimitExceededException(final PetLimitExceededException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                ErrorCode.PET_COUNT_EXCEEDED,
                ERROR_TITLE_PET_COUNT_EXCEEDED,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ExceptionHandler(UserProfileCannotBeLoadedException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileCannotBeLoadedException(final UserProfileCannotBeLoadedException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                ErrorCode.USER_PROFILE_CANNOT_BE_LOADED,
                ERROR_TITLE_USER_PROFILE_ERROR,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ExceptionHandler(PetBulkCreateEmptyException.class)
    public ResponseEntity<ErrorResponse> handleEmptyBulkCreatePetException(final PetBulkCreateEmptyException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                ErrorCode.PET_EMPTY_LIST,
                ERROR_TITLE_PET_CREATE,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePetNotFoundException(final PetNotFoundException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ErrorCode.PET_NOT_FOUND,
                ERROR_TITLE_PET_NOT_FOUND,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(final EmailAlreadyExistsException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ErrorCode.EMAIL_ALREADY_EXISTS,
                ERROR_TITLE_EMAIL_ALREADY_EXISTS,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EstablishmentYearInvalidException.class)
    public ResponseEntity<ErrorResponse> handleEstablishmentYearInvalidException(final EstablishmentYearInvalidException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.INVALID_EST_YEAR,
                ERROR_TITLE_INVALID_EST_YEAR,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(final UserNotFoundException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ErrorCode.USER_NOT_FOUND,
                ERROR_TITLE_USER_NOT_FOUND,
                ex.getMessage(),
                null,
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(final MethodArgumentNotValidException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_VALIDATION,
                ERROR_TITLE_BAD_VALIDATION,
                ERROR_MESSAGE_BAD_VALIDATION,
                ValidationUtil.parseJPAValidationErrors(ex),
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodParameterValidationException(final HandlerMethodValidationException ex)
    {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ErrorCode.BAD_VALIDATION,
                ERROR_TITLE_BAD_VALIDATION,
                ERROR_MESSAGE_BAD_VALIDATION,
                ValidationUtil.parseJPAValidationErrors(ex),
                Instant.now()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJacksonInvalidEnum(final HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if(cause instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType().isEnum())
        {
            ErrorResponse body = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    ErrorCode.INVALID_ENUM_VALUE,
                    ERROR_TITLE_INVALID_ENUM_VALUE,
                    ValidationUtil.parseJacksonInvalidEnumErrors(invalidFormatException),
                    null,
                    Instant.now()
            );
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * A general error handler that handles any exceptions generated by the server
     * and provides a structured error response.
     *
     * @see ResponseEntity
     *
     * @apiNote ResponseEntity's structure
     *      HTTP Status -> 200
     *      HTTP Headers -> Bearer: token
     *      Body: {
     *          "json_field": 123
     *      }
     *
     * @param ex
     *      The thrown exception that was caught
     *
     * @return problem
     *      A ProblemDetail object
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(final Exception ex) {
        log.error("[+] An unexpected error has occurred", ex);

        if(ex instanceof BadCredentialsException) {
            ErrorResponse body = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    ErrorCode.BAD_CREDENTIALS,
                    ERROR_TITLE_BAD_CREDENTIALS,
                    ERROR_MESSAGE_BAD_CREDENTIALS,
                    null,
                    Instant.now()
            );
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        }

        if(ex instanceof AccountStatusException) {
            ErrorResponse body = new ErrorResponse(
                    HttpStatus.FORBIDDEN.value(),
                    ErrorCode.BAD_ACCOUNT_STATUS,
                    ERROR_TITLE_BAD_ACCOUNT_STATUS,
                    ERROR_MESSAGE_BAD_ACCOUNT_STATUS,
                    null,
                    Instant.now()
            );
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        }

        if(ex instanceof AccessDeniedException) {
            ErrorResponse body = new ErrorResponse(
                    HttpStatus.FORBIDDEN.value(),
                    ErrorCode.ACCESS_DENIED,
                    ERROR_TITLE_ACCESS_DENIED,
                    ERROR_MESSAGE_ACCESS_DENIED,
                    null,
                    Instant.now()
            );
            return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        }

        // Handler for general exceptions
        if(ex != null) {
            ErrorResponse body = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    ERROR_TITLE_INTERNAL_SERVER_ERROR,
                    ex.getMessage(),
                    null,
                    Instant.now()
            );
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
