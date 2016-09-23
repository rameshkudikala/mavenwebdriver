package com.xiim.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import com.xiim.util.*;

public class TestBase {
	
	public static boolean loggedIn = false;
	//public static Logger APP_LOGS = null;
	public static Properties l4j = null;
	public static Properties CONFIG = null;
	public static Properties OR = null;
	public static WebDriver driver = null;
	public static Logger APP_LOGS = Logger.getLogger(TestBase.class.getClass());
	public static EventFiringWebDriver wbDv = null;
	public static Xls_Reader suiteXls = null;
	public static Xls_Reader suiteXls1 = null;
	public static Xls_Reader suiteXls2 = null;
	public static Xls_Reader suiteXls3 = null;
	public static Xls_Reader suiteXls4 = null;
	public static Xls_Reader suite_xiim_xls = null;
	public static Xls_Reader suite_Login_xls = null;
	public static String errormsg=null;
	public static String Assertionerror=null;
	// This is Xls reader File 
	
	public static Xls_Reader signup_xls=null;

	public static boolean isInitalized = false;
	public static boolean isBrowserOpened = false;
	public static Hashtable<String, String> sessionData = new Hashtable<String, String>();
	
    //initializing the Tests
	public void initialize() throws Exception {
		// logs
		if (!isInitalized) {
			
			l4j = new Properties();
			FileInputStream fl4j = new FileInputStream(System.getProperty("user.dir")+"\\src\\test\\java\\com\\xiim\\config\\log4j.properties");
			l4j.load(fl4j);
			
			PropertyConfigurator.configure(System.getProperty("user.dir")+"\\src\\test\\java\\com\\xiim\\config\\log4j.properties");
			
			APP_LOGS = Logger.getLogger("XiimLogs");
			// config
			APP_LOGS.debug("Loading Property files");
			CONFIG = new Properties();
			FileInputStream ip = new FileInputStream(System.getProperty("user.dir")+ "//src//test//java//com//xiim//config//config.properties");
			CONFIG.load(ip);

			OR = new Properties();
			ip = new FileInputStream(System.getProperty("user.dir")+ "//src//test//java//com//xiim//config//OR.properties");
			OR.load(ip);
			APP_LOGS.debug("Loaded Property files successfully");
			APP_LOGS.debug("Loading XLS Files");

			// sign up xls file
			suite_xiim_xls = new Xls_Reader(System.getProperty("user.dir")+ "//src//test//java//com//xiim//xls//TestData Suite.xlsx");
			APP_LOGS.debug("Loaded XLS Files successfully");
			isInitalized = true;
			
			suiteXls = new Xls_Reader(System.getProperty("user.dir")+ "//src//test//java//com//xiim//xls//XiiMSuite.xlsx");
            APP_LOGS.debug("XiiM XLS Files loaded successfully.....");
			isInitalized = true;
		}
	}
                

    //open a browser if its not opened
	public void openBrowser() {
		if (!isBrowserOpened) {
			if (CONFIG.getProperty("browserType").equals("Firefox")){
				driver = new FirefoxDriver();
				
			}
			else if (CONFIG.getProperty("browserType").equals("IE")){
				System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+ "\\src\\test\\resources\\drivers\\IEDriverServer.exe");
				DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
				// this line of code is to resolve protected mode issue capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				driver = new InternetExplorerDriver(capabilities);
							
			}
			else if (CONFIG.getProperty("browserType").equals("CHROME")){
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+ "\\src\\test\\resources\\drivers\\chromedriver.exe");
				driver = new ChromeDriver();
				
			}

			isBrowserOpened = true;
			driver.manage().window().maximize();
			String waitTime = CONFIG.getProperty("default_implicitWait");
			driver.manage().timeouts().implicitlyWait(Long.parseLong(waitTime), TimeUnit.SECONDS);
		}
	}

    //close browser
	public void closeBrowser() {
		driver.quit();
		isBrowserOpened = false;
	}
	// Login to Xiim With Valid Credentials
			public static void Login_Valid(String Email, String Password){
				try{
					getObject("login_link").click();
				getObject("login_Email_text").sendKeys(Email);
				getObject("login_Pwd_text").sendKeys(Password);
				getObject("login_btn").click();
				}catch(Exception t){
					throw new AssertionError(errormsg);
				}
				
			}

    //compare titles
	public boolean compareTitle(String expectedVal) {
		try {
			Assert.assertEquals(driver.getTitle(), expectedVal);
		} catch (Throwable t) {
			APP_LOGS.debug("Titles do not match");
			return false;
		}
		return true;
	}
	public static boolean getResponseCode(String urlString) {
        boolean isValid = false;
        try {
            URL u = new URL(urlString);
            HttpURLConnection h = (HttpURLConnection) u.openConnection();
            h.setRequestMethod("GET");
            h.connect();
            System.out.println(h.getResponseCode());
            if (h.getResponseCode() == 200) {
                isValid = true;
            }
        } catch (Exception e) {

        }
        return isValid;
    }
	public static void selectDate(String Year, String Month, String date) throws InterruptedException {
		driver.findElement(By.xpath(OR.getProperty("MOdifierCalendarIcon"))).click();
		driver.findElement(By.xpath("html/body/div[5]/div/div[1]/table/thead/tr[1]/th[2]")).click();
		driver.findElement(By.xpath("html/body/div[5]/div/div[2]/table/thead/tr/th[2]")).click();
		String temp = driver.findElement(By.xpath("html/body/div[5]/div/div[3]/table/thead/tr/th[2]")).getText();

		String arr[] = temp.split("-");
		int i = Integer.parseInt(arr[0]);
		int j = Integer.parseInt(arr[1]);
		int year = Integer.parseInt(Year);
		System.out.println("year value is" + year);
		if (i <= year && year <= j) {
			WebElement dateWidget = driver.findElement(By.xpath("html/body/div[5]/div/div[3]/table/tbody"));
			List<WebElement> rows = dateWidget.findElements(By.tagName("tr"));
			
			for (WebElement row : rows) {
				List<WebElement> cols = row.findElements(By.tagName("td"));
				for (WebElement col : cols) {
					List<WebElement> values = col.findElements(By.tagName("span"));
					
					for (WebElement value : values) {
						int yearint = Integer.parseInt(value.getText());
						if (yearint == year) {
							Thread.sleep(2000);
							value.click();
							break;
						} else
							System.out.println("value is" + value.getText());
					}
				}
			}
		}

		WebElement dateWidget = driver.findElement(By.xpath("html/body/div[5]/div/div[2]/table/tbody"));
		List<WebElement> rows = dateWidget.findElements(By.tagName("tr"));
		for (WebElement row : rows) {
			List<WebElement> cols = row.findElements(By.tagName("td"));
			for (WebElement col : cols) {
				List<WebElement> values = col.findElements(By.tagName("span"));
				for (WebElement value : values) {
					if (value.getText().equals(Month)) {
						Thread.sleep(2000);
						value.click();
						break;
					} else
						System.out.println("value is" + value.getText());

				}
			}
		}

		WebElement dateWidget1 = driver.findElement(By.xpath("html/body/div[5]/div/div[1]/table/tbody"));
		List<WebElement> rows1 = dateWidget1.findElements(By.tagName("tr"));
		
		for (WebElement row1 : rows1) {
			List<WebElement> cols1 = row1.findElements(By.tagName("td"));
			for (WebElement col1 : cols1) {

				if (col1.getText().equals(date)) {
					Thread.sleep(2000);
					col1.click();
					break;
				}
			}
		}
	}

    //compare text
	public boolean compareText(String expectedVal, String actualValue) {
		try {
			Assert.assertEquals(actualValue, expectedVal);
		} catch (Throwable t) {
			APP_LOGS.debug("Titles do not match");
			return false;
		}
		return true;
	}

                /*
                * public static void compareText(String xpath, String data) { try {
                * driver.findElement(By.xpath("//option[contains(text(),'" + data +
                * "')]")).click(); } catch (Exception e) { // TODO Auto-generated catch
                * block e.printStackTrace(); } }
                */

    //compaerStrings
    //compare titles
	public boolean compareNumbers(int expectedVal, int actualValue) {
		try {
			Assert.assertEquals(actualValue, expectedVal);
		} catch (Throwable t) {
			APP_LOGS.debug("Values do not match");
			return false;
		}
		return true;
	}

	public boolean checkElementPresence(String xpathKey) {
		int count = driver.findElements(By.xpath(OR.getProperty(xpathKey))).size();

		try {
			Assert.assertTrue(count > 0, "No element present");
		} catch (Throwable t) {
			APP_LOGS.debug("No element present");
			return false;
		}
		return true;
	}

	//your own functions
	public static WebElement getObject(String xpathKey) {

		WebElement x = null;
		try{
			x = driver.findElement(By.xpath(OR.getProperty(xpathKey)));
			return x;
		}catch(Exception t){
			errormsg = xpathKey+"\t:"+t.getMessage();
			System.out.println(errormsg);
			throw new AssertionError(errormsg);
		}
	}

    // many companies
	public static void adminLogin(String UserName, String Password) {
		// log u in
		try {
			driver.findElement(By.xpath(OR.getProperty("username"))).sendKeys(UserName);
			driver.findElement(By.xpath(OR.getProperty("password"))).sendKeys(Password);
			driver.findElement(By.xpath(OR.getProperty("Login"))).click();
			driver.findElement(By.xpath("html/body/div[2]/form/div/div/div[4]/div/button[1]")).click();
			fluentWait("//*[@id='btnContinue']");
			driver.findElement(By.xpath(OR.getProperty("Continue"))).click();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void doLogout() {
		try {
			getObject("login_user_link").click();
			getObject("signout_link").click();
		
			Assert.assertEquals(driver.getTitle(), "Login");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void userLogin(String email, String password) {
		// log u in
		// try{
		driver.findElement(By.xpath(OR.getProperty("username"))).sendKeys(email);
		driver.findElement(By.xpath(OR.getProperty("password"))).sendKeys(password);
		getObject("Login").click();
		/*
		 * }catch(Throwable t){ ErrorUtil.addVerificationFailure(t); }
		 */
	}

	public static void createCategory(String Category, String ServiceCodeType) {
		driver.findElement(By.xpath(OR.getProperty("SCAddCategory"))).click();
		driver.findElement(By.xpath(OR.getProperty("SCAddCateorytext"))).sendKeys(Category);
		Select dropdown = new Select(driver.findElement(By.xpath(OR.getProperty("SCAddType"))));
		dropdown.selectByVisibleText(ServiceCodeType);
		driver.findElement(By.xpath(OR.getProperty("SCCategorySave"))).click();
		driver.findElement(By.xpath(OR.getProperty("SCCategoryClose"))).click();
	}

	public void capturescreenshot(String filename) throws IOException {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir")+ "\\screenshots\\" + filename + ".jpg"));
	}

	public static void fluentWait(String xpathy) {
		new FluentWait<WebDriver>(driver)
				.withTimeout(10, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class)
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OR.getProperty(xpathy))));
	}

	public static void selectValue(String xpath1, String data1) {
		try {
			driver.findElement(By.xpath("//option[contains(text(),'" + data1 + "')]")).click();
		} catch (Throwable t) {
			// TODO Auto-generated catch block
			t.printStackTrace();
		}
	}

	public static void textVerification(String xpath, String data) {
		try {
			driver.findElement(By.xpath("//option[contains(text(),'" + data + "')]")).getText();
		}catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void enterData(String xpath, String data) {

		try {
			// logger.debug("Entering  in"+xpath);
			/*
			 * driver.manage().timeouts() .implicitlyWait(implicitWait,
			 * TimeUnit.SECONDS);
			 */
			driver.findElement(By.xpath(OR.getProperty(xpath))).sendKeys(data);
			// logger.debug("Entered  in"+xpath);
			// takeScheenShort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


// testing
	@AfterSuite
	public static void quit() {
		driver.quit();
	}

	}