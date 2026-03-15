package com.mase.cafe.system.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "password_manager_leak_detection", false
        ));

        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);
        driver.get("http://host.docker.internal:8080");
    }

    @Test
    void testRegistrationSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String uniqueUser = "user_" + System.currentTimeMillis();

        driver.findElement(By.id("username")).sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");
        driver.findElement(By.id("submit")).click();

        WebElement usersButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-users")));
        usersButton.click();

        WebElement addUserButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("openAddUserBtn")));
        addUserButton.click();

        WebElement addUserModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));

        addUserModal.findElement(By.id("username")).sendKeys(uniqueUser);
        addUserModal.findElement(By.id("password")).sendKeys("Test@1234!");
        addUserModal.findElement(By.id("role")).sendKeys("MANAGER");

        addUserModal.findElement(By.id("saveUserBtn")).click();

        wait.until(ExpectedConditions.invisibilityOf(addUserModal));

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("userTable"), uniqueUser));

        WebElement table = driver.findElement(By.id("userTable"));
        assertTrue(table.getText().contains(uniqueUser), "Unique user should be in the table");
    }

    @Test
    void testRegistrationDuplicateAccount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.findElement(By.id("username")).sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");
        driver.findElement(By.id("submit")).click();

        WebElement usersButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-users")));
        usersButton.click();

        WebElement addUserButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("openAddUserBtn")));
        addUserButton.click();

        WebElement addUserModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));

        addUserModal.findElement(By.id("username")).sendKeys("testuser");
        addUserModal.findElement(By.id("password")).sendKeys("Test@1234!");
        addUserModal.findElement(By.id("role")).sendKeys("MANAGER");

        addUserModal.findElement(By.id("saveUserBtn")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        assertTrue(alertText.contains("Username already exists"));
        alert.accept();

        WebElement closeButton = driver.findElement(By.cssSelector("#userModal .btn-close, #userModal [data-bs-dismiss='modal']"));
        closeButton.click();

        wait.until(ExpectedConditions.invisibilityOf(addUserModal));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}