package abstractscrapers.src.Scrapers;

import abstractscrapers.src.Field;
import abstractscrapers.src.FieldType;
import abstractscrapers.src.SelectorType;
import abstractscrapers.src.Scrapers.AbstractScraper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.util.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author vasgat
 */
public class DynamicHTMLScraper extends AbstractScraper {
   public String baseURL;
   public String source;
   private WebDriver driver;
   
   public DynamicHTMLScraper(String baseURL, String relativeURL) throws URISyntaxException, IOException, InterruptedException{
      this.baseURL = baseURL;
      this.source = baseURL+relativeURL;
      this.driver = Selenium.setUpChromeDriver();//Selenium.setUpPhantomJSDriver("C:\\Program Files (x86)\\phantomjs-2.0.0-windows\\bin\\phantomjs.exe");
      this.driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
      this.driver.get(
              baseURL+relativeURL
      );
      Thread.sleep(20000);
   }    
    
   public DynamicHTMLScraper(String FullLink) throws URISyntaxException, IOException, InterruptedException{
      this.source = FullLink; 
      this.driver = Selenium.setUpChromeDriver();//.setUpFireFoxDriver();//.setUpPhantomJSDriver("C:\\Program Files (x86)\\phantomjs-1.9.8-windows\\phantomjs.exe");
      this.driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
      this.driver.get(FullLink); 
      Thread.sleep(20000);
   }
   
   public void clickEvent(String CSSSelector) throws InterruptedException{
      driver.findElement(By.cssSelector(CSSSelector)).click();
      Thread.sleep(5000);
   }
   
   public void scrollDownEvent() throws InterruptedException{ 
      String currentDoc = "";      
      do{
         currentDoc = driver.getPageSource();
         JavascriptExecutor jse = (JavascriptExecutor)driver;
         jse.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
         Thread.sleep(4000);
      }while(!currentDoc.equals(driver.getPageSource()));
      System.out.println("We are in the end of the page");
   }
   
   public void scrollDownEvent(int timesToRepeat) throws InterruptedException{ 
      String currentDoc = "";      
      for (int i=0; i< timesToRepeat; i++){
         currentDoc = driver.getPageSource();
         JavascriptExecutor jse = (JavascriptExecutor)driver;
         jse.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
         Thread.sleep(4000);
      }
   }   
   
   @Override
   public HashMap scrapeFields(List<Field> fields){

      HashMap<String, Object> ScrapedFields = new HashMap<String, Object>();
      System.out.println(fields);
      for (int i=0; i<fields.size(); i++){
         Pair<String, Object> pair = getSelectedElement(fields.get(i), (WebElement) driver);
         ScrapedFields.put(pair.getKey(), pair.getValue());
      }
      ScrapedFields.put("source", source);
      if (!ScrapedFields.containsKey("citeyear")){
         ScrapedFields.put(
                 "citeyear",
                 Calendar.getInstance().get(Calendar.YEAR)
         );
      }
      return ScrapedFields;      
   }
   
   @Override
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
         Pair<String, Object> pair = getSelectedElement(fields.get(i), element);
         ScrapedFields.put(pair.getKey(), pair.getValue());
      }
      ScrapedFields.put("source", source);
      if (!ScrapedFields.containsKey("citeyear")){
         ScrapedFields.put(
                 "citeyear",
                 Calendar.getInstance().get(Calendar.YEAR)
         );
      }
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
   
   private Pair<String, Object> getSelectedElement(Field field, WebElement element){
      String tempName;
      Object tempValue;
      if (field.SelectorNameType.equals(SelectorType.rawtext)){
         tempName = field.FieldName;
      }
      else{
         if (field.FieldNameType.equals(FieldType.text)){
            tempName = element.findElement(
                    By.cssSelector(field.FieldName)
            ).getText();
         }
         else if (field.FieldNameType.equals(FieldType.link)){
            tempName = element.findElement(
                    By.cssSelector(field.FieldName)
            ).getAttribute("href");
         }
         else if ((field.FieldNameType.equals(FieldType.image))){
            tempName = element.findElement(
                    By.cssSelector(field.FieldName)
            ).getAttribute("src");
         }
         else{
            tempName = element.findElement(
                    By.cssSelector(field.FieldName)
            ).getAttribute(field.FieldValueType);
         }
      }
      if (field.SelectorValueType.equals(SelectorType.rawtext)){
         tempValue = field.FieldValue;
      }
      else{
         if (field.FieldValueType.equals(FieldType.text)){
            tempValue = element.findElement(
                    By.cssSelector(field.FieldValue)
            ).getText();
         }
         else if (field.FieldValueType.equals(FieldType.link)){
            tempValue = element.findElement(
                    By.cssSelector(field.FieldValue)
            ).getAttribute("href");
         }
         else if ((field.FieldValueType.equals(FieldType.image))){
            tempValue = element.findElement(
                    By.cssSelector(field.FieldValue)
            ).getAttribute("src");
         }
         else if (field.FieldValueType.equals(FieldType.list)){
            tempValue = scrapeList(field.FieldValue);
         }
         else{
            tempValue = element.findElement(
                    By.cssSelector(field.FieldValue)
            ).getAttribute(field.FieldValueType);
         }
      }
         return new Pair(tempName, tempValue);
   }   
}
