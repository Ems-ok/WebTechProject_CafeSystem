package com.mase.cafe.system.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuTestIT {

    WebDriver driver;
    private static final String APP_URL = "http://cafe-app:8081";

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
                "profile.password_manager_enabled", false
        ));

        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);
    }

    private void loginAndNavigateToMenu() {
        driver.get(APP_URL + "/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement userField = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
        userField.sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");

        driver.findElement(By.id("submit")).click();

        WebElement navMenus = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-menus")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navMenus);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuDate")));
    }

    @Test
    void testCreateMenuItemSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String uniqueItemName = "Latte " + System.currentTimeMillis();

        loginAndNavigateToMenu();

        WebElement dateField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("menuDate")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '2026-03-15';", dateField);

        WebElement categoryDropdown = driver.findElement(By.id("itemCategory"));
        new Select(categoryDropdown).selectByValue("Beverage");

        driver.findElement(By.id("itemName")).sendKeys(uniqueItemName);
        driver.findElement(By.id("itemDescription")).sendKeys("Testing E2E");
        driver.findElement(By.id("itemPrice")).sendKeys("4.50");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement responseMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menu-response-msg")));
        assertTrue(responseMsg.getText().toLowerCase().contains("successfully"));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}