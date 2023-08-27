package com.weather.api.controller;

import com.weather.api.domain.Diary;
import com.weather.api.service.DiaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {

  @MockBean
  private DiaryService diaryService;
  @Autowired
  private MockMvc mockMvc;

  @Test
  void createDiary_success() throws Exception {
    // given
    LocalDate date = LocalDate.parse("2023-08-26");
    String text = "Your diary text goes here";
    doNothing().when(diaryService).createDiary(date, text);

    // when
    mockMvc.perform(post("/create/diary")
            .param("date", date.toString())
            .content(text)
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void createDiary_fail() throws Exception {
    // when
    mockMvc.perform(post("/create/diary")
            .param("date", "1234")
            .content("text")
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.errorCode").value("TYPE_MISMATCH"))
        .andExpect(jsonPath("$.name").value("date"));
  }

  @Test
  void readDiary_success() throws Exception {
    // given
    Diary diary1 = Diary.builder()
        .date(LocalDate.now())
        .temperature(10.1)
        .weather("좋음").build();
    Diary diary2 = Diary.builder()
        .date(LocalDate.now())
        .temperature(10.1)
        .weather("나쁨").build();
    Diary diary3 = Diary.builder()
        .date(LocalDate.now())
        .temperature(10.1)
        .weather("보통").build();
    List<Diary> diaryList = new ArrayList<>();
    diaryList.add(diary1);
    diaryList.add(diary2);
    diaryList.add(diary3);
    LocalDate date = LocalDate.now();
    given(diaryService.readDiary(date))
        .willReturn(diaryList);

    mockMvc.perform(get("/read/diary")
            .param("date", date.toString())
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].date").value(date.toString()))
        .andExpect(jsonPath("$[0].weather").value("좋음"))
        .andExpect(jsonPath("$[1].date").value(date.toString()))
        .andExpect(jsonPath("$[1].weather").value("나쁨"))
        .andExpect(jsonPath("$[2].date").value(date.toString()))
        .andExpect(jsonPath("$[2].weather").value("보통"));
  }

  @Test
  void readDiary_fail() throws Exception {
    // when
    mockMvc.perform(get("/read/diary")
            .param("date", "1234")
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.errorCode").value("TYPE_MISMATCH"))
        .andExpect(jsonPath("$.name").value("date"));
  }
  @Test
  void readDiaries_success() throws Exception {
    LocalDate day1 = LocalDate.of(2023, 1, 20);
    LocalDate day2 = LocalDate.of(2023, 2, 20);
    // given
    Diary diary1 = Diary.builder()
        .date(day1)
        .temperature(10.1)
        .weather("좋음").build();
    Diary diary2 = Diary.builder()
        .date(day2)
        .temperature(10.1)
        .weather("나쁨").build();
    List<Diary> diaryList = new ArrayList<>();
    diaryList.add(diary1);
    diaryList.add(diary2);
    given(diaryService.readDiaries(day1, day2))
        .willReturn(diaryList);

    mockMvc.perform(get("/read/diaries")
            .param("startDate", day1.toString())
            .param("endDate", day2.toString())
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].date").value(day1.toString()))
        .andExpect(jsonPath("$[0].weather").value("좋음"))
        .andExpect(jsonPath("$[1].date").value(day2.toString()))
        .andExpect(jsonPath("$[1].weather").value("나쁨"));
  }

  @Test
  void updateDiary_success() throws Exception {
    // given
    LocalDate day = LocalDate.of(2023, 1, 20);
    String text = "수정 하는 날짜!";
    doNothing().when(diaryService).updateDiary(day, text);

    mockMvc.perform(put("/update/diary")
            .param("date", day.toString())
            .content(text)
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk());

  }

  @Test
  void deleteDiary() throws Exception {
    // given
    LocalDate day = LocalDate.of(2023, 1, 20);
    doNothing().when(diaryService).deleteDiary(day);

    mockMvc.perform(delete("/delete/diary")
            .param("date", day.toString())
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk());
  }
}