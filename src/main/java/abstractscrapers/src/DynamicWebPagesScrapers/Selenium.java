/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package abstractscrapers.src.DynamicWebPagesScrapers;

import java.io.File;
import org.openqa.selenium.WebDriver;
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
          dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjs.getAbsolutePath());
          return new PhantomJSDriver(dcaps);
      }
}
