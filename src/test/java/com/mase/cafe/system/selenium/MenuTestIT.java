package com.mase.cafe.system.selenium;

import com.mase.cafe.system.models.Menu;
import com.mase.cafe.system.repositories.MenuRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class MenuTestIT {

    WebDriver driver;

    private static final String APP_URL = "http://cafe-app:8081";
    private static final LocalDate TEST_DATE = LocalDate.of(2026, 3, 15);

    @Autowired
    private MenuRepository menuRepository;

    @BeforeEach
    void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");

        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);

        seedMenuData();

        driver.get(APP_URL);
    }

    private void seedMenuData() {

        if(menuRepository.findByMenuDate(TEST_DATE).isEmpty()) {
            Menu menu = new Menu();
            menu.setMenuDate(TEST_DATE);
            menuRepository.saveAndFlush(menu);
            System.out.println("DEBUG: Seeded Menu for " + TEST_DATE);
        }
    }

    private void loginAndNavigateToMenu() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("username"))).sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");

        WebElement submitBtn = driver.findElement(By.id("submit"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

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
        String dateForBrowser = TEST_DATE.toString();
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '" + dateForBrowser + "';" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                dateField
        );

        WebElement categoryDropdown = driver.findElement(By.id("itemCategory"));
        new Select(categoryDropdown).selectByValue("Beverage");

        driver.findElement(By.id("itemName")).sendKeys(uniqueItemName);
        driver.findElement(By.id("itemDescription")).sendKeys("Pipeline Test Item");
        driver.findElement(By.id("itemPrice")).sendKeys("4.50");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        try {
            WebElement responseMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menu-response-msg")));

            wait.until(d -> !responseMsg.getText().trim().isEmpty());

            String actualText = responseMsg.getText();
            assertTrue(actualText.toLowerCase().contains("successfully"),
                    "Expected success message but got: " + actualText);
        } catch (TimeoutException e) {
            System.err.println("DEBUG: Page Source on Failure: " + driver.getPageSource());
            throw e;
        }
    }

    @Test
    void testMenuFormValidation() {
        loginAndNavigateToMenu();
        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        WebElement dateField = driver.findElement(By.id("menuDate"));
        String validationMessage = dateField.getAttribute("validationMessage");
        assertFalse(validationMessage.isEmpty(), "HTML5 validation should trigger for empty fields");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}