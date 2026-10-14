# Описание проекта «Controller endpoints»

## Карточка

| Поле | Значение |
|---|---|
| Тема | Spring MVC: текстовые GET-эндпоинты |
| Технологии | Java 17, Spring Boot 3.5.16, Maven |
| Пакет | `com.example.controllerendpoints` |
| Точка входа | `ControllerEndpointsApplication` |
| Контроллер | публичный `PageController` |

## Условие и перевод

**Original:** Create a Spring Boot application with a controller that has two GET endpoints: `/index` returns `This is the main page`, and `/contact` returns `This is the contact page`.

**Перевод:** Создать Spring Boot-приложение с контроллером и двумя GET-эндпоинтами: `/index` возвращает `This is the main page`, а `/contact` — `This is the contact page`.

## Results

Приложение возвращает две неизменяемые текстовые строки по HTTP. Оно не работает с базой данных, параметрами, JSON, HTML-шаблонами или пользовательским интерфейсом.

## Criteria

- `PageController` — публичный класс с `@Controller` и `@ResponseBody`;
- публичный `index()` обслуживает `GET /index`;
- публичный `contact()` обслуживает `GET /contact`;
- строки совпадают с условием посимвольно;
- есть `contextLoads()` и MockMvc-проверки маршрутов.

## File tree

```text
src/
├── main/java/com/example/controllerendpoints/
│   ├── ControllerEndpointsApplication.java
│   └── PageController.java
└── test/java/com/example/controllerendpoints/
    ├── ControllerEndpointsApplicationTests.java
    └── PageControllerTest.java
```

## Responsibilities

| Часть | Ответственность |
|---|---|
| `ControllerEndpointsApplication` | запускает Spring Boot и embedded Tomcat |
| `PageController` | принимает HTTP GET-запросы и возвращает строки |
| `PageControllerTest` | проверяет HTTP-контракт MockMvc |
| `ControllerEndpointsApplicationTests` | проверяет загрузку Spring context |

## HTTP contract

| Запрос | Ответ |
|---|---|
| `GET /index` | `200 OK`, `This is the main page` |
| `GET /contact` | `200 OK`, `This is the contact page` |
| `GET /unknown` | `404 Not Found` |

## Request flow

```text
клиент → embedded Tomcat → Spring MVC → PageController.method()
       → class-level @ResponseBody → HTTP 200 + текст
```

## Annotations

`@SpringBootApplication` запускает автоконфигурацию и поиск компонентов. `@Controller` регистрирует публичный класс `PageController` как MVC-контроллер. `@ResponseBody` находится **на классе**, поэтому результаты обоих публичных методов становятся телом HTTP-ответа. `@GetMapping` связывает каждый метод с GET-путём.

Это не `@RestController`: он мог бы объединить эффекты `@Controller` и `@ResponseBody`, но в задании обе аннотации показаны отдельно. Thymeleaf не используется; это альтернативный механизм для серверных HTML-шаблонов.

## macOS

Установите JDK 17 и IntelliJ IDEA. Откройте папку с `pom.xml` как Maven-проект, дождитесь Maven Sync и проверьте Project SDK/Maven JDK = 17. При необходимости:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Затем откройте `http://localhost:8080/index` и `http://localhost:8080/contact`. Сервер останавливается через `Ctrl+C` или Stop в IntelliJ.

## Testing

```bash
./mvnw test
curl -i http://localhost:8080/index
curl -i http://localhost:8080/contact
curl -i http://localhost:8080/unknown
```

JUnit-проверка `contextLoads()` подтверждает создание Spring context. MockMvc проверяет 200 и точные тела ответов для обязательных путей, а также 404 для неизвестного пути. Команды следует выполнить на машине проверяющего; этот документ не заявляет, что они уже запускались.

## Limitations

Ответы статичны; нет базы данных, валидации, сервисного слоя, авторизации, параметров, JSON и HTML. Порт по умолчанию — 8080; при конфликте остановите приложение, которое уже использует этот порт.

## RU defense

«Spring Boot запускает встроенный Tomcat. `PageController` — публичный MVC-контроллер. На классе находятся `@Controller` и `@ResponseBody`; поэтому оба публичных метода возвращают текст прямо в HTTP body. `@GetMapping` выбирает `index()` для `/index` и `contact()` для `/contact`. MockMvc автоматически проверяет статус, тело и 404».

## EN defense

“`PageController` is a public class annotated with `@Controller` and class-level `@ResponseBody`. Its two public methods handle GET requests. `index()` returns the main-page text and `contact()` returns the contact-page text. Spring Boot starts embedded Tomcat, and MockMvc verifies the HTTP contract.”

## Navigation

- [README.md](docs/PROJECT-README.md) — запуск, ручная приёмка и расширенная защита;
- [METHODICAL_GUIDE.md](METHODICAL_GUIDE.md) — занятие на 90 минут;
- [SPRING_CONCEPTS.md](SPRING_CONCEPTS.md) — объяснения аннотаций, HTTP, Maven и MockMvc.
