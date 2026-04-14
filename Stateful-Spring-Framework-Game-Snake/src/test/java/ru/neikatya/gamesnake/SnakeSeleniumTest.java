package ru.neikatya.gamesnake;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SnakeSeleniumTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("snake_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Полный сценарий: загрузка игры и проверка управления")
    void testFullGameCycle() throws InterruptedException {
        driver.get("http://localhost:8080");

        WebElement header = driver.findElement(By.tagName("h1"));
        assertTrue(header.getText().contains("Змейка"), "Заголовок должен содержать слово 'Змейка'");

        WebElement status = driver.findElement(By.id("status"));
        Thread.sleep(1000);
        assertTrue(status.getText().contains("Игра началась"), "Статус должен подтвердить начало игры");

        Actions action = new Actions(driver);

        action.sendKeys(Keys.ARROW_RIGHT).perform();
        Thread.sleep(800);
        action.sendKeys(Keys.ARROW_DOWN).perform();
        Thread.sleep(800);
        action.sendKeys(Keys.ARROW_LEFT).perform();
        Thread.sleep(800);
        action.sendKeys(Keys.ARROW_UP).perform();
        Thread.sleep(800);

        assertTrue(status.isDisplayed(), "Элемент статуса должен оставаться видимым");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}