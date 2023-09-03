package com.weather.api.error;

import com.weather.api.error.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryException extends RuntimeException{
  private ErrorCode errorCode;
  private String errorMessage;

  public DiaryException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.errorMessage = errorCode.getDescription();
  }
}
