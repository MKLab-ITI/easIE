package abstractscrapers.src.DynamicWebPagesScrapers;

import abstractscrapers.src.Field;
import abstractscrapers.src.FieldType;
import abstractscrapers.src.SelectorType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author vasgat
 */
public class Scraper {
   public String baseURL;
   public String source;
   private WebDriver driver;
   
   public Scraper(String baseURL, String relativeURL) throws URISyntaxException, IOException, InterruptedException{
      this.baseURL = baseURL;
      this.source = baseURL+relativeURL;
      this.driver = Selenium.setUpPhantomJSDriver("C:\\Program Files (x86)\\phantomjs-2.0.0-windows\\bin\\phantomjs.exe");
      this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
      this.driver.get(
              baseURL+relativeURL
      );
      Thread.sleep(15000);
   }    
    
   public Scraper(String FullLink) throws URISyntaxException, IOException, InterruptedException{
      this.source = FullLink; 
      this.driver = Selenium.setUpPhantomJSDriver("C:\\Program Files (x86)\\phantomjs-2.0.0-windows\\bin\\phantomjs.exe");
      this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
      this.driver.get(FullLink); 
      Thread.sleep(15000);
   }
   
   public void clickEvent(String CSSSelector) throws InterruptedException{
      driver.findElement(By.cssSelector(CSSSelector)).click();
      Thread.sleep(2000);
   }
   
   public void scrollDownEvent() throws InterruptedException{ 
   JavascriptExecutor jse = (JavascriptExecutor)driver;
   jse.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
      Thread.sleep(4000);
   }
   
   public HashMap scrapeFields(List<Field> fields){
      HashMap<String, Object> ScrapedFields = new HashMap<String, Object>();
      for (int i=0; i<fields.size(); i++){
         String tempName;
         Object tempValue;
         if (fields.get(i).SelectorNameType.equals(SelectorType.rawtext)){
            tempName = fields.get(i).FieldName;
         }
         else{
            if (fields.get(i).FieldNameType.equals(FieldType.text)){
               tempName = driver.findElement(
                       By.cssSelector(fields.get(i).FieldName)
               ).getText();
            }
            else if (fields.get(i).FieldNameType.equals(FieldType.link)){
               tempName = driver.findElement(
                       By.cssSelector(fields.get(i).FieldName)
               ).getAttribute("href");
            }
            else if ((fields.get(i).FieldNameType.equals(FieldType.image))){
               tempName = driver.findElement(
                       By.cssSelector(fields.get(i).FieldName)
               ).getAttribute("src");
            }
            else{
               tempName = driver.findElement(
                       By.cssSelector(fields.get(i).FieldName)
               ).getAttribute(fields.get(i).FieldValueType);
            }
         }
         if (fields.get(i).SelectorValueType.equals(SelectorType.rawtext)){
            tempValue = fields.get(i).FieldValue;
         }
         else{
            if (fields.get(i).FieldValueType.equals(FieldType.text)){
               tempValue = driver.findElement(
                       By.cssSelector(fields.get(i).FieldValue)
               ).getText();
            }
            else if (fields.get(i).FieldValueType.equals(FieldType.link)){
               tempValue = driver.findElement(
                       By.cssSelector(fields.get(i).FieldValue)
               ).getAttribute("href");
            }
            else if ((fields.get(i).FieldValueType.equals(FieldType.image))){
               tempValue = driver.findElement(
                       By.cssSelector(fields.get(i).FieldValue)
               ).getAttribute("src");
            }
            else if (fields.get(i).FieldValueType.equals(FieldType.list)){
               tempValue = scrapeList(fields.get(i).FieldValue);
            }
            else{
               tempValue = driver.findElement(
                       By.cssSelector(fields.get(i).FieldValue)
               ).getAttribute(fields.get(i).FieldValueType);
            }
         }
         ScrapedFields.put(tempName, tempValue);
      }
      ScrapedFields.put("URL", source);
      return ScrapedFields;      
   }
   
   public ArrayList<HashMap<String, Object>> scrapeTable(String tableSelector, List<Field> fields){
      ArrayList<HashMap<String, Object>> scrapedTableFields = new ArrayList();
      List<WebElement> table = driver.findElements(By.cssSelector(tableSelector));      
      for (int i=0; i<table.size(); i++){
         scrapedTableFields.add(scrapeTableFields(fields, table.get(i)));
      }
      return scrapedTableFields;
   }
   
   private HashMap<String, Object> scrapeTableFields(List<Field> fields, WebElement element){
      HashMap<String, Object> ScrapedFields = new HashMap<String, Object>();
      for (int i=0; i<fields.size(); i++){
         String tempName;
         Object tempValue;
         if (fields.get(i).SelectorNameType.equals(SelectorType.rawtext)){
            tempName = fields.get(i).FieldName;
         }
         else{
            if (fields.get(i).FieldNameType.equals(FieldType.text)){
               tempName = driver.findElement(
                       By.cssSelector(fields.get(i).FieldName)
               ).getText();
            }
            else if (fields.get(i).FieldNameType.equals(FieldType.link)){
               tempName = driver.findElement(
                       By.cssSelector(fields.get(i).FieldName)
               ).getAttribute("href");
            }
            else if ((fields.get(i).FieldNameType.equals(FieldType.image))){
               tempName = driver.findElement(
                       By.cssSelector(fields.get(i).FieldName)
               ).getAttribute("src");
            }
            else{
               tempName = driver.findElement(
                       By.cssSelector(fields.get(i).FieldName)
               ).getAttribute(fields.get(i).FieldValueType);
            }
         }
         if (fields.get(i).SelectorValueType.equals(SelectorType.rawtext)){
            tempValue = fields.get(i).FieldValue;
         }
         else{
            if (fields.get(i).FieldValueType.equals(FieldType.text)){
               tempValue = element.findElement(
                       By.cssSelector(fields.get(i).FieldValue)
               ).getText();
            }
            else if (fields.get(i).FieldValueType.equals(FieldType.link)){
               tempValue = element.findElement(
                       By.cssSelector(fields.get(i).FieldValue)
               ).getAttribute("href");
            }
            else if ((fields.get(i).FieldValueType.equals(FieldType.image))){
               tempValue = element.findElement(
                       By.cssSelector(fields.get(i).FieldValue)
               ).getAttribute("src");
            }
            else if (fields.get(i).FieldValueType.equals(FieldType.list)){
               tempValue = scrapeList(fields.get(i).FieldValue);
            }
            else{
               tempValue = element.findElement(
                       By.cssSelector(fields.get(i).FieldValue)
               ).getAttribute(fields.get(i).FieldValueType);
            }
         }
         ScrapedFields.put(tempName, tempValue);
      }
      ScrapedFields.put("URL", source);
      return ScrapedFields;
   }   
   
   public ArrayList scrapeList(String listSelector){
      ArrayList list = new ArrayList();
      List<WebElement> elements = driver.findElements(By.cssSelector(listSelector));
      for (int i=0; i<elements.size(); i++){
         list.add(elements.get(i).getText());
      }
      return list;
   }   
   
   public void quit(){
      driver.quit();
   }
}
