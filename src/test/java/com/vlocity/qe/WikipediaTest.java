package com.vlocity.qe;

import static org.hamcrest.CoreMatchers.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class verifies elements on the wikipedia homepage.
 */
public class WikipediaTest {

    private Logger log = LoggerFactory.getLogger(WikipediaTest.class);

    private WebDriver driver;
    private ElementFinder finder;

    @BeforeClass
    public void setup() {

        /*
            If the following driver version doesn't work with your Chrome version
            see https://sites.google.com/a/chromium.org/chromedriver/downloads
            and update it as needed.
        */

        WebDriverManager.chromedriver().version("74.0.3729.6").setup();
        driver = new ChromeDriver();
        finder = new ElementFinder(driver);
        driver.get("https://www.wikipedia.org/");
    }

    @Test
    public void sloganPresent() {

        String sloganClass = "localized-slogan";
        WebElement slogan = finder.findElement(By.className(sloganClass));

        Assert.assertNotNull(slogan, String.format("Unable to find slogan div by class: %s", sloganClass));

        log.info("Slogan text is {}", slogan.getText());

        Assert.assertEquals(slogan.getText(), "The Free Encyclopedia");
    }
   
    //Verify the languages present
    @Test
    public void verifyFeaturedLanguages() {
        
    	WebElement linkGroup = driver.findElement(By.className("central-featured"));
        //Store all the "a" tagname WebElements to links variable.
        List<WebElement> links = linkGroup.findElements(By.tagName("a"));
        
        //Converting the list to String
        String []linkText =new String[links.size()];       
        int i=0;
        
        //Iterate over all the "links" WebElements using java for-each loop
        for (WebElement link : links) {
        	linkText[i]=link.getText();
        	
   //     	Verify featured languages
        	assertThat(linkText[i], either(containsString("English")).or(containsString("Español")).or(containsString("Deutsch")).or(containsString("Français")).or(containsString("Italiano")).or(containsString("Português")).or(containsString("Polski")));
        	//print the text of each language variable using Selenium's getText() method.
          	System.out.println(linkText[i]);
        	
        	i++;
        }
        
    }
    
    //Verify the hyperlinks for the Featured Languages work, i.e., they return a HTTP 200 status
    @Test
    public void verifyHTTPResponse() {
        
    	WebElement linkGroup = driver.findElement(By.className("central-featured"));
        //Store all the "a" tagname WebElements to links variable.
        List<WebElement> links = linkGroup.findElements(By.tagName("a"));
        
        String []linkText =new String[links.size()];
        Iterator<WebElement> it = links.iterator();
        
        int i=0;
        
        String url = "";
        HttpURLConnection huc = null;
        int respCode = 200;
        
        //Iterate over all the "links" WebElements using java for-each loop
        while(it.hasNext()){
            
        url = it.next().getAttribute("href");
        
        try {
        huc = (HttpURLConnection)(new URL(url).openConnection());
        
        huc.setRequestMethod("HEAD");
        
        huc.connect();
        
        respCode = huc.getResponseCode();
        
         //Verify HTTP Response code is 200
        if(respCode == 200)
            System.out.println(url+" is working");
        }  
        
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        }
    }

    @AfterClass
    public void closeBrowser() {

        if(driver!=null) {
            driver.close();
        }
    }
}
