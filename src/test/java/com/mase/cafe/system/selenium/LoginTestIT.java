package com.mase.cafe.system.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTestIT {

    WebDriver driver;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver = new ChromeDriver();
        driver.get("http://localhost:8080");
    }

    @Test
    void testLoginSuccess() {

        driver.findElement(By.id("username")).sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");
        driver.findElement(By.id("submit")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement logout = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/nav/div/div/button"))
        );

        assertNotNull(logout);

    }

    @Test
    void testIncorrectCredentials() {

        driver.findElement(By.id("username")).sendKeys("test");
        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.id("submit")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        String alertText = alert.getText();
        assertEquals("Invalid credentials", alertText);

        alert.accept(); // close popup

    }

    @Test
    void testEmptyUsername() {

        driver.findElement(By.id("password")).sendKeys("test");
        driver.findElement(By.id("submit")).click();


        WebElement usernameField = driver.findElement(By.id("username"));
        String validationMessage = usernameField.getAttribute("validationMessage");
        assertTrue(validationMessage.contains("Please fill"));

        driver.quit();
    }

	@Test
    void testEmptyPassword() {

        driver.findElement(By.id("username")).sendKeys("test");
        driver.findElement(By.id("submit")).click();


        WebElement usernameField = driver.findElement(By.id("password"));
        String validationMessage = usernameField.getAttribute("validationMessage");
        assertTrue(validationMessage.contains("Please fill"));

    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}


