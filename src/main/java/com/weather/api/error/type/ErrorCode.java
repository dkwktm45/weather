package com.weather.api.error.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  TYPE_MISMATCH("사용자가 없습니다."),
  DIARY_NOT_FOUND("다이어리를 찾을 수 없습니다.");

  private final String description;

}
