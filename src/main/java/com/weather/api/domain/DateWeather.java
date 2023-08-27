package com.weather.api.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter @Setter
public class DateWeather {
  @Id
  private LocalDate date;
  private String weather;
  private String icon;
  private double temperature;
}
