package org.automation.qa;

import java.lang.reflect.Method;
import java.nio.file.Paths;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class LoginTest {

    WebDriver driver;
    ExtentReports extent;
    ExtentTest test;

    @BeforeSuite
    public void suiteSetup() {

        //  Cross-platform report path (Linux + Windows)
        String reportPath = Paths.get("Report", "Extent.html").toString();

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("SauceDemo Automation Test");

        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(Method method) {

        ChromeOptions options = new ChromeOptions();

        //  CI-safe Chrome flags
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        //  Debug (temporary – keep for 1 run)
        System.out.println("ChromeOptions = " + options.asMap());

        test = extent.createTest(method.getName());

        driver = new ChromeDriver(options);
        driver.get("https://www.saucedemo.com/");
    }

    @Test
    public void FT001_Valid_Credentials() {

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        WebElement homePageProduct =
                driver.findElement(By.xpath("//span[@data-test='title']"));

        Assert.assertEquals(homePageProduct.getText(), "Products");
    }

    @Test
    public void FT002_Invalid_Credentials() {

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.id("login-button")).click();

        WebElement errorElement =
                driver.findElement(By.xpath("//h3[@data-test='error']"));

        Assert.assertEquals(
                errorElement.getText(),
                "Epic sadface: Username and password do not match any user in this service"
        );
    }

    @Test
    public void EDT003_BlankFeilds() {

        driver.findElement(By.id("login-button")).click();

        WebElement errorElement =
                driver.findElement(By.xpath("//h3[@data-test='error']"));

        Assert.assertEquals(
                errorElement.getText(),
                "Epic sadface: Username is required"
        );
    }

    @Test
    public void EDT004_SpecialCharacter() {

        driver.findElement(By.id("user-name")).sendKeys("#%^$^$&%");
        driver.findElement(By.id("password")).sendKeys("%$^$&%&*%*");
        driver.findElement(By.id("login-button")).click();

        WebElement errorElement =
                driver.findElement(By.xpath("//h3[@data-test='error']"));

        Assert.assertTrue(errorElement.isDisplayed());
    }

    @Test
    public void EDT005_LongCharacter() {

        driver.findElement(By.id("user-name")).sendKeys(
                "verylongusernameverylongusernameverylongusername");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("login-button")).click();

        WebElement errorElement =
                driver.findElement(By.xpath("//h3[@data-test='error']"));

        Assert.assertTrue(errorElement.isDisplayed());
    }

    @Test
    public void ST_SQLInjection() {

        driver.findElement(By.id("user-name")).sendKeys("admin' OR '1'='1");
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.id("login-button")).click();

        WebElement errorElement =
                driver.findElement(By.xpath("//h3[@data-test='error']"));

        Assert.assertTrue(errorElement.isDisplayed());
    }

    @Test
    public void ST_CSS_XSScripting() {

        driver.findElement(By.id("user-name"))
              .sendKeys("<script>alert('xss')</script>");
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.id("login-button")).click();

        WebElement errorElement =
                driver.findElement(By.xpath("//h3[@data-test='error']"));

        Assert.assertTrue(errorElement.isDisplayed());
    }

    @Test
    public void ST_HTTPSEnforcement() {

        driver.get("http://www.saucedemo.com/");
        Assert.assertTrue(driver.getCurrentUrl().startsWith("https"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        extent.flush();
    }
}
