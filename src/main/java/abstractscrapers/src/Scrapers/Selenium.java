package abstractscrapers.src.Scrapers;

import java.io.File;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author vasgat
 */
public class Selenium {
      
   /**
    * Setting up PhantoJSDriver 
    * @return PhantomJSDriver
    */
    public static WebDriver setUpPhantomJSDriver(String driverPath){
          File phantomjs = new File(driverPath);
          DesiredCapabilities dcaps = new DesiredCapabilities();
          dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] { "--ignore-ssl-errors=yes","--ssl-protocol=tlsv1", "version=2.0", "driverVersion=1.2.0" });
          dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjs.getAbsolutePath());
          
          return new PhantomJSDriver(dcaps);
      }
    
    public static WebDriver setUpFireFoxDriver(){
       return new FirefoxDriver();
    }
    
    public static WebDriver setUpChromeDriver() {
      File file = new File("C:\\Users\\vasgat\\Desktop\\chromedriver.exe");
      System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
       return new ChromeDriver();
    }    
}
