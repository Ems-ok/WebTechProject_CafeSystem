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

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationTestIT {

    WebDriver driver;

    @BeforeEach
    void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "password_manager_leak_detection", false
        ));

        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);
        driver.get("http://host.docker.internal:8080");
    }

    private void login() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");
        driver.findElement(By.id("submit")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-users"))).click();
    }

    @Test
    void testRegistrationSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String uniqueUser = "success_" + System.currentTimeMillis();

        login();

        WebElement addUserButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("openAddUserBtn")));
        wait.until(ExpectedConditions.elementToBeClickable(addUserButton));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addUserButton);

        WebElement addUserModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));

        addUserModal.findElement(By.id("username")).sendKeys(uniqueUser);
        addUserModal.findElement(By.id("password")).sendKeys("Test@1234!");
        addUserModal.findElement(By.id("role")).sendKeys("MANAGER");

        addUserModal.findElement(By.id("saveUserBtn")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("userModal")));

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("userTable"), uniqueUser));
        assertTrue(driver.findElement(By.id("userTable")).getText().contains(uniqueUser));
    }

    @Test
    void testRegistrationDuplicateAccount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String duplicateName = "dup_" + System.currentTimeMillis();

        login();

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("openAddUserBtn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));
        modal.findElement(By.id("username")).sendKeys(duplicateName);
        modal.findElement(By.id("password")).sendKeys("Test@1234!");
        modal.findElement(By.id("saveUserBtn")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("userModal")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addButton);
        modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));
        modal.findElement(By.id("username")).sendKeys(duplicateName);
        modal.findElement(By.id("password")).sendKeys("Test@1234!");
        modal.findElement(By.id("saveUserBtn")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertTrue(alert.getText().contains("Username already exists"));
        alert.accept();

        WebElement closeButton = driver.findElement(By.cssSelector("#userModal .btn-close, #userModal [data-bs-dismiss='modal']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("userModal")));

        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-backdrop")));
        } catch (Exception e) {

        }
    }
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}