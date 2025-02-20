package ru.website.micro.authservice.weatherapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.website.micro.authservice.weatherapi.entity.WeatherData;
import ru.website.micro.authservice.weatherapi.enums.SdkMode;
import ru.website.micro.authservice.weatherapi.exception.WeatherSdkException;
import ru.website.micro.authservice.weatherapi.service.WeatherSDKService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        WeatherApp.weatherApp();
    }
}