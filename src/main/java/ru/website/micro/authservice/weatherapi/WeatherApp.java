package ru.website.micro.authservice.weatherapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.website.micro.authservice.weatherapi.entity.WeatherData;
import ru.website.micro.authservice.weatherapi.enums.SdkMode;
import ru.website.micro.authservice.weatherapi.exception.WeatherSdkException;
import ru.website.micro.authservice.weatherapi.service.WeatherSDKService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WeatherApp {
    private static final Logger logger = LoggerFactory.getLogger(WeatherApp.class);

    public static void weatherApp() {
        Scanner scanner = new Scanner(System.in);
        ObjectMapper objectMapper = new ObjectMapper();

        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1) Создать SDK");
            System.out.println("2) Вывести все созданные SDK");
            System.out.println("3) Удалить SDK");
            System.out.println("4) Завершить");
            System.out.print("Выберите опцию: ");
            int mainChoose;
            try {
                mainChoose = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Пожалуйста, введите число от 1 до 4.");
                continue;
            }

            try {
                switch (mainChoose) {
                    case 1: {
                        System.out.print("Введите город: ");
                        String city = scanner.nextLine().trim();

                        System.out.print("Введите API key: ");
                        String apiKey = scanner.nextLine().trim();

                        System.out.println("Выберите режим работы SDK:");
                        System.out.println("1) Polling (автоматическое обновление данных)");
                        System.out.println("2) On-demand (обновление только по запросу)");
                        System.out.print("Введите 1 или 2: ");
                        int modeChoice;
                        SdkMode sdkMode;
                        try {
                            modeChoice = Integer.parseInt(scanner.nextLine());
                            if (modeChoice == 1) {
                                sdkMode = SdkMode.POLLING;
                            } else if (modeChoice == 2) {
                                sdkMode = SdkMode.ON_DEMAND;
                            } else {
                                System.out.println("Некорректный выбор режима. Используем On-demand по умолчанию.");
                                sdkMode = SdkMode.ON_DEMAND;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Некорректный ввод. Используем On-demand по умолчанию.");
                            sdkMode = SdkMode.ON_DEMAND;
                        }
                        WeatherSDKService sdk = WeatherSDKService.createInstance(apiKey, sdkMode);
                        WeatherData weather = sdk.getWeather(city);
                        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(weather);
                        System.out.println("Данные о погоде: ");
                        System.out.println(json);
                        break;
                    }
                    case 2: {
                        System.out.println("Список созданных SDK:");
                        System.out.println(WeatherSDKService.getInstances().toString());
                        break;
                    }
                    case 3: {
                        System.out.print("Введите API key для удаления: ");
                        String apiKey = scanner.nextLine().trim();
                        WeatherSDKService.deleteSDKInstance(apiKey);
                        System.out.println("SDK с ключом " + apiKey + " удален.");
                        break;
                    }
                    case 4: {
                        System.out.println("Завершение работы приложения.");
                        return;
                    }
                    default:
                        System.out.println("Пожалуйста, выберите корректную опцию (от 1 до 4).");
                }
            } catch (WeatherSdkException e) {
                logger.error("Ошибка в работе SDK: {}", e.getMessage());
            } catch (JsonProcessingException e) {
                logger.error("Ошибка обработки JSON: {}", e.getMessage());
            }
        }
    }
}