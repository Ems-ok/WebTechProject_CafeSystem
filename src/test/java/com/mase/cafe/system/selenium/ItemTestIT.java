package com.mase.cafe.system.selenium;

import com.mase.cafe.system.models.Item;
import com.mase.cafe.system.models.Menu;
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

    @Autowired private MenuRepository menuRepository;
    @Autowired private ItemRepository itemRepository;

    private Long targetItemId;

    @BeforeEach
    void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);
        driver.get(APP_URL);

        itemRepository.deleteAll();
        itemRepository.flush();
        menuRepository.deleteAll();
        menuRepository.flush();

        Menu menu = new Menu();
        menu.setMenuDate(LocalDate.now());

        Menu savedMenu = menuRepository.save(menu);
        menuRepository.flush();

        Item item = new Item();
        item.setName("Original Latte");
        item.setCategory("Beverage");
        item.setPrice(3.50);
        item.setDescription("Freshly brewed");
        item.setMenu(savedMenu);

        Item savedItem = itemRepository.save(item);
        itemRepository.flush();

        targetItemId = savedItem.getId();
    }

    private void login() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("username"))).sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.id("submit")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"brandLink\"]")));
    }

    @Test
    void testUpdateItemSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        login();

        WebElement navItems = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nav-menus")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navItems);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"menuCardsContainer\"]/div/div/div[2]/ul/li/div[1]/span")));

        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("edit-btn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("itemName")));
        nameField.clear();
        nameField.sendKeys("Updated Caramel Latte");

        WebElement priceField = driver.findElement(By.id("itemPrice"));
        priceField.clear();
        priceField.sendKeys("5.50");

        WebElement updateBtn = driver.findElement(By.id("submitBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", updateBtn);

        wait.until(d -> !d.findElement(By.id("menu-response-msg")).getText().trim().isEmpty());
        String resultText = driver.findElement(By.id("menu-response-msg")).getText();

        assertTrue(resultText.toLowerCase().contains("successfully") || resultText.toLowerCase().contains("updated"),
                "Expected update success message but got: " + resultText);

        Item updatedItem = itemRepository.findById(targetItemId).orElseThrow();
        assertEquals("Updated Caramel Latte", updatedItem.getName());
        assertEquals(5.50, updatedItem.getPrice());
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