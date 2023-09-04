package com.weather.api.service;

import com.weather.api.domain.DateWeather;
import com.weather.api.domain.Diary;
import com.weather.api.repository.DateWeatherRepository;
import com.weather.api.repository.DiaryRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

  @Mock
  private DiaryRepository diaryRepository;
  @Mock
  private DateWeatherRepository dateWeatherRepository;
  @InjectMocks
  private DiaryService diaryService;

  @Test
  void saveWeatherDate() {

  }
  @Test
  void createDiary_weather_exist() {
    DateWeather dateWeather = DateWeather.builder()
        .date(LocalDate.now())
        .icon("testIcon")
        .weather("bad")
        .build();

    List<DateWeather> dateWeathers = new ArrayList<>();
    dateWeathers.add(dateWeather);
    given(dateWeatherRepository.findAllByDate(LocalDate.now()))
        .willReturn(dateWeathers);

    ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);
    //when
    DateWeather dateWeather1 =
        diaryService.getDateWeather(dateWeather.getDate());
    diaryService.createDiary(dateWeather1.getDate(), "text");

    //then
    verify(diaryRepository, times(1)).save(captor.capture());
  }

  @Test
  void readDiary_exit() {
    List<Diary> diaries = List.of(
        Diary.builder().text("test1").build(),
        Diary.builder().text("test2").build(),
        Diary.builder().text("test3").build()
    );

    given(diaryRepository.findAllByDate(any())).willReturn(diaries);

    //when
    diaryService.readDiary(LocalDate.now());

    //then
    verify(diaryRepository, times(1)).findAllByDate(LocalDate.now());
    assertEquals(diaries.size(), 3);
    assertEquals(diaries.get(0).getText(), "test1");
    assertEquals(diaries.get(1).getText(), "test2");
    assertEquals(diaries.get(2).getText(), "test3");
  }

  @Test
  void readDiary_not_exit() {
    List<Diary> diaries = new ArrayList<>();
    given(diaryRepository.findAllByDate(any())).willReturn(diaries);

    //when
    diaryService.readDiary(LocalDate.now());

    //then
    verify(diaryRepository, times(1)).findAllByDate(LocalDate.now());
    assertEquals(diaries.size(), 0);
  }

  @Test
  void readDiaries() {
    List<Diary> diaries = List.of(
        Diary.builder().text("test1").build(),
        Diary.builder().text("test2").build(),
        Diary.builder().text("test3").build()
    );
    LocalDate start = LocalDate.of(2023, 1, 20);
    LocalDate end = LocalDate.of(2023, 3, 20);
    given(diaryRepository.findAllByDateBetween(start, end)).willReturn(diaries);

    //when
    diaryService.readDiaries(start,end);

    //then
    verify(diaryRepository, times(1)).findAllByDateBetween(start, end);
    assertEquals(diaries.size(), 3);
    assertEquals(diaries.get(0).getText(), "test1");
    assertEquals(diaries.get(1).getText(), "test2");
    assertEquals(diaries.get(2).getText(), "test3");
  }

  @Test
  void updateDiary() {
    //given
    Diary diary = Diary.builder().text("test1").build();
    LocalDate date = LocalDate.of(2023, 1, 20);
    given(diaryRepository.getFirstByDate(date)).willReturn(Optional.of(diary));
    given(diaryRepository.save(diary)).willReturn(diary);

    //when
    diaryService.updateDiary(date,"텍스트 수정");

    //then
    verify(diaryRepository, times(1)).getFirstByDate(date);
    verify(diaryRepository, times(1)).save(diary);
  }

  @Test
  void deleteDiary() {
    //given
    LocalDate date = LocalDate.of(2023, 1, 20);

    //when
    diaryService.deleteDiary(date);

    //then
    verify(diaryRepository, times(1)).deleteAllByDate(date);
  }
}
@SpringBootTest @ActiveProfiles("test")
class DiaryService_Integrantion{
  @Autowired
  private DiaryService diaryService;
  @MockBean
  private DiaryRepository diaryRepository;
  @MockBean DateWeatherRepository dateWeatherRepository;
  @Test
  void createDiary_weather_not_exist() {
    DateWeather dateWeather = DateWeather.builder()
        .date(LocalDate.now())
        .icon("testIcon")
        .weather("bad")
        .build();
    ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);
    //when
    diaryService.createDiary(dateWeather.getDate(), "text");

    //then
    verify(diaryRepository, times(1)).save(captor.capture());
  }

  @Test
  void create_weather_schedule() {
    ArgumentCaptor<DateWeather> captor = ArgumentCaptor.forClass(DateWeather.class);

    Awaitility.await()
        .atMost(4, TimeUnit.SECONDS)
        .untilAsserted(()-> {
          //when
          diaryService.saveWeatherDate();
          //then
          verify(dateWeatherRepository, times(4)).save(captor.capture());
        });
  }
  @Test
  void saveWeatherDate(){
    //when
    diaryService.saveWeatherDate();

    //then
    verify(dateWeatherRepository).save(any());
  }
}