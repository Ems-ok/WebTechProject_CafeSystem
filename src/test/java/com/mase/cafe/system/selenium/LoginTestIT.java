package com.mase.cafe.system.selenium;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class LoginTestIT {
    WebDriver driver;

    private static final String APP_URL = "http://cafe-app:8081";

    @BeforeEach
    void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false
        ));
        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);
        driver.get(APP_URL);
    }

    @Test
    void testLoginSuccess() {
        driver.findElement(By.id("username")).sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");
        driver.findElement(By.id("submit")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement logout = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/nav/div/div/button")));
        assertNotNull(logout);
    }

    @Test
    void testIncorrectCredentials() {
        driver.findElement(By.id("username")).sendKeys("test");
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.id("submit")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Invalid credentials. Please try again.", alert.getText());
        alert.accept();
    }

    @Test
    void testEmptyUsername() {
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.id("submit")).click();
        WebElement usernameField = driver.findElement(By.id("username"));
        assertTrue(usernameField.getAttribute("validationMessage").contains("Please fill"));
    }

    @Test
    void testEmptyPassword() {
        driver.findElement(By.id("username")).sendKeys("test");
        driver.findElement(By.id("submit")).click();
        WebElement passwordField = driver.findElement(By.id("password"));
        assertTrue(passwordField.getAttribute("validationMessage").contains("Please fill"));
    }

    @AfterEach
    void tearDown() { if (driver != null) driver.quit(); }
}