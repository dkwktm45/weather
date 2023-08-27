package com.weather.api.config;

import com.weather.api.error.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static com.weather.api.error.type.ErrorCode.TYPE_MISMATCH;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ErrorResponse handleAllException(MethodArgumentTypeMismatchException e) {
    return new ErrorResponse(TYPE_MISMATCH, e.getName());
  }
}
