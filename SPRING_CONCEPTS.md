# Spring concepts: справочник к текущему проекту

Этот справочник разбирает именно приложение `controller-endpoints`: Java 17, Spring Boot 3.5.16, публичный `PageController`, два GET-маршрута и тесты JUnit/MockMvc. Кодовые фрагменты ниже совпадают по форме с текущим проектом, если явно не помечены как дополнительный учебный пример.

## HTTP: GET, path, status и body

Реальный контракт проекта:

```http
GET /index HTTP/1.1
Host: localhost:8080

HTTP/1.1 200 OK

This is the main page
```

| Часть | Назначение | Типичная ошибка | Модельное объяснение |
|---|---|---|---|
| `GET` | HTTP-метод чтения ресурса | считать путь достаточным без метода | маршрут — это пара «метод + путь»; тот же путь мог бы иметь отдельный POST |
| `/index` | path, адрес обработчика | открыть `/Index`, `/indexes` или забыть `/` | путь регистрозависим и должен совпадать с `@GetMapping` |
| `200 OK` | успешный status | смотреть только тело в браузере | статус сообщает клиенту результат обработки; `curl -i` показывает его явно |
| тело ответа | данные после заголовков | ожидать HTML вместо фразы | здесь body — plain text, который вернул Java-метод |

`GET /contact` возвращает `200 OK` и `This is the contact page`. `GET /unknown` не имеет mapping и должен дать `404 Not Found`. Это корректный результат поиска несуществующего маршрута, а не обязательно сбой приложения.

## Embedded Tomcat

Точка входа текущего проекта:

```java
@SpringBootApplication
public class ControllerEndpointsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControllerEndpointsApplication.class, args);
    }
}
```

**Назначение.** Зависимость `spring-boot-starter-web` приносит embedded Tomcat. Вызов `SpringApplication.run(...)` создаёт Spring context, настраивает веб-слой и запускает сервер внутри Java-процесса; по умолчанию он слушает 8080.

**Типичная ошибка.** Сообщение, что 8080 already in use. Значит, другой процесс уже занял порт. Остановите именно запущенное ранее приложение в IntelliJ IDEA или определите процесс на macOS командой `lsof -i :8080`.

**Модельное объяснение.** Отдельный Tomcat вручную устанавливать и разворачивать WAR не нужно: приложение запускает сервер вместе с собой.

## `@SpringBootApplication`

Тот же реальный фрагмент содержит `@SpringBootApplication`.

**Назначение.** Это составная аннотация: она включает конфигурацию, автоконфигурацию Spring Boot и component scan. Так Spring находит `PageController` в пакете `com.example.controllerendpoints`.

**Типичная ошибка.** Поместить контроллер в несвязанный верхнеуровневый пакет и получить 404: стандартное сканирование начинается от пакета главного класса и идёт во вложенные пакеты.

**Модельное объяснение.** Главный класс — корень приложения; все web-компоненты учебного проекта лежат в том же пакете или ниже, поэтому ручная регистрация `new PageController()` не нужна.

## `@Controller` и public class

Реальная шапка текущего контроллера:

```java
@Controller
@ResponseBody
public class PageController {
```

**Назначение.** `@Controller` регистрирует класс как MVC-контроллер. `public class PageController` объявляет публичный Java-класс, который может использоваться внешними пакетами при необходимости; Spring обнаруживает его по аннотации и component scan.

**Типичная ошибка.** Создать класс без `@Controller`: Java скомпилируется, но Spring не увидит обработчик, поэтому путь даст 404. Другая ошибка — менять фактическую форму класса на package-private, когда по заданию он должен быть public.

**Модельное объяснение.** Аннотация делает класс Spring bean с веб-назначением; ключевое слово `public` — модификатор доступа Java. Это разные уровни: Spring-регистрация и Java-доступность.

`@RestController` здесь не используется. Он объединяет смысл `@Controller` и `@ResponseBody`, но задача специально показывает эти две аннотации отдельно.

## `@ResponseBody` на классе

В текущем проекте `@ResponseBody` стоит между `@Controller` и объявлением публичного класса:

```java
@Controller
@ResponseBody
public class PageController {
```

**Назначение.** Class-level `@ResponseBody` действует на результаты обоих методов `PageController`: возвращённый `String` становится HTTP response body.

**Типичная ошибка.** Убрать аннотацию с класса и ожидать, что строка всё равно будет текстом ответа. У обычного `@Controller` строка без `@ResponseBody` обычно интерпретируется как имя view; Spring может попытаться найти шаблон.

**Модельное объяснение.** В проекте нет method-level `@ResponseBody`. Одна аннотация на классе экономит повторение и задаёт одинаковое правило для `index()` и `contact()`.

## `@GetMapping` и связь с `@RequestMapping`

Реальные методы контроллера:

```java
@GetMapping("/index")
public String index() {
    return "This is the main page";
}

@GetMapping("/contact")
public String contact() {
    return "This is the contact page";
}
```

**Назначение.** `@GetMapping("/index")` связывает GET-запрос к `/index` с публичным методом `index()`. Аналогично работает `contact()`.

**Связь с `@RequestMapping`.** `@GetMapping` — специализированная краткая форма общего `@RequestMapping` для GET. Учебный эквивалент первого mapping выглядел бы так (это пояснение, не код проекта):

```java
@RequestMapping(value = "/index", method = RequestMethod.GET)
public String index() { /* ... */ }
```

**Типичная ошибка.** Написать `/contacts` вместо `/contact`, или ожидать, что POST будет обработан `@GetMapping`. Первый случай даёт 404, второй — маршрут не подходит по HTTP-методу.

**Модельное объяснение.** Spring MVC получает запрос, сопоставляет его метод и путь с mapping, вызывает нужный Java-метод, затем class-level `@ResponseBody` превращает возвращённую строку в body.

## Java: class, method, parameter, constructor

Текущий класс — `public class PageController`; его методы имеют форму `public String index()` и `public String contact()`.

| Элемент | Реальный пример | Назначение | Типичная ошибка | Модельное объяснение |
|---|---|---|---|---|
| class | `public class PageController` | описывает новый тип и объединяет методы | написать класс вне нужного пакета | класс — контейнер поведения контроллера |
| method | `public String index()` | выполняет действие и возвращает `String` | указать другой тип, но вернуть строку | `String` — тип результата; `return` обязан ему соответствовать |
| parameter | в `index()` параметров нет | передаёт данные методу | думать, что пустые `()` — ошибка | пустые скобки означают: этому методу входные значения не нужны |
| constructor | в текущем классе не объявлен | создаёт объект при необходимости | путать конструктор с обычным методом | Java создаёт неявный constructor без параметров, если явный не объявлен |

Дополнительный пример конструктора (его **нет** в текущем контроллере):

```java
public PageController() {
}
```

У конструктора нет типа результата, даже `void`, и его имя совпадает с именем класса. В текущем простом контроллере явный конструктор не нужен: у него нет зависимостей.

## Maven, `pom.xml` и Maven Wrapper

Фрагменты текущего `pom.xml`:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.16</version>
</parent>
<properties>
    <java.version>17</java.version>
</properties>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**Назначение.** `pom.xml` описывает зависимости и настройки сборки. Parent задаёт Spring Boot 3.5.16, property фиксирует Java 17, web starter даёт MVC и Tomcat, test starter предоставляет тестовую инфраструктуру.

**Типичная ошибка.** Запускать `mvnw.cmd` на macOS или использовать несовместимую JDK. На macOS используется wrapper `./mvnw`; при отсутствии прав сначала выполните `chmod +x mvnw`.

**Модельное объяснение.** Maven Wrapper запускает Maven из состава проекта, поэтому глобально установленный Maven не обязателен. Команды:

```bash
./mvnw test
./mvnw spring-boot:run
```

Первая компилирует код и запускает тесты, вторая запускает приложение. Если загрузка зависимостей не проходит, сначала проверьте сеть и Maven Sync в IntelliJ IDEA, а не копируйте библиотеки в `src` вручную.

## JUnit, `@SpringBootTest` и `contextLoads`

Реальный тест загрузки:

```java
@SpringBootTest
class ControllerEndpointsApplicationTests {

    @Test
    void contextLoads() {
    }
}
```

**Назначение.** JUnit запускает методы с `@Test`. `@SpringBootTest` создаёт реальный Spring application context; пустой `contextLoads()` успешен, если context смог собраться.

**Типичная ошибка.** Считать, что `contextLoads()` проверяет ответы `/index` и `/contact`. Он выявляет проблемы конфигурации и создания beans, но не сравнивает HTTP status/body маршрутов.

**Модельное объяснение.** Это «дымовый» тест запуска Spring: он нужен в дополнение, а не вместо контрактных веб-тестов.

## MockMvc

Реальная конфигурация и проверка:

```java
@SpringBootTest
@AutoConfigureMockMvc
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void indexReturnsTheMainPage() throws Exception {
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(content().string("This is the main page"));
    }
}
```

**Назначение.** `@AutoConfigureMockMvc` создаёт `MockMvc`, а `@Autowired` передаёт его полю теста. `perform(get(...))` имитирует запрос через Spring MVC; `status().isOk()` проверяет 200, `content().string(...)` — точное тело.

**Типичная ошибка.** Проверять только статус или написать почти правильную строку. Статус 200 не гарантирует корректный текст; тест обязан проверять оба условия. Для `get("/unknown")` в проекте ожидается `status().isNotFound()`.

**Модельное объяснение.** MockMvc не требует вручную открывать браузер, но проверяет HTTP-путь внутри тестового Spring context. Ручная проверка после `spring-boot:run` всё равно полезна, чтобы увидеть работающий сервер.

## Краткая цепочка обработки

```text
GET /index
  → embedded Tomcat принимает соединение
  → Spring MVC находит @GetMapping("/index")
  → public String index() возвращает фразу
  → class-level @ResponseBody пишет фразу в HTTP body
  → 200 OK отправляется клиенту
```

Thymeleaf здесь не используется. Это альтернативный инструмент для создания серверных HTML-страниц из шаблонов; он не нужен для двух plain-text ответов.
