package easIE.src.Wrappers;

import easIE.src.Field;
import easIE.src.FieldType;
import easIE.src.SelectorType;
import easIE.src.Wrappers.AbstractWrapper;
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
 * DynamicHTMLWrapper object extends AbstractWrapper and is responsible for extracting 
 * content from defined fields (by css selectors) from a dynamic webpage or fields from a table.
 * @author vasgat
 */
public class DynamicHTMLWrapper extends AbstractWrapper {
   public String baseURL;
   public String source;
   private WebDriver driver;
   
   /**
    * Creates a new DynamicHTMLWrapper for a webpage
    * @param baseURL: webpage base url
    * @param relativeURL: path to the specific spot in the page
    * @throws URISyntaxException
    * @throws IOException
    * @throws InterruptedException 
    */
   public DynamicHTMLWrapper(String baseURL, String relativeURL) throws URISyntaxException, IOException, InterruptedException{
      this.baseURL = baseURL;
      this.source = baseURL+relativeURL;
      this.driver = Selenium.setUpChromeDriver();//Selenium.setUpPhantomJSDriver("C:\\Program Files (x86)\\phantomjs-2.0.0-windows\\bin\\phantomjs.exe");
      this.driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
      this.driver.get(
              baseURL+relativeURL
      );
      Thread.sleep(20000);
   }    
    
   /**
    * Creates a new DynamicHTMLWrapper for a webpage
    * @param FullLink: webpage's full url
    * @throws URISyntaxException
    * @throws IOException
    * @throws InterruptedException 
    */
   public DynamicHTMLWrapper(String FullLink) throws URISyntaxException, IOException, InterruptedException{
      this.source = FullLink; 
      this.driver = Selenium.setUpChromeDriver();//.setUpFireFoxDriver();//.setUpPhantomJSDriver("C:\\Program Files (x86)\\phantomjs-1.9.8-windows\\phantomjs.exe");
      this.driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
      this.driver.get(FullLink); 
      Thread.sleep(20000);
   }
   
   /**
    * Executes Click event to the specified HTML element
    * @param CSSSelector: points to the HTML element
    * @throws InterruptedException 
    */
   public void clickEvent(String CSSSelector) throws InterruptedException{
      driver.findElement(By.cssSelector(CSSSelector)).click();
      Thread.sleep(5000);
   }
   
   /**
    * Scrolls down to the end of the page.
    * @throws InterruptedException 
    */
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
   
   /**
    * Scrolls down a specific amount of times
    * @param timesToRepeat
    * @throws InterruptedException 
    */
   public void scrollDownEvent(int timesToRepeat) throws InterruptedException{ 
      String currentDoc = "";      
      for (int i=0; i< timesToRepeat; i++){
         currentDoc = driver.getPageSource();
         JavascriptExecutor jse = (JavascriptExecutor)driver;
         jse.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
         Thread.sleep(4000);
      }
   }   
   
   /**
    * extracts data from a list of specified fields from a webpage
    * @param fields: list of fields
    * @return a HashMap of the extracted data fields
    */
   @Override
   public HashMap extractFields(List<Field> fields){

      HashMap<String, Object> ExtractedFields = new HashMap<String, Object>();
      System.out.println(fields);
      for (int i=0; i<fields.size(); i++){
         Pair<String, Object> pair = getSelectedElement(fields.get(i), (WebElement) driver);
         ExtractedFields.put(pair.getKey(), pair.getValue());
      }
      ExtractedFields.put("source", source);
      if (!ExtractedFields.containsKey("citeyear")){
         ExtractedFields.put(
                 "citeyear",
                 Calendar.getInstance().get(Calendar.YEAR)
         );
      }
      return ExtractedFields;      
   }
   
   /**
    * extracts data from the specified table fields
    * @param tableSelector: CSS table selector
    * @param fields: list of table fields
    * @return an ArrayList of HashMap (corresponds to the extracted table fields)
    */
   @Override
   public ArrayList<HashMap<String, Object>> extractTable(String tableSelector, List<Field> fields){
      ArrayList<HashMap<String, Object>> extractedTableFields = new ArrayList();
      List<WebElement> table = driver.findElements(By.cssSelector(tableSelector));      
      for (int i=0; i<table.size(); i++){
         extractedTableFields.add(extractTableFields(fields, table.get(i)));
      }
      return extractedTableFields;
   }
   
   /**
    * private function which extracts columns of a table row
    * @param fields/columns of the table
    * @param element row of the table
    * @return a HashMap of the extracted fields
    */
   private HashMap<String, Object> extractTableFields(List<Field> fields, WebElement element){
      HashMap<String, Object> ExtractedFields = new HashMap<String, Object>();
      for (int i=0; i<fields.size(); i++){
         Pair<String, Object> pair = getSelectedElement(fields.get(i), element);
         ExtractedFields.put(pair.getKey(), pair.getValue());
      }
      ExtractedFields.put("source", source);
      if (!ExtractedFields.containsKey("citeyear")){
         ExtractedFields.put(
                 "citeyear",
                 Calendar.getInstance().get(Calendar.YEAR)
         );
      }
      return ExtractedFields;
   }   
   
   /**
    * extracts a list of values from a specific field
    * @param listSelecto CSS selector of list field
    * @return an ArrayList of values
    */
   public ArrayList extractList(String listSelector){
      ArrayList list = new ArrayList();
      List<WebElement> elements = driver.findElements(By.cssSelector(listSelector));
      for (int i=0; i<elements.size(); i++){
         list.add(elements.get(i).getText());
      }
      return list;
   }   
   
   /**
    * Closes browser driver
    */
   public void quit(){
      driver.quit();
   }
   
   /**
    * Returns the content of a specific field in the document
    * @param field 
    * @param element
    * @return a Pair of String, Object that corresponds to field name and field value accordingly
    */
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
            tempValue = extractList(field.FieldValue);
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
