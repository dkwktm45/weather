package com.weather.api.controller;

import com.weather.api.domain.Diary;
import com.weather.api.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequiredArgsConstructor
public class DiaryController {
  private final DiaryService diaryService;
  @PostMapping("/create/diary")
  @ApiOperation(value = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장", notes = "이것은 노트")
  public void createDiary(@RequestParam @DateTimeFormat(iso =
      DATE)
                   LocalDate date,
                   @RequestBody String text) {
    diaryService.createDiary(date, text);
  }

  @GetMapping("/read/diary")
  @ApiOperation("선택한 날짜에 대한 모든 일기를 가져옵니다.")
  public List<Diary> readDiary(@RequestParam @DateTimeFormat(iso =
      DATE) LocalDate date){
    return diaryService.readDiary(date);
  }

  @GetMapping("/read/diaries")
  @ApiOperation("선택한 날짜내에서 대한 모든 일기를 가져옵니다.")
  public List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso =
      DATE) @ApiParam(value = "조회할 기간의 첫번째날", example = "2022-01-20") LocalDate startDate,
                                 @RequestParam @DateTimeFormat(iso =
      DATE) @ApiParam(value = "조회할 기간의 마지막날", example = "2022-01-20") LocalDate endDate) {
    return diaryService.readDiaries(startDate, endDate);
  }

  @PutMapping("/update/diary")
  @ApiOperation("선택한 날짜에 대한 일기의 텍스트를 수정합니다")
  void updateDiary(@RequestParam @DateTimeFormat(iso =
      DATE) LocalDate date,
                   @RequestBody String text) {
    diaryService.updateDiary(date, text);
  }

  @DeleteMapping("/delete/diary")
  @ApiOperation("선택한 날짜에 대한 일기를 삭제합니다.")
  void deleteDiary(@RequestParam @DateTimeFormat(iso = DATE) LocalDate date) {
    diaryService.deleteDiary(date);
  }
}
