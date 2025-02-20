package ru.website.micro.authservice.weatherapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    @JsonProperty("weather")
    private List<WeatherInfo> weather;
    private TemperatureInfo temperature;
    private int visibility;
    private WindInfo wind;
    private long datetime;
    private SysInfo sys;
    private int timezone;
    private String name;

    @JsonProperty("main")
    public TemperatureInfo getTemperature() {
        return temperature;
    }

    @JsonProperty("wind")
    public WindInfo getWind() {
        return wind;
    }

    @JsonProperty("dt")
    public long getDatetime() {
        return datetime;
    }

    @JsonProperty("sys")
    public SysInfo getSys() {
        return sys;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherInfo {
        private String main;
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TemperatureInfo {
        @JsonProperty("temp")
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WindInfo {
        private double speed;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SysInfo {
        private long sunrise;
        private long sunset;

    }
}
