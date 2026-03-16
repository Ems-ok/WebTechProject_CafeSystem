package com.mase.cafe.system.selenium;

import com.mase.cafe.system.models.Menu;
import com.mase.cafe.system.repositories.MenuRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class MenuTestIT {
    WebDriver driver;
    private static final String APP_URL = "http://cafe-app:8081";
    @Autowired private MenuRepository menuRepository;

    @BeforeEach
    void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);
        driver.get(APP_URL);

        menuRepository.deleteAll();
        menuRepository.flush();

        LocalDate testDate = LocalDate.parse("2026-03-15");
        Menu menu = new Menu();
        menu.setMenuDate(testDate);
        menuRepository.saveAndFlush(menu);
    }

    private void loginAndNavigateToMenu() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("username"))).sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.id("submit")));
        WebElement navMenus = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nav-menus")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navMenus);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuDate")));
    }

    @Test
    void testCreateMenuItemSuccess() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        loginAndNavigateToMenu();

        WebElement dateField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("menuDate")));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '2026-03-15';" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", dateField);

        new Select(driver.findElement(By.id("itemCategory"))).selectByValue("Beverage");
        driver.findElement(By.id("itemName")).sendKeys("Latte " + System.currentTimeMillis());
        driver.findElement(By.id("itemDescription")).sendKeys("Automated Test");
        driver.findElement(By.id("itemPrice")).sendKeys("4.50");

        WebElement submitBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        wait.until(d -> !d.findElement(By.id("menu-response-msg")).getText().trim().isEmpty());

        String resultText = driver.findElement(By.id("menu-response-msg")).getText();

        assertTrue(resultText.toLowerCase().contains("successfully"),
                "Expected success message but received: " + resultText);
    }

    @Test
    void testMenuFormValidation() {
        loginAndNavigateToMenu();
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.cssSelector("button[type='submit']")));
        assertFalse(driver.findElement(By.id("menuDate")).getAttribute("validationMessage").isEmpty());
    }

    @AfterEach
    void tearDown() { if (driver != null) driver.quit(); }
}