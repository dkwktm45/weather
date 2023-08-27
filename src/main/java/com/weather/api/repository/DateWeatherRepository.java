package com.weather.api.repository;

import com.weather.api.domain.DateWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DateWeatherRepository extends JpaRepository<DateWeather,
    LocalDate> {
  List<DateWeather> findAllByDate(LocalDate localDate);
}
