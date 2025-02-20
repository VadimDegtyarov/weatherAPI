package ru.website.micro.authservice.weatherapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import ru.website.micro.authservice.weatherapi.entity.WeatherData;
import ru.website.micro.authservice.weatherapi.enums.SdkMode;
import ru.website.micro.authservice.weatherapi.exception.WeatherSdkException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class WeatherSDKServiceTest {

    private WeatherSDKService sdk;
    private RestTemplate restTemplateMock;

    @BeforeEach
    public void setup() throws WeatherSdkException, NoSuchFieldException, IllegalAccessException {
        sdk = WeatherSDKService.createInstance("ee9eab30ae5641e3a2da39d62a3f8b36", SdkMode.ON_DEMAND);
        restTemplateMock = Mockito.mock(RestTemplate.class);
        Field restTemplateField = WeatherSDKService.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(sdk, restTemplateMock);
    }

    @Test
    public void testGetWeatherSuccess() throws WeatherSdkException {
        WeatherData testData = new WeatherData();
        when(restTemplateMock.getForObject(anyString(), Mockito.eq(WeatherData.class))).thenReturn(testData);

        WeatherData result = sdk.getWeather("Moscow");
        assertNotNull(result, "Полученные данные не должны быть null");

        WeatherData cachedResult = sdk.getWeather("Moscow");
        assertEquals(result, cachedResult, "Данные должны возвращаться из кэша");
    }
}
