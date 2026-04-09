package ru.neikatya.gamesnake;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SnakeSeleniumTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
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

        System.out.println("Начинаем имитацию управления...");

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