/*
 * Copyright 2016 vasgat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package certh.iti.mklab.easie.extractors.dynamicpages;

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
    
    public static WebDriver setUpChromeDriver(String ChromeDriverPath) {
      File file = new File("chromedriver.exe");
      System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());
       return new ChromeDriver();
    }    
}
