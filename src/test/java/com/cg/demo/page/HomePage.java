package com.cg.demo.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.server.handler.SendKeys;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {
	
	WebDriver driver;

	@FindBy(xpath="//*[@id=\"top-links\"]/ul/li[2]/a")
	WebElement myaccount;

	@FindBy(css="#top-links > ul > li.dropdown.open > ul > li:nth-child(2) > a")
	WebElement loginbtn;
	
	@FindBy(xpath="//*[@id=\"menu\"]/div[2]/ul/li[1]/a")
	WebElement desktops;
	
	@FindBy(xpath="//*[@id=\"menu\"]/div[2]/ul/li[1]/div/a")
	WebElement showalldesktop;

	@FindBy(xpath="//*[@id=\"content\"]/div[4]/div[3]/div/div[2]/div[1]/h4/a")
	WebElement HP_LP3065;

	@FindBy(xpath="//*[@id=\"button-cart\"]")
	WebElement add_to_cart;

	@FindBy(xpath="//*[@id=\"top-links\"]/ul/li[4]")
	WebElement cart;
	
	@FindBy(xpath="//*[@id=\"content\"]/div[3]/div[2]/a")
	WebElement checkout;
	
	public HomePage(WebDriver driver) {
		PageFactory.initElements(driver, this);
		this.driver = driver;
	}
	
	public void clickMyaccount() {
		myaccount.click();
	}
	public void clickLogin() {
		loginbtn.click();
	}
	public void clickDesktop() {
		desktops.click();
	}
	public void clickShowAllDesktops() {
		showalldesktop.click();
	}
	public void selectProduct(String productname) {
		driver.findElement(By.partialLinkText(productname)).click();
		//HP_LP3065.click();
	}
	public void addProductToCart() {
		add_to_cart.click();
	}
	public void viewCart() {
		cart.click();
	}
	public void clickCheckouts() {
		checkout.click();
	}
	public void enterQty(String qty){
		driver.findElement(By.id("input-quantity")).clear();
		driver.findElement(By.id("input-quantity")).sendKeys(qty);
	}
	public String getQtyErrorMessage(){
		String error = driver.findElement(By.xpath("//*[@id=\"product-product\"]/div[1]")).getText();
		error = error.substring(0, error.length()-2);
		return error;
	}
	
}
