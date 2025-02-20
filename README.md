# WeatherAPI SDK

Данный SDK позволяет получать данные о погоде с использованием [OpenWeather API](https://openweathermap.org/api).

## Особенности

- Принимает API ключ для доступа к OpenWeather API.
- Позволяет получать данные о погоде по названию города.
- Использует кэширование: данные считаются актуальными в течение 10 минут.
- Ограничение кэша до 10 городов.
- Поддержка двух режимов работы:
  - **Polling:** автоматическое обновление данных каждые 5 минут.
  - **On-demand:** обновление данных только по запросу.
- Обработка ошибок с подробными сообщениями.

## Пример использования

java

// Создание экземпляра SDK в режиме polling
WeatherSDKService sdk = WeatherSDKService.createInstance("YOUR_API_KEY", SdkMode.POLLING);

// Получение данных о погоде для города "Moscow"
WeatherData weather = sdk.getWeather("Moscow");

// Вывод полученных данных в формате JSON
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(weather);
System.out.println(json);

// Удаление экземпляра SDK
WeatherSDKService.deleteSDKInstance("YOUR_API_KEY");
