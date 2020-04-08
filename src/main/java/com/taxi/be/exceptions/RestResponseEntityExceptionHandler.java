package com.taxi.be.exceptions;

import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleHttpMediaTypeNotSupportedException(HttpServletRequest request, Throwable ex) {
        return new ResponseEntity<>(new JsonResponse("json data required", 415), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleHttpMessageNotReadableException(HttpServletRequest request, Throwable ex) {
        return new ResponseEntity<>(new JsonResponse("malformed submission",400),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResultException.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleNoResultException(Throwable x) {
        return new ResponseEntity<>(new JsonResponse("requested map does not exist",400),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    ResponseEntity<JsonResponse> handleGenericException() {
        return new ResponseEntity<>(new JsonResponse("internal error",500),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}