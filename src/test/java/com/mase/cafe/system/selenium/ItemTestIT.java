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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class ItemTestIT {

    private WebDriver driver;
    private static final String APP_URL = "http://cafe-app:8081";

    @Autowired private MenuRepository menuRepository;
    @Autowired private ItemRepository itemRepository;

    @BeforeEach
    void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);

        itemRepository.deleteAll();
        menuRepository.deleteAll();

        driver.get(APP_URL);
    }

    private void loginAndNavigate() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        List<WebElement> usernameFields = driver.findElements(By.id("username"));
        if (!usernameFields.isEmpty()) {
            usernameFields.get(0).sendKeys("manager");
            driver.findElement(By.id("password")).sendKeys("manager");
            driver.findElement(By.id("submit")).click();
        }

        WebElement nav = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-menus")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nav);
    }

    @Test
    void testUpdateItemSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        loginAndNavigate();

        String itemName = "Latte-" + System.currentTimeMillis();
        createItemViaUI(itemName, "4.50");

        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("edit-btn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("itemName")));
        nameField.clear();
        nameField.sendKeys("Updated Latte");

        WebElement priceField = driver.findElement(By.id("itemPrice"));
        priceField.clear();
        priceField.sendKeys("5.50");

        WebElement submitBtn = driver.findElement(By.id("submitBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        wait.until(d -> !d.findElement(By.id("menu-response-msg")).getText().trim().isEmpty());
        assertTrue(driver.findElement(By.id("menu-response-msg")).getText().toLowerCase().contains("successfully"));
    }

    @Test
    void testCreateValidation() {
        loginAndNavigate();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("itemName")));
        nameField.clear();

        WebElement submitBtn = driver.findElement(By.id("submitBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        assertFalse(nameField.getAttribute("validationMessage").isEmpty(), "HTML5 validation should trigger");
    }

    private void createItemViaUI(String name, String price) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement dateField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("menuDate")));
        String today = LocalDate.now().toString();
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '" + today + "';" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", dateField);

        new Select(driver.findElement(By.id("itemCategory"))).selectByVisibleText("Beverage");

        driver.findElement(By.id("itemName")).sendKeys(name);
        driver.findElement(By.id("itemPrice")).sendKeys(price);
        driver.findElement(By.id("itemDescription")).sendKeys("Test Desc");

        WebElement submitBtn = driver.findElement(By.id("submitBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("menuCardsContainer"), name));
    }

    @Test
    void testDeleteItemWithCustomModal() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        String itemName = "Hot Chocolate";

        loginAndNavigate();

        createItemViaUI(itemName, "3.50");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".delete-btn")));

        WebElement deleteIconButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".delete-btn")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", deleteIconButton);
        deleteIconButton.click();

        WebElement confirmDeleteBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmDeleteBtn")));

        WebElement modalBody = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-body")));
        assertTrue(modalBody.getText().contains("Are you sure you want to remove this item?"));

        confirmDeleteBtn.click();

        boolean isDeleted = wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//span[contains(text(),'" + itemName + "')]")));

        assertTrue(isDeleted, "Item '" + itemName + "' should no longer be visible in the menu cards");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}