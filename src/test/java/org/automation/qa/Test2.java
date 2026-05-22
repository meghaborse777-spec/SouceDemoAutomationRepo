package org.automation.qa;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

public class Test2 {
	
	@Test
	public void Test1() {
		
		ChromeOptions co=new ChromeOptions();
		co.addArguments("--headless=new");
        co.addArguments("--disable-gpu");
		WebDriver driver=new ChromeDriver(co);
		driver.get("https://www.google.com/");
	}

}
