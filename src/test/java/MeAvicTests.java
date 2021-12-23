import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class MeAvicTests {

    private WebDriver driver;

    @BeforeTest
    public void testSetup() {
        System.setProperty("webdriver.chrome.driver", "");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void methodSetup() {
        driver.get("https://avic.ua/ua");
    }

    //in first 5 cases I created separate WebElement instances for much convenient reading
    //in last 6 test I used regular syntax due to Exception - stale element reference: element is not attached to the page document
    @Test
    public void tradeInNavigationValidation(){
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(15));

        WebElement headerTradeInLink = driver.findElement(By.xpath("(//a[text()='Trade-in оцінка'])[2]"));

        headerTradeInLink.click();
        wait.until(ExpectedConditions.urlToBe("https://avic.ua/ua/tradein"));
        //tradeInPage elements
        WebElement titleText = driver.findElement(By.xpath("//h1[@class='page-title'][text()='Trade in від Avic']"));//Trade in від Avic
        WebElement tradeInContainer = driver.findElement(By.xpath("//div[@class='tradein-container']"));
        WebElement logoImage = driver.findElement(By.xpath("//div[@class='header-bottom__logo']//img"));

        //tradeInPageValidation
        wait.until(ExpectedConditions.visibilityOf(titleText));
        Assert.assertEquals(driver.getCurrentUrl(),"https://avic.ua/ua/tradein");
        Assert.assertTrue(tradeInContainer.isDisplayed());
        //goingBack
        logoImage.click();
        wait.until(ExpectedConditions.urlToBe("https://avic.ua/ua"));
        //validation
        Assert.assertEquals(driver.getCurrentUrl(),"https://avic.ua/ua");
    }

    @Test
    public void directorLetterPopUpValidation() {
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(15));
        //elements on page
        WebElement DirectorMailLink = driver.findElement(By.xpath("//a[@class='header-top__item or-color js_addMessage_btn']"));

        //scenario
        DirectorMailLink.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='js_addMessage']")));

        //elements on page
        WebElement sendMessagePopUP = driver.findElement(By.xpath("//div[@id='js_addMessage']"));
        WebElement popUpText = driver.findElement(By.xpath("//div[text()='Надіслати повідомлення']"));
        WebElement nameInput = driver.findElement(By.xpath("(//input[@name='user_name'])[2]"));
        WebElement emailInput = driver.findElement(By.xpath("(//input[@placeholder='Електронна пошта'])[2]"));
        WebElement confirmButton = driver.findElement(By.xpath("(//button[contains(text(),'Надіслати повідомлення')])[4]"));
        WebElement crossWindow = driver.findElement(By.xpath("//button[@class='fancybox-button fancybox-close-small']"));

        //validation
        Assert.assertTrue(popUpText.isDisplayed());
        Assert.assertTrue(nameInput.isDisplayed());
        Assert.assertTrue(emailInput.isDisplayed());
        Assert.assertTrue(confirmButton.isDisplayed());
        Assert.assertTrue(crossWindow.isDisplayed());

        //scenario
        crossWindow.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='js_addMessage']")));
        Assert.assertFalse(sendMessagePopUP.isDisplayed());
    }


    @Test
    public void searchResultsQuantityValidation(){
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(15));

        //elements on page
        WebElement inputField = driver.findElement(By.xpath("//input[@id='input_search']"));
        //scenario
        inputField.sendKeys("Фотокамера");
        inputField.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[@class='item-prod col-lg-3']")));
        //validation
        List<WebElement> elementsList = driver.findElements(By.xpath("//div[@class='item-prod col-lg-3']"));
        Assert.assertEquals(elementsList.size(),12);
    }


        @Test
    public void searchingNegativeCaseValidation(){
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(15));
        //elements on page
        WebElement inputField = driver.findElement(By.xpath("//input[@id='input_search']"));
        //scenario
        inputField.sendKeys("T");
        inputField.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='modalAlert']")));
        //validation
        Assert.assertTrue(driver.findElement(By.xpath("//div[@id='modalAlert']")).isDisplayed());
        //elements on page
        WebElement closeCross = driver.findElement(By.xpath("//button[@class='fancybox-button fancybox-close-small']"));
        //validation
        Assert.assertTrue(closeCross.isEnabled());
        //scenario
        closeCross.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='modalAlert']")));
        //validation
        Assert.assertFalse(driver.findElement(By.xpath("//div[@id='modalAlert']")).isDisplayed());
    }

    @Test
    public void descriptionTextSearchResults(){
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(15));
        //element
        WebElement inputField = driver.findElement(By.xpath("//input[@id='input_search']"));
        //scenario
        inputField.sendKeys("Фотокамера");
        inputField.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[text()='Результати пошуку']")));

        List<WebElement> elementList = driver.findElements(By.xpath("//div[@class='prod-cart__descr']"));
        //validation
        for(WebElement a : elementList){
            Assert.assertTrue(a.getText().contains("Фотокамера"));
        }
    }

    @Test
    public void addingToCartValidations()  {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        //adding one product and verify value is 1
        driver.findElement(By.xpath("//input[@id='input_search']")).sendKeys("Фото");
        driver.findElement(By.xpath("//input[@id='input_search']")).sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[text()='Результати пошуку']")));

        Assert.assertEquals(driver.findElement(By.xpath("(//div[@class='active-cart-item js_cart_count'])[2]")).getText(), "0");
        driver.findElement(By.xpath("//div[@class='item-prod col-lg-3'][1]//a[@class='prod-cart__buy']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='js_cart']")));

        driver.findElement(By.xpath("//button[@class='fancybox-button fancybox-close-small']")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='js_cart']")));
        Assert.assertEquals(driver.findElement(By.xpath("(//div[@class='active-cart-item js_cart_count'])[2]")).getText(), "1");

        //add one more product and verify value is 2
        driver.findElement(By.xpath("(//div[@class='active-cart-item js_cart_count'])[2]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='js_cart']")));
        driver.findElement(By.xpath("//span[@class='js_plus btn-count btn-count--plus ']")).click();
        driver.findElement(By.xpath("//a[@class='main-btn main-btn--orange']")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='js_cart']")));

        wait.until(ExpectedConditions.textToBePresentInElement(driver.findElement(By.xpath("(//div[@class='active-cart-item js_cart_count'])[2]")), "2"));
        Assert.assertEquals(driver.findElement(By.xpath("(//div[@class='active-cart-item js_cart_count'])[2]")).getText(), "2");

        //delete one product from cart and verify value is 1
        driver.findElement(By.xpath("(//div[@class='active-cart-item js_cart_count'])[2]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='js_cart']")));

        WebElement reduceQuantity = driver.findElement(By.xpath("//span[@class='js_minus btn-count btn-count--minus ']"));

        wait.until(ExpectedConditions.elementToBeClickable(reduceQuantity));
        driver.findElement(By.xpath("//span[@class='js_minus btn-count btn-count--minus ']")).click();
        driver.findElement(By.xpath("//a[@class='main-btn main-btn--orange']")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='js_cart']")));
        wait.until(ExpectedConditions.textToBePresentInElement(driver.findElement(By.xpath("(//div[@class='active-cart-item js_cart_count'])[2]")), "1"));
        Assert.assertEquals(driver.findElement(By.xpath("(//div[@class='active-cart-item js_cart_count'])[2]")).getText(), "1");

        //delete all from cart and verify value is 0
        driver.findElement(By.xpath("(//div[@class='active-cart-item js_cart_count'])[2]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='js_cart']")));
        driver.findElement(By.xpath("(//i[@class='icon icon-close js-btn-close'])[2]")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@id='js_cart']")));
        Assert.assertEquals(driver.findElement(By.xpath("(//div[@class='active-cart-item js_cart_count'])[2]")).getText(), "0");
    }

    @AfterTest
    public void tearDown() {
        driver.quit();
    }

}

