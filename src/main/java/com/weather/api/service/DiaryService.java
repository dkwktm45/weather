package com.weather.api.service;

import com.weather.api.ApiApplication;
import com.weather.api.domain.DateWeather;
import com.weather.api.domain.Diary;
import com.weather.api.repository.DateWeatherRepository;
import com.weather.api.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service @Transactional
@RequiredArgsConstructor
public class DiaryService {

  @Value("${openweathermap.key}")
  private String apiKey;

  private static final Logger logger =
      LoggerFactory.getLogger(ApiApplication.class);
  private final DiaryRepository diaryRepository;
  private final DateWeatherRepository dateWeatherRepository;
  @Scheduled(cron = "0 0 1 * * *")
  public void saveWeatherDate() {
    dateWeatherRepository.save(getWeatherFromApi());
  }

  @Transactional(readOnly = true)
  public List<Diary> readDiary(LocalDate date) {
    logger.info("find diary");
    return diaryRepository.findAllByDate(date);
  }
  @Transactional
  public void deleteDiary(LocalDate date) {
    diaryRepository.deleteAllByDate(date);
  }

  @Transactional(readOnly = true)
  public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
    return diaryRepository.findAllByDateBetween(startDate, endDate);
  }
  public void createDiary(LocalDate date, String text) {
    logger.info("create diary");
    DateWeather dateWeather = getDateWeather(date);

    Diary nowDiary = new Diary();
    nowDiary.setDateWeather(dateWeather);
    nowDiary.setText(text);
    diaryRepository.save(nowDiary);
  }
  public DateWeather getWeatherFromApi() {
    // open weather map에서 날씨 데이터 가져오기
    String weatherData = getWeatherString();

    // 받아온 날씨 json 파싱하기
    Map<String, Object> parsedWeather = parseWeather(weatherData);
    DateWeather dateWeather = new DateWeather();
    dateWeather.setDate(LocalDate.now());
    dateWeather.setWeather(parsedWeather.get("main").toString());
    dateWeather.setIcon(parsedWeather.get("icon").toString());
    dateWeather.setTemperature((Double) parsedWeather.get("temp"));
    return dateWeather;
  }

  public DateWeather getDateWeather(LocalDate date) {
    List<DateWeather> dateWeathers = dateWeatherRepository.findAllByDate(date);
    if (dateWeathers.size() == 0) {
      // 새로 api에서 날씨 정보를 가져와야한다.
      return getWeatherFromApi();
    } else {
      return dateWeathers.get(0);
    }
  }

  public void updateDiary(LocalDate date, String text) {
    Diary nowDiary = diaryRepository.getFirstByDate(date);
    nowDiary.setText(text);
    diaryRepository.save(nowDiary);
  }

  public String getWeatherString() {
    String apiUrl = "https://api.openweathermap.org/data/2" +
        ".5/weather?q=seoul&appid=" + apiKey;
    try {
      URL url = new URL(apiUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      int responseCode = connection.getResponseCode();

      BufferedReader br;

      if (responseCode == 200) {
        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } else {
        br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
      }
      String inputLine;
      StringBuilder response = new StringBuilder();
      while ((inputLine = br.readLine()) != null) {
        response.append(inputLine);
      }
      br.close();
      return response.toString();
    } catch (Exception e) {
      return "failed to get response";
    }
  }

  public Map<String, Object> parseWeather(String jsonString) {
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;

    try {
      jsonObject = (JSONObject) jsonParser.parse(jsonString);
    } catch (ParseException e) {
      throw new RuntimeException();
    }
    Map<String, Object> resultMap = new HashMap<>();
    JSONObject mainData = (JSONObject) jsonObject.get("main");
    resultMap.put("temp", mainData.get("temp"));
    JSONArray weatherArr = (JSONArray) jsonObject.get("weather");
    JSONObject weatherData = (JSONObject) weatherArr.get(0);
    resultMap.put("main", weatherData.get("main"));
    resultMap.put("icon", weatherData.get("icon"));
    return resultMap;
  }
}
