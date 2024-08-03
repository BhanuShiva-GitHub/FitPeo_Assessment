package Demo;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

public class FitPeoAssessment {
	static WebDriver driver;

	public static void main(String[] args) throws Exception {

		// Setup the Chrome driver
		System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		System.out.println("------------Driver setup Successfully------------");

		// Hit the URL
		driver.get("https://www.fitpeo.com");
		System.out.println("------------Naviagated to FitPeo Website Successfully------------");
		Thread.sleep(6000);

		// Click the Revenue Calculator Menu from the Header
		WebElement RevenueCalculator = driver
				.findElement(By.xpath("//div[contains(@class,'MuiToolbar')]//div[text()='Revenue Calculator']"));
		RevenueCalculator.click();
		System.out.println("------------RevenueCalculator is Clicked Successfully------------");
		Thread.sleep(3000);

		//To get the Slider Width
		WebElement Sliderwidth = driver.findElement(By.xpath("//span[contains(@class,'MuiSlider-colorPrimary')]"));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		int sliderWidth = ((Long) js.executeScript("return arguments[0].offsetWidth;", Sliderwidth)).intValue();
		System.out.println("------------Slider Width is: " + sliderWidth +"------------");

		// Drag and drop the slider to the desired Value (i.e 820) and Validate
        Actions action = new Actions(driver);
		WebElement Slider = driver.findElement(By.xpath("//span[contains(@class,'MuiSlider')]//input"));
		action.moveToElement(Slider);

		//Get the Slider Initial Position after landing into the Revenue Calculator Page
		int SliderInitialPosition = Integer.parseInt(Slider.getAttribute("value"));
		int SliderMinValue = 0; int SliderMaxValue = 2000;
		int DesiredValue = 820;
		// Calculate the slider range
        int SliderRange = SliderMaxValue - SliderMinValue;
        // Calculate the position of the initial value as a fraction of the slider range
        double InitialPosition = (double) (SliderInitialPosition - SliderMinValue) / SliderRange;
        // Calculate the desired position as a fraction of the slider range
        double DesiredPosition = (double) (DesiredValue - SliderMinValue) / SliderRange;
        // Calculate the pixel position on the slider relative to the initial value
        int PixelPosition = (int) ((DesiredPosition - InitialPosition) * sliderWidth);
        
		action.clickAndHold(Slider).moveByOffset(PixelPosition, 0).build().perform();
		System.out.println("------------Slider is dragged Successfully------------");
		Thread.sleep(3000);

		// Enter the desired Patients Number in the Text Field
		WebElement MedicareEligiblePatientsEditField = driver
				.findElement(By.xpath("//div[contains(@class,'MuiTextField')]//input"));
		MedicareEligiblePatientsEditField.click();
		MedicareEligiblePatientsEditField.sendKeys(Keys.CONTROL + "a");
		MedicareEligiblePatientsEditField.sendKeys(Keys.DELETE);
		MedicareEligiblePatientsEditField.sendKeys("560");
		System.out.println("------------Entered the desired Medicare Eligible Patients Successfully------------");
		Assert.assertEquals(Slider.getAttribute("value"), "560");
		Thread.sleep(3000);

		// Selecting the Required CPT Codes and Validate
		List<String> ExpectedCPTCodes = Arrays.asList("CPT-99091", "CPT-99453", "CPT-99454", "CPT-99474");
		for (String strExpectedCPTCodes : ExpectedCPTCodes) {
			WebElement CPTCheckbox = driver.findElement(
					By.xpath("//p[text()='" + strExpectedCPTCodes + "']/parent::div//input[@type='checkbox']"));
			CPTCheckbox.click();
			Thread.sleep(1000);

		}
		System.out.println("------------Selected Required CPT Codes Successfully------------");

		// Validation of CPT Codes
		List<WebElement> SelectedCPTCodes = driver
				.findElements(By.xpath("//p[text()='Selected CPT Codes']/parent::div//span"));
		int checkboxTypesIndex = 0;
		System.out.println("------------Selected CPT Codes: ------------");
		for (WebElement strSelectedCPTCheckBox : SelectedCPTCodes) {
			Assert.assertEquals(strSelectedCPTCheckBox.getText().toLowerCase(),
					ExpectedCPTCodes.get(checkboxTypesIndex).replace("-", "").toLowerCase());
			System.out.println("Selected CPT Code is: " + strSelectedCPTCheckBox.getText());
			checkboxTypesIndex++;
		}

		// Validate Total Recurring Reimbursement for all Patients Per Month:
		WebElement RecurringReimbursementHeader = driver.findElement(By.xpath(
				"//header[contains(@class,'MuiAppBar')]//p[text()='Total Recurring Reimbursement for all Patients Per Month:']"));
		Assert.assertEquals(RecurringReimbursementHeader.getText().split(":")[0], "Total Recurring Reimbursement for all Patients Per Month");
		System.out.println("------------Recurring Reimbursement Header: " + RecurringReimbursementHeader.getText().split(":")[0] + "------------");
		WebElement RecurringReimbursementPrice = driver.findElement(By.xpath(
				"//header[contains(@class,'MuiAppBar')]//p[text()='Total Recurring Reimbursement for all Patients Per Month:']/p"));
		Assert.assertEquals(RecurringReimbursementPrice.getText(), "$75600");
		System.out.println("------------Recurring Reimbursement Price = " + RecurringReimbursementPrice.getText() + "------------");
		
		//Quitting the browser
		driver.quit();
	}

}
