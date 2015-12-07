package easIE.src.Wrappers;

import java.util.concurrent.TimeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author vasgat
 */
public class DynamicHTMLFetcher extends Fetcher {
    public WebDriver driver;

    public DynamicHTMLFetcher(String baseURL, String relativeURL) throws InterruptedException {
        driver = Selenium.setUpChromeDriver();
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.get(
                baseURL + relativeURL
        );
        Thread.sleep(20000);
    }

    public DynamicHTMLFetcher(String fullURL) throws InterruptedException {
        driver = Selenium.setUpChromeDriver();
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.get(
                fullURL
        );
        Thread.sleep(20000);
    }

    @Override
    public Object getHTMLDocument() {
        Document doc = Jsoup.parse(driver.getPageSource());
        return doc;
    }

}
