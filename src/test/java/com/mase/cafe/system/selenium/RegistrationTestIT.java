package com.mase.cafe.system.selenium;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class RegistrationTestIT {
    WebDriver driver;
    private static final String APP_URL = "http://cafe-app:8081";

    @BeforeEach
    void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new RemoteWebDriver(new URL("http://selenium-chrome:4444/wd/hub"), options);
        driver.get(APP_URL);
    }

    private void loginAndNavigateToUsers() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("manager");
        driver.findElement(By.id("password")).sendKeys("manager");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.id("submit")));
        WebElement navUsers = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-users")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", navUsers);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("openAddUserBtn")));
    }

    @Test
    void testRegistrationSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        loginAndNavigateToUsers();
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.id("openAddUserBtn")));
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));

        String uniqueUser = "user" + System.currentTimeMillis();
        modal.findElement(By.id("username")).sendKeys(uniqueUser);
        modal.findElement(By.id("password")).sendKeys("Test@1234!");
        modal.findElement(By.id("role")).sendKeys("MANAGER");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", modal.findElement(By.id("saveUserBtn")));

        assertTrue(wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("userTable"), uniqueUser)));
    }

    @Test
    void testRegistrationDuplicateAccount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        loginAndNavigateToUsers();
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.id("openAddUserBtn")));
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));

        modal.findElement(By.id("username")).sendKeys("testuser");
        modal.findElement(By.id("password")).sendKeys("Test@1234!");
        modal.findElement(By.id("role")).sendKeys("MANAGER");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", modal.findElement(By.id("saveUserBtn")));

        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (TimeoutException e) {

            WebElement visibleModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userModal")));
            assertTrue(visibleModal.isDisplayed());
        }
    }

    @AfterEach
    void tearDown() { if (driver != null) driver.quit(); }
}