package ru.website.micro.authservice.weatherapi.exception;

public class WeatherSdkException extends Exception {

    public WeatherSdkException(String message) {
        super(message);
    }
    public WeatherSdkException(String message, Throwable cause) {
        super(message, cause);
    }

}
