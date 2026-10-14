# Controller endpoints: Spring Boot

Небольшой учебный проект на **Spring Boot 3.5.16**, **Java 17** и Maven. Он показывает, как сервер принимает HTTP GET-запрос и возвращает простой текст. В проекте нет HTML-шаблонов, базы данных, авторизации и фронтенда: это сознательно минимальный пример контроллера.

## 1. Задание: оригинал и перевод

**Original.** Create a Spring Boot application with a controller that has two GET endpoints: `/index` returns `This is the main page`, and `/contact` returns `This is the contact page`.

**Перевод.** Создать Spring Boot-приложение с контроллером, у которого есть два GET-эндпоинта: `/index` возвращает строку `This is the main page`, а `/contact` — строку `This is the contact page`.

## 2. Обязательные критерии

| Требование | Где проверяется |
|---|---|
| Java 17 и Spring Boot 3.5.16 | `pom.xml` |
| Пакет `com.example.controllerendpoints` | исходные классы |
| Есть `PageController` с `@Controller` | `src/main/java/.../PageController.java` |
| Обычный текст отправляется через class-level `@ResponseBody` | класс `PageController` |
| `GET /index` отдаёт точную строку | ручная проверка и MockMvc |
| `GET /contact` отдаёт точную строку | ручная проверка и MockMvc |
| Контекст Spring Boot загружается | `contextLoads()` |
| Маршруты проверяются без ручного браузера | `PageControllerTest` с MockMvc |

Регистр букв, пробелы и точка в ответах важны: тест ожидает точные английские строки.

## 3. Среда на macOS

Нужны JDK 17 и IntelliJ IDEA в актуальном unified/free mode. Maven отдельно устанавливать не нужно: в проекте уже есть Maven Wrapper — файл `mvnw`.

В Terminal проверьте JDK:

```bash
java -version
```

Должна быть версия 17. Если IDE использует другую JDK, откройте **IntelliJ IDEA → Settings → Build, Execution, Deployment → Build Tools → Maven** и укажите JDK 17; также проверьте **Project Structure → Project SDK**. Откройте в IntelliJ IDEA именно папку этого проекта — ту, где находится `pom.xml`; дождитесь окончания Maven Sync.

Разрешите выполнение wrapper, если macOS сообщит об отсутствии прав:

```bash
chmod +x mvnw
```

Команды ниже выполняются в папке проекта. Не применяйте Windows-команды PowerShell и не запускайте `mvnw.cmd` на macOS.

## 4. Запуск

В IntelliJ IDEA запустите класс `ControllerEndpointsApplication` зелёной кнопкой около `main`, либо в Terminal выполните:

```bash
./mvnw spring-boot:run
```

После появления сообщения о запуске сервера откройте:

```text
http://localhost:8080/index
http://localhost:8080/contact
```

Или проверьте из другого окна Terminal:

```bash
curl -i http://localhost:8080/index
curl -i http://localhost:8080/contact
```

Остановите сервер `Ctrl+C`. Если порт 8080 занят, завершите другое запущенное Spring-приложение в окне Run IntelliJ IDEA или найдите процесс командой `lsof -i :8080` и остановите именно его. Не меняйте порт, пока это не требуется заданием.

## 5. Структура

```text
src/
├── main/
│   ├── java/com/example/controllerendpoints/
│   │   ├── ControllerEndpointsApplication.java  # точка входа
│   │   └── PageController.java                  # HTTP-маршруты
│   └── resources/application.properties         # настройки (пустые допустимы)
└── test/java/com/example/controllerendpoints/
    ├── ControllerEndpointsApplicationTests.java # contextLoads
    └── PageControllerTest.java                  # MockMvc-проверки
```

`ControllerEndpointsApplication` находится в корне пакета. Поэтому `@SpringBootApplication` сканирует вложенный пакет и находит `PageController` автоматически.

## 6. HTTP contract

| Метод | Путь | Успешный статус | Тело ответа | Тип |
|---|---|---:|---|---|
| GET | `/index` | 200 OK | `This is the main page` | plain text |
| GET | `/contact` | 200 OK | `This is the contact page` | plain text |
| GET | `/unknown` | 404 Not Found | не является частью задания | — |

Пример запроса и ответа:

```http
GET /index HTTP/1.1
Host: localhost:8080

HTTP/1.1 200 OK

This is the main page
```

## 7. Путь запроса

```text
Браузер / curl
      │ GET /index
      ▼
встроенный Tomcat (порт 8080)
      ▼
Spring DispatcherServlet
      ▼
PageController.index()
      ▼
@ResponseBody преобразует String в тело HTTP-ответа
      ▼
200 OK + This is the main page
```

`@GetMapping("/index")` связывает путь с методом. Метод не создаёт страницу и не ищет файл: он возвращает Java-строку. `@ResponseBody` стоит на классе `PageController`, поэтому применяется к обоим его методам и записывает результат в ответ.

## 8. Понятия

- **HTTP** — протокол общения клиента и веб-сервера.
- **Endpoint (эндпоинт)** — доступный по HTTP адрес и метод, например `GET /contact`.
- **Controller** — класс, который принимает веб-запросы и решает, что вернуть.
- **Маршрутизация** — поиск метода по HTTP-методу и пути.
- **Status code** — результат обработки запроса: 200 означает успех, 404 — путь не найден.
- **Embedded Tomcat** — веб-сервер, стартующий внутри Java-приложения.
- **Dependency injection** — Spring сам передаёт готовый объект `MockMvc` полю теста.

Подробный мини-справочник с фрагментами кода — в [SPRING_CONCEPTS.md](../SPRING_CONCEPTS.md).

## 9. Автоматические тесты

Запустите все тесты:

```bash
./mvnw test
```

Или в IntelliJ IDEA откройте окно Maven и выберите **Lifecycle → test**, либо нажмите зелёную кнопку у тестового класса. Тесты проверяют:

1. создание контекста приложения;
2. 200 и точное тело для `/index`;
3. 200 и точное тело для `/contact`;
4. 404 для неизвестного `/unknown`.

Эта инструкция не утверждает, что команда уже была выполнена на вашем компьютере. Успешным результатом будет Maven `BUILD SUCCESS` и зелёные тесты в IntelliJ IDEA.

## 10. Ручная приёмка

Запустите приложение и выполните проверку в браузере или `curl`.

| Действие | Ожидаемый результат |
|---|---|
| Открыть `/index` | виден ровно `This is the main page` |
| Открыть `/contact` | виден ровно `This is the contact page` |
| Открыть `/unknown` | HTTP 404, а не один из двух текстов |
| Обновить `/index` | тот же стабильный ответ, без запуска отдельного HTML-файла |

Для просмотра статуса используйте `curl -i`, а не только браузер: первая строка ответа должна содержать `200` для обязательных маршрутов.

## 11. Частые ошибки

| Симптом | Причина | Что сделать |
|---|---|---|
| `404` на `/index` | путь набран иначе или нет `@GetMapping` | используйте точный путь `/index` |
| Вместо текста ошибка шаблона | есть `@Controller`, но нет `@ResponseBody` | добавьте `@ResponseBody` к классу контроллера |
| `Address already in use` | порт 8080 занят | остановите ранее запущенное приложение |
| `Permission denied: ./mvnw` | wrapper не исполняемый | выполните `chmod +x mvnw` |
| Maven не скачивает зависимости | сеть, прокси или Maven Sync | проверьте интернет и повторите синхронизацию |
| Тест не проходит при похожей фразе | строка отличается символом/пробелом | скопируйте контракт из раздела 6 |
| Контроллер не найден | класс вне сканируемого пакета | оставьте его в `com.example.controllerendpoints` или вложенном пакете |

## 12. Вопросы для самопроверки

1. Почему маршрут задают как пару HTTP-метод + путь, а не только путь?
2. Что именно делает `@ResponseBody` в этом проекте?
3. Почему `@Controller` без `@ResponseBody` мог бы интерпретировать строку как имя представления?
4. Что проверяет `contextLoads()` и чего он не проверяет?
5. Чем MockMvc отличается от ручной проверки в браузере?
6. Почему `GET /unknown` должен вернуть 404?
7. Как Spring находит `PageController` без `new PageController()`?

## 13. Полные ответы

1. Один путь может поддерживать разные операции; например, `GET /items` читает данные, а `POST /items` создаёт. Поэтому метод — часть контракта.
2. Она говорит Spring записать возвращённую строку в тело HTTP-ответа.
3. Без неё строка у MVC-контроллера обычно рассматривается как логическое имя шаблона. Spring попытался бы найти представление, а не отдать фразу.
4. Он доказывает, что Spring Boot смог собрать контекст и обнаружить нужные конфигурации. Он сам по себе не доказывает точные ответы маршрутов.
5. MockMvc отправляет имитационный HTTP-запрос в Spring-контекст без реального браузера и удобно проверяет статус и тело автоматически. Браузер подтверждает сценарий с настоящим запущенным сервером.
6. В приложении нет обработчика для такого пути; сервер не должен выдавать успешный ответ за несуществующий ресурс.
7. `@SpringBootApplication` запускает component scan, Spring создаёт зарегистрированный контроллер как bean и выбирает его при запросе.

Сначала ответьте устно сами, затем сверяйтесь с этим разделом.

## 14. English defense

> This is a small Spring Boot web application written in Java 17. The application class is annotated with `@SpringBootApplication`, so Spring Boot configures the web application and starts an embedded Tomcat server. `PageController` is a public class annotated with `@Controller` and `@ResponseBody`. The class-level `@ResponseBody` applies to both public methods. `index()` handles `GET /index` and returns the plain-text message `This is the main page`. `contact()` handles `GET /contact` and returns `This is the contact page`. `@GetMapping` connects each HTTP path to its Java method. The project has no database or HTML templates. I use JUnit to check that the Spring context loads, and MockMvc to verify status codes, response bodies, and the 404 result for an unknown path.

Useful short answers:

- **Why `@ResponseBody`?** — It sends the returned string as the response body instead of a view name.
- **What does MockMvc test?** — It tests HTTP requests, status codes, and response content in the Spring test context.
- **Why is `/unknown` 404?** — There is no controller mapping for that path.

## 15. Самостоятельные улучшения

Не меняйте обязательный контракт, пока преподаватель его проверяет. После этого попробуйте отдельно:

1. добавить `GET /about` с собственным точным текстом и тестом;
2. вынести общий префикс в `@RequestMapping("/pages")` и обновить контракт/тесты;
3. вернуть JSON из нового endpoint через `record PageInfo(String title)`;
4. добавить `@RequestParam` в новый маршрут, например приветствие по имени;
5. написать тест на `Content-Type` для текстового ответа;
6. сравнить текущий текстовый контроллер с Thymeleaf. Thymeleaf здесь **не используется**: это альтернативный подход для рендеринга HTML-шаблонов, а не требование данного задания.

Для подробного занятия используйте [METHODICAL_GUIDE.md](../METHODICAL_GUIDE.md), а для защиты — [DESCRIPTION.md](../DESCRIPTION.md).
