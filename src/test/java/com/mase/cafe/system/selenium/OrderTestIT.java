package com.mase.cafe.system.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderTestIT {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("username")).sendKeys("manager1");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("loginBtn")).click();

        wait.until(ExpectedConditions.urlContains("/dashboard"));
        driver.get(BASE_URL + "/orders");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    @Order(1)
    void testCreateNewOrder() {

        wait.until(ExpectedConditions.elementToBeClickable(By.id("openAddOrderBtn"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ordername")))
                .sendKeys("Selenium Table Test");
        driver.findElement(By.id("totalAmount")).sendKeys("150.00");

        driver.findElement(By.id("saveOrderBtn")).click();

        WebElement tableRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[contains(text(), 'Selenium Table Test')]")));
        assertNotNull(tableRow);
    }

    @Test
    @Order(2)
    void testEditOrder() {

        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//td[text()='Selenium Table Test']/following-sibling::td//button[contains(@class, 'edit-btn')]")));
        editBtn.click();

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ordername")));
        nameInput.clear();
        nameInput.sendKeys("Updated Order Name");
        driver.findElement(By.id("saveOrderBtn")).click();

        WebElement updatedRow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//td[contains(text(), 'Updated Order Name')]")));
        assertNotNull(updatedRow);
    }

    @Test
    @Order(3)
    void testDeleteOrder() {

        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//td[text()='Updated Order Name']/following-sibling::td//button[contains(@class, 'delete-btn')]")));
        deleteBtn.click();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmDeleteBtn"))).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//td[contains(text(), 'Updated Order Name')]")));
    }
}