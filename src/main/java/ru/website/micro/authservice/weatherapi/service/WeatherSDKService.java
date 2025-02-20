package ru.website.micro.authservice.weatherapi.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.website.micro.authservice.weatherapi.entity.WeatherData;
import ru.website.micro.authservice.weatherapi.enums.SdkMode;
import ru.website.micro.authservice.weatherapi.exception.WeatherSdkException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
@ToString
public class WeatherSDKService {
    private static final Duration CACHE_VALIDITY = Duration.ofMinutes(10);
    private static final int MAX_CACHE_SIZE = 10;
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final String apiKey;
    private final Map<String, CacheEntry> cache;
    private final RestTemplate restTemplate;

    private ScheduledExecutorService scheduler;
    @Getter
    private static final Map<String, WeatherSDKService> instances = new ConcurrentHashMap<>();



    private WeatherSDKService(String apiKey, SdkMode sdkMode) {
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
        this.cache = Collections.synchronizedMap(new LinkedHashMap<String, CacheEntry>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        });
        if (sdkMode == SdkMode.POLLING) {
            startPolling();
        }
    }

    public static synchronized WeatherSDKService createInstance(String apiKey, SdkMode sdkMode) throws WeatherSdkException {
        if (instances.containsKey(apiKey)) {
            throw new WeatherSdkException("API key already exists");
        }
        WeatherSDKService sdk = new WeatherSDKService(apiKey, sdkMode);
        instances.put(apiKey, sdk);
        return sdk;
    }

    public static synchronized void deleteSDKInstance(String apiKey) {
        WeatherSDKService sdk = instances.remove(apiKey);
        if (sdk != null && sdk.scheduler != null) {
            sdk.scheduler.shutdownNow();
        }
    }

    public WeatherData getWeather(String city) throws WeatherSdkException {
        String cityLower = city.toLowerCase();
        CacheEntry cacheEntry = cache.get(cityLower);
        if (cacheEntry != null &&
                Duration.between(cacheEntry.timestamp, Instant.now()).compareTo(CACHE_VALIDITY) < 0) {
            return cacheEntry.weatherData;
        }
        WeatherData weatherData = fetchWeatherData(city);
        cache.put(cityLower, new CacheEntry(weatherData, Instant.now()));
        return weatherData;
    }


    private void startPolling() {

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (cache) {
                for (String city : cache.keySet()) {
                    try {
                        WeatherData data = fetchWeatherData(city);
                        cache.put(city, new CacheEntry(data, Instant.now()));
                    } catch (WeatherSdkException e) {
                        System.err.printf("Ошибка обновления погоды для города %s:%s%n", city, e.getMessage());
                    }
                }
            }
        }, 0, 5, TimeUnit.MINUTES);


    }

    private WeatherData fetchWeatherData(String city) throws WeatherSdkException {
        String url = API_URL + "?q=" + city + "&appid=" + apiKey + "&units=metric";
        try {
            WeatherData data = restTemplate.getForObject(url, WeatherData.class);
            if (data == null) {
                throw new WeatherSdkException("Ответ от API пуст");
            }
            return data;

        } catch (HttpClientErrorException e) {
            throw new WeatherSdkException("Ошибка при вызове API:%s %s".formatted(e.getStatusCode(),
                    e.getResponseBodyAsString(), e));
        } catch (Exception e) {
            throw new WeatherSdkException(
                    "Ошибка при получении данных о погоде:%s %s".formatted(e.getMessage(), e));
        }
    }

    @AllArgsConstructor
    private static class CacheEntry {
        private WeatherData weatherData;
        private Instant timestamp;
    }
}
