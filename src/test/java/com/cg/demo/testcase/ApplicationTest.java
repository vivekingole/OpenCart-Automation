package com.cg.demo.testcase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.cg.demo.launch.LoadDriver;
import com.cg.demo.page.CheckoutPage;
import com.cg.demo.page.HomePage;
import com.cg.demo.page.LoginPage;
import com.cg.demo.util.Capture;
import com.cg.demo.util.ExcelReader;

public class ApplicationTest{

	static WebDriver driver;
	static HomePage home;
	static LoginPage login;
	static CheckoutPage checkout;
	int addresscount=1;
	int qtycount=1;
	private ExcelReader reader;
	private ExcelReader qtyreader;
	static ExtentHtmlReporter report=new ExtentHtmlReporter("./Report/Report.html");
	static ExtentReports extent=new ExtentReports();
	static ExtentTest logger;

	@BeforeClass
	public static void launchBrowser(){    	
		 driver = LoadDriver.loadBrowser("chrome", "http://demo.opencart.com/");	
		 driver.manage().window().maximize();
		 driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		 home = new HomePage(driver);
		 login = new LoginPage(driver);
		 checkout = new CheckoutPage(driver);
		 extent.attachReporter(report);
	}
	
	@BeforeMethod
	public void setup(Method method) {
    	logger=extent.createTest(method.getName());
	}
	
	@AfterMethod
	public void checkStatus(ITestResult result) {
		try {
			if(result.getStatus()== ITestResult.FAILURE) {
				String path = Capture.captureScreenshot(driver);
				logger.fail(result.getThrowable().getMessage(), MediaEntityBuilder.createScreenCaptureFromPath(path).build());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		extent.flush();
	}
	
	@Test(priority = 0)
	public void launchBrowserTest() {
		if(driver == null)
			logger.log(Status.FAIL, "Launch Browser Failure");
		else
			logger.log(Status.PASS, "Launch Browser Success");
		extent.flush();		
	}

	@Test(priority=1)
	public void login(){
		home.clickMyaccount();
		home.clickLogin();
		
		logger.log(Status.INFO, "Entering Login Details...");		
		login.setUsername("vivek1@gmail.com");
		login.setPassword("12345");
		login.clickLogin();
		
		String title=driver.getTitle();
		
		if(title.equals("My Account")) // verify title to check if user is successfully logged in or not.
			logger.log(Status.PASS, "Login Successfull");
		else
			logger.log(Status.FAIL, "Login Failure");
	}
	
	@Test(priority = 2)
	public void browseProduct() {
		home.clickDesktop();
		logger.log(Status.INFO, "Selects Desktop");		
		
		home.clickShowAllDesktops();
		logger.log(Status.INFO, "Selects Show all Desktop");		
		
		home.selectProduct("HP LP3065");			
		logger.log(Status.INFO, "Product 'HP LP3065' Selected");
	}
	
	@Test(priority = 3, dataProvider = "getQty") 
	public void addProductToCart(String qty) throws InterruptedException { 
		try {
			
			home.enterQty(qty); 
			home.addProductToCart();
			Thread.sleep(3000); 
		
			String actual= home.getQtyErrorMessage();
			String expected = qtyreader.getCellData(qtycount, 2);
			if(actual.equals(expected))
				qtyreader.setCellData(qtycount, 1, "Passed"); // Making status Passed if Error message is verified.
			else
				qtyreader.setCellData(qtycount, 1, "Failed"); // Making status Failed if Error message is not verified.
			qtycount++;
			Assert.assertEquals(actual,expected);	
			logger.log(Status.PASS, "Qty value : "+qty+" Accepted");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	  
	@Test(priority = 4)
	public void nevigateToCheckout() throws InterruptedException {
		
		home.viewCart(); 
		Thread.sleep(3000);
		logger.log(Status.INFO, "Cart Displayed");
		
		home.clickCheckouts(); 
		Thread.sleep(3000); 
		logger.log(Status.INFO, "Nevigated to checkout page");
	}

	@Test(priority=5, dataProvider="getAddressData")
	public void enterAddress(String first,String last,String addr1,String city,String pincode,String country,String state) throws InterruptedException{		
		checkout.selectNewAddress();
		logger.log(Status.INFO, "New Address selected");
		checkout.enterBillingDetails(first, last, addr1, city, pincode, country, state);
		logger.log(Status.INFO, "Address Data : " + first + " , " + last + " , " + addr1 + " , " + city + " , "+ pincode + " , " + country + " , " + state);
		if (first.equals("") && last.equals("") && addr1.equals("") && city.equals("")) {
			
			Assert.assertEquals(checkout.getFirstFieldError(), "First Name must be between 1 and 32 characters!","Error Message Not Displayed!!");
			logger.log(Status.PASS, "Error message of first name field is verified");
			
			Assert.assertEquals(checkout.getLastFieldError(), "Last Name must be between 1 and 32 characters!","Error Message Not Displayed!!");
			logger.log(Status.PASS, "Error message of first name field is verified");
			
			Assert.assertEquals(checkout.getAddr1FieldError(), "Address 1 must be between 3 and 128 characters!","Error Message Not Displayed!!");
			logger.log(Status.PASS, "Error message of first name field is verified");
			
			Assert.assertEquals(checkout.getCityFieldError(), "City must be between 2 and 128 characters!","Error Message Not Displayed!!");
			logger.log(Status.PASS, "Error message of first name field is verified");
			
			reader.setCellData(addresscount, reader.getColCount(), "Passed"); // Making Status Passed if add error message is verified.
		} else {
			reader.setCellData(addresscount, reader.getColCount(), "Passed");
		}
		Thread.sleep(5000);
		addresscount++;
	}

	@Test(priority=6)
	public void enterDeliveryDetails() throws InterruptedException{
		checkout.enterDeliveryDetails();
		logger.log(Status.INFO, "Delivery Details Entered");
		Thread.sleep(5000);
	}
	
	@Test(priority=7)
	public void enterDeliveryMethod() throws InterruptedException{
		checkout.enterDeliveryMethod();
		logger.log(Status.INFO, "Delivery Method Details Entered");
		Thread.sleep(3000);
	}
	
	@Test(priority = 8)
	public void acceptTerms() throws InterruptedException{
		driver.findElement(By.cssSelector("#collapse-payment-method > div > div.buttons > div > input[type=checkbox]:nth-child(2)")).click();
		logger.log(Status.INFO, "Terms and condition Accepted.");
		checkout.clickStep5Continue();
		Thread.sleep(3000);
		checkout.confirmOrder();
		logger.log(Status.INFO, "Order is confirmed.");
		Thread.sleep(3000);
	}
	
	@AfterClass
	public void closeBrowser(){
		driver.close();
	}

	@DataProvider
	public Object[][] getAddressData(){
		String projectpath = System.getProperty("user.dir");
		String filepath = projectpath + "/testdata/";
		String filename = "TestData.xlsx";
		String sheetname = "Address";		
		reader = new ExcelReader(filepath+filename, sheetname,1);
		Object data[][]  = reader.getAllData();
		return data;
	}

	@DataProvider
	public Object[][] getQty(){
		String projectpath = System.getProperty("user.dir");
		String filepath = projectpath + "/testdata/";
		String filename = "TestData.xlsx";
		String sheetname = "Qty";		
		qtyreader = new ExcelReader(filepath+filename, sheetname,2);
		Object data[][]  = qtyreader.getAllData();
		return data;
	}
	

}
