package com.mase.cafe.system.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

public class RegistrationTestIT {

    WebDriver driver;
    private static final String APP_URL = "http://host.docker.internal:8080";

    @BeforeEach
    void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "password_manager_leak_detection", false
        ));

        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);
        driver.get(APP_URL);
    }

    private void loginAndNavigateToUsers() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement userField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        userField.sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");
        driver.findElement(By.id("submit")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-users"))).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("openAddUserBtn")));
    }

    @Test
    void testRegistrationSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String uniqueUser = "user" + System.currentTimeMillis();

        loginAndNavigateToUsers();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("openAddUserBtn"))).click();

        WebElement addUserModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));
        addUserModal.findElement(By.id("username")).sendKeys(uniqueUser);
        addUserModal.findElement(By.id("password")).sendKeys("Test@1234!");
        addUserModal.findElement(By.id("role")).sendKeys("MANAGER");

        addUserModal.findElement(By.id("saveUserBtn")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("userModal")));

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("userTable"), uniqueUser));
    }

    @Test
    void testRegistrationDuplicateAccount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        loginAndNavigateToUsers();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("openAddUserBtn"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("Test@1234!");
        driver.findElement(By.id("role")).sendKeys("MANAGER");
        driver.findElement(By.id("saveUserBtn")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#userModal .btn-close, #userModal [data-bs-dismiss='modal']")));

        try {
            closeButton.click();
        } catch (ElementClickInterceptedException e) {

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
        }

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("userModal")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-backdrop")));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}