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

import static org.junit.jupiter.api.Assertions.assertFalse;
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
                "profile.password_manager_enabled", false,
                "password_manager_leak_detection", false
        ));

        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);
        driver.get(APP_URL);

    }

    private void loginAndNavigateToMenu() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement userField = wait.until(ExpectedConditions.elementToBeClickable(By.id("username")));
        userField.sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");

        WebElement submitBtn = driver.findElement(By.id("submit"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        WebElement navMenus = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nav-menus")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navMenus);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuDate")));
    }

    @Test
    void testCreateMenuItemSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        String uniqueItemName = "Latte " + System.currentTimeMillis();

        loginAndNavigateToMenu();

        WebElement dateField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("menuDate")));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '2026-03-15';" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                dateField
        );

        WebElement categoryDropdown = driver.findElement(By.id("itemCategory"));
        new Select(categoryDropdown).selectByValue("Beverage");
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'));", categoryDropdown);

        driver.findElement(By.id("itemName")).sendKeys(uniqueItemName);
        driver.findElement(By.id("itemDescription")).sendKeys("Testing with event-dispatched JS injection");
        driver.findElement(By.id("itemPrice")).sendKeys("4.50");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        try {
            WebElement responseMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menu-response-msg")));
            String actualText = responseMsg.getText();
            assertTrue(actualText.toLowerCase().contains("successfully"),
                    "Expected a success message but received: " + actualText);
        } catch (TimeoutException e) {
            String validationError = (String) ((JavascriptExecutor) driver).executeScript(
                    "return Array.from(document.querySelectorAll(':invalid')).map(el => (el.id || el.name) + ': ' + el.validationMessage).join(' | ');"
            );
            System.err.println("DEBUG: HTML5 Validation Errors: " + validationError);
            throw e;
        }
    }

    @Test
    void testMenuFormValidation() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        loginAndNavigateToMenu();

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        WebElement dateField = driver.findElement(By.id("menuDate"));
        String validationMessage = dateField.getAttribute("validationMessage");
        assertFalse(validationMessage.isEmpty(), "Validation message should be present for empty date");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}