package com.weather.api.domain;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Diary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String weather;
  private String icon;
  private double temperature;
  private String text;
  private LocalDate date;

  public void setDateWeather(DateWeather dateWeather) {
    this.date = dateWeather.getDate();
    this.weather = dateWeather.getWeather();
    this.icon = dateWeather.getIcon();
    this.temperature = dateWeather.getTemperature();
  }
}
