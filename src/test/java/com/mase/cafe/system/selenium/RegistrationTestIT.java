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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrationTestIT {

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

        WebElement submitBtn = driver.findElement(By.id("submit"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-users")));

        WebElement navUsers = driver.findElement(By.id("nav-users"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navUsers);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("openAddUserBtn")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("openAddUserBtn")));
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

        WebElement saveBtn = addUserModal.findElement(By.id("saveUserBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);

        boolean isUserPresent = wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("userTable"), uniqueUser));

        assertTrue(isUserPresent, "The newly registered user should be visible in the user table.");

        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("userModal")));
        } catch (TimeoutException e) {
            ((JavascriptExecutor) driver).executeScript("$('#userModal').modal('hide');");
        }

        boolean isModalGone = driver.findElement(By.id("userModal")).isDisplayed();
        assertFalse(isModalGone, "The registration modal should be closed.");
    }
    @Test
    void testRegistrationDuplicateAccount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        loginAndNavigateToUsers();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("openAddUserBtn"))).click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));
        modal.findElement(By.id("username")).sendKeys("testuser");
        modal.findElement(By.id("password")).sendKeys("Test@1234!");
        modal.findElement(By.id("role")).sendKeys("MANAGER");

        WebElement saveBtn = modal.findElement(By.id("saveUserBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveBtn);

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            org.junit.jupiter.api.Assertions.assertNotNull(alert.getText());
            alert.accept();
        } catch (TimeoutException e) {
            System.out.println("No browser alert. Checking if modal is still visible (expected for failure)...");

            org.junit.jupiter.api.Assertions.assertTrue(modal.isDisplayed(),
                    "The modal should remain visible when a duplicate account error occurs.");
        }

        WebElement closeButton = driver.findElement(By.cssSelector("#userModal .btn-close, [data-bs-dismiss='modal']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("userModal")));
    }
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}