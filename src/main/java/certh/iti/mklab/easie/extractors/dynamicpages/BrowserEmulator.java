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

import certh.iti.mklab.easie.extractors.Fetcher;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author vasgat
 */
public class BrowserEmulator extends Fetcher {

    public WebDriver driver;

    public BrowserEmulator(String baseURL, String relativeURL, String ChromeDriverPath) throws InterruptedException {
        driver = Selenium.setUpChromeDriver(ChromeDriverPath);
        driver.manage().window().setPosition(new Point(-2000, 0));
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.get(
                baseURL + relativeURL
        );
        Thread.sleep(10000);
    }

    public BrowserEmulator(String fullURL, String ChromeDriverPath) throws InterruptedException {        
        driver = Selenium.setUpChromeDriver(ChromeDriverPath);
        driver.manage().window().setPosition(new Point(-2000, 0));
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.get(
                fullURL
        );
        Thread.sleep(5000);
    }

    /**
     * Executes Click event to the specified HTML element
     *
     * @param CSSSelector: points to the HTML element
     * @throws InterruptedException
     */
    public void clickEvent(String CSSSelector) throws InterruptedException {
        driver.findElement(By.cssSelector(CSSSelector)).click();
        Thread.sleep(3000);
    }

    /**
     * Scrolls down to the end of the page.
     *
     * @throws InterruptedException
     */
    public void scrollDownEvent() throws InterruptedException {
        String currentDoc = "";
        do {
            currentDoc = driver.getPageSource();
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
            Thread.sleep(4000);
        } while (!currentDoc.equals(driver.getPageSource()));
    }

    /**
     * Scrolls down a specific amount of times
     *
     * @param timesToRepeat
     * @throws InterruptedException
     */
    public void scrollDownEvent(int timesToRepeat) throws InterruptedException {
        Random rand = new Random();
        String currentDoc = "";
        for (int i = 0; i < timesToRepeat; i++) {
            currentDoc = driver.getPageSource();
            int counter = 0;
            while (((Document) this.getHTMLDocument()).select("._hnn7m").size() > 0 && counter <= 600000) {
                Thread.sleep(2000);
                counter += 2000;
            }
            if (counter > 600000) {
                break;
            }
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("window.scrollBy(0," + (300 + rand.nextInt(312)) + ")", "");
            int sleep_time = rand.nextInt(3000);
            Thread.sleep(2142 + sleep_time);

            /*if (currentDoc.equals(driver.getPageSource())){
                jse.executeScript("scroll(0, -25);");
                Thread.sleep(1968+rand.nextInt(12365));
                jse.executeScript("scroll(0, 250);");
            }*/
        }
    }

    /**
     * Closes browser driver
     */
    public void close() {
        driver.close();
        driver.quit();
    }

    @Override
    public Document getHTMLDocument() {
        Document doc = Jsoup.parse(driver.getPageSource());
        return doc;
    }

}
