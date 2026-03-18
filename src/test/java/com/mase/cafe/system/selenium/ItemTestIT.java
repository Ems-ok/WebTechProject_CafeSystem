package com.mase.cafe.system.selenium;

import com.mase.cafe.system.repositories.ItemRepository;
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
class ItemTestIT {

    WebDriver driver;
    private static final String APP_URL = "http://cafe-app:8081";

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MenuRepository menuRepository;

    @BeforeEach
    void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);

        itemRepository.deleteAll();
        menuRepository.deleteAll();

        driver.get(APP_URL);
        login();
        createItemViaUI("Original Latte", "3.50");
    }

    private void createItemViaUI(String name, String price) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement nav = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-menus")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nav);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("itemName"))).sendKeys(name);
        driver.findElement(By.id("itemPrice")).sendKeys(price);
        driver.findElement(By.id("itemDescription")).sendKeys("Freshly brewed");

        WebElement dateField = driver.findElement(By.id("menuDate"));
        String today = LocalDate.now().toString();
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '" + today + "';", dateField);

        driver.findElement(By.id("submitBtn")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("menuCardsContainer"), name));
    }

    @Test
    void testUpdateItemSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("edit-btn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("itemName")));
        nameField.clear();
        nameField.sendKeys("Updated Caramel Latte");

        WebElement priceField = driver.findElement(By.id("itemPrice"));
        priceField.clear();
        priceField.sendKeys("5.50");

        driver.findElement(By.id("submitBtn")).click();

        wait.until(d -> !d.findElement(By.id("menu-response-msg")).getText().trim().isEmpty());
        assertTrue(driver.findElement(By.id("menu-response-msg")).getText().contains("successfully"));
    }

    private void login() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");
        driver.findElement(By.id("submit")).click();
    }

    @Test
    void testCreateValidation() {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement navItems = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-menus")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navItems);

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"itemName\"]")));

        nameField.clear();

        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("submitBtn")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        String validationMsg = nameField.getAttribute("validationMessage");
        assertFalse(validationMsg.isEmpty(), "The 'Required' validation should have blocked the submission.");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}