# 🐍 Snake Game — Full Stack System (Spring Boot + Swing + Web)

Проект представляет собой клиент-серверную игру «Змейка» с разделением ответственности: сервер управляет состоянием и логикой, а клиенты (Swing и Web) отвечают за визуализацию и взаимодействие с пользователем.

---

## 🚀 Функциональность

### 🖥 Сервер (Spring Boot)
* **REST API**: Обработка игровых сессий, команд управления и запросов координат через JSON.
* **Game Engine**: Расчёт логики движения, коллизий и генерации еды на стороне сервера.
* **Persistence**: Сохранение данных в **PostgreSQL** (основная БД) или **H2** (для разработки и тестов).
* **DevOps Ready**: Полная интеграция с **GitHub Actions** и **Docker (Testcontainers)**.

### 🎮 Клиенты
* **Java Swing**: Десктопное приложение. Отрисовка поля, обработка нажатий клавиш через `JFrame` и взаимодействие с сервером через `Java HTTP Client`.
* **Web Frontend**: Современный веб-интерфейс, взаимодействующий с REST API сервера в реальном времени.

---

## 🧩 Технический стек

**Backend:**
* **Язык**: Java 21.
* **Framework**: Spring Boot 3.5 (Spring Web, Spring Data JPA).
* **Базы данных**: PostgreSQL, H2.
* **Инструменты**: Lombok, Maven, Testcontainers.

**Frontend:**
* **Desktop**: Java Swing, Gson (JSON serialization).
* **Web**: JavaScript, HTML/CSS.

---

## 🧪 Тестирование (Multi-Level QA)

Проект охватывает все уровни пирамиды тестирования, что гарантирует стабильность приложения в различных средах:

### 🔴 Backend & Integration (Java)
* **Unit & Integration Tests**: JUnit 5 + Mockito для проверки бизнес-логики и маппинга данных.
* **Infrastructure Tests**: **Testcontainers** для запуска тестов в изолированной среде с реальной базой данных PostgreSQL.
* **E2E Selenium**: Автоматизированные тесты на **Selenium WebDriver** для проверки сквозных сценариев взаимодействия сервера и браузера.

### 🔵 Frontend & E2E (Playwright)
* **E2E Web Tests**: Использование **Playwright** для тестирования пользовательских путей (запуск, управление, окончание игры) в браузере.
* **Network Mocking**: Использование `page.route` для имитации критических состояний (например, смерть змейки или ошибки сервера).

### ⚙️ CI/CD (GitHub Actions)
Настроен автоматизированный пайплайн, который при каждом пуше выполняет следующие шаги:
1. Сборка проекта и прогон Java-тестов с использованием **Testcontainers**.
2. Развертывание временной инфраструктуры (Docker контейнер PostgreSQL + запуск Spring App).
3. Выполнение **Playwright** тестов в Headless режиме.
4. Сбор и сохранение артефактов (отчеты Surefire, Playwright HTML reports и логи приложения).

---

## 🛠 Пример реализации (Swing MainWindow)

Пример инициализации основного окна клиента с инъекцией сетевого взаимодействия:

```java
public class MainWindow extends JFrame {
    public MainWindow() {
        setTitle("Змейка");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(355, 375);
        setLocation(400, 400);
        // Инъекция сетевого клиента в игровой компонент
        add(new Game(new GameSnakeClient()));
        setVisible(true);
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
