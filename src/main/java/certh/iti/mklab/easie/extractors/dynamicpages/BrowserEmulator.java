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
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author vasgat
 */
public class BrowserEmulator extends Fetcher {

    public WebDriver driver;

    public BrowserEmulator(String baseURL, String relativeURL, String pathToChromeDriver) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", pathToChromeDriver);
        driver = new ChromeDriver();
        driver.get(
                baseURL + relativeURL
        );
    }

    public BrowserEmulator(String fullURL, String pathToChromeDriver) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", pathToChromeDriver);
        driver = new ChromeDriver();
        driver.get(
                fullURL
        );
    }

    public void waitPageLoad() {
        new WebDriverWait(driver, 60).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Executes Click event to the specified HTML element
     *
     * @param CSSSelector: points to the HTML element
     * @throws InterruptedException
     */
    public void clickEvent(String CSSSelector) throws InterruptedException {
        WebElement element = driver.findElement(By.cssSelector(CSSSelector));
        element.click();
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

    public void scrollUpEvent(int timesToRepeat) throws InterruptedException {
        Random rand = new Random();
        String currentDoc = "";
        for (int i = 0; i < timesToRepeat; i++) {
            currentDoc = driver.getPageSource();
            int counter = 0;
            while (((Document) this.getHTMLDocument()).select("._hnn7m").size() > 0 && counter <= 600000) {
                Thread.sleep(2000);
                counter = 2000;
            }
            if (counter > 600000) {
                break;
            }
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("window.scrollBy(0," + (-300 - rand.nextInt(312)) + ")", "");
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
