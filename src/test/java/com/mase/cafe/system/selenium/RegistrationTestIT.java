package com.mase.cafe.system.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationTestIT {

    WebDriver driver;

    @BeforeEach
    void setUp() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false
        ));
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        driver.get("http://localhost:8080");
    }

    @Test
    void testRegistrationSuccess() {
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

        WebElement modal = driver.findElement(By.id("userModal"));
        wait.until(driver1 -> !modal.isDisplayed());

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("userTable"), "testuser"));

        WebElement table = driver.findElement(By.id("userTable"));
        assertTrue(table.getText().contains("testuser"), "User should be added successfully");

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
        assertNotNull(alert);
        assertTrue(alert.getText().contains("Username already exists"));
        alert.accept();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("saveUserBtn")));

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("userTable"), "manager"));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
