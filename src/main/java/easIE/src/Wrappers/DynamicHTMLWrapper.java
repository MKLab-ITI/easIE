package easIE.src.Wrappers;

import easIE.src.Field;
import easIE.src.Wrappers.AbstractWrapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * DynamicHTMLWrapper object extends AbstractWrapper and is responsible for
 * extracting content from defined fields (by css selectors) from a dynamic
 * webpage or fields from a table.
 *
 * @author vasgat
 */
public class DynamicHTMLWrapper extends AbstractWrapper {
    public String baseURL;
    public String source;
    public WebDriver driver;
    private DynamicHTMLFetcher fetcher;
    
    
    /**
     * Creates a new DynamicHTMLWrapper for a webpage
     *
     * @param baseURL: webpage base url
     * @param relativeURL: path to the specific spot in the page
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public DynamicHTMLWrapper(String baseURL, String relativeURL) throws URISyntaxException, IOException, InterruptedException {
        this.baseURL = baseURL;
        this.source = baseURL + relativeURL;
        this.fetcher = new DynamicHTMLFetcher(baseURL + relativeURL);
        this.driver = (WebDriver) fetcher.driver;
    }

    /**
     * Creates a new DynamicHTMLWrapper for a webpage
     *
     * @param FullLink: webpage's full url
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public DynamicHTMLWrapper(String FullLink) throws URISyntaxException, IOException, InterruptedException {
        this.source = FullLink;
        this.fetcher = new DynamicHTMLFetcher(FullLink);
        this.driver = (WebDriver) fetcher.driver;
    }

    /**
     * Executes Click event to the specified HTML element
     *
     * @param CSSSelector: points to the HTML element
     * @throws InterruptedException
     */
    public void clickEvent(String CSSSelector) throws InterruptedException {
        driver.findElement(By.cssSelector(CSSSelector)).click();
        Thread.sleep(5000);
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
        System.out.println("We are in the end of the page");
    }

    /**
     * Scrolls down a specific amount of times
     *
     * @param timesToRepeat
     * @throws InterruptedException
     */
    public void scrollDownEvent(int timesToRepeat) throws InterruptedException {
        String currentDoc = "";
        for (int i = 0; i < timesToRepeat; i++) {
            currentDoc = driver.getPageSource();
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
            Thread.sleep(4000);
        }
    }

    /**
     * extracts data from a list of specified fields from a webpage
     *
     * @param fields: list of fields
     * @return a HashMap of the extracted data fields
     */
    @Override
    public ArrayList<HashMap> extractFields(List<Field> fields) {
        FieldExtractor extractor
                = new FieldExtractor((Element) fetcher.getHTMLDocument(), source);
        return extractor.run(fields);
    }

    /**
     * extracts data from the specified table fields
     *
     * @param tableSelector: CSS table selector
     * @param fields: list of table fields
     * @return an ArrayList of HashMap (corresponds to the extracted table
     * fields)
     */
    @Override
    public ArrayList<HashMap<String, Object>> extractTable(String tableSelector, List<Field> fields) {
        TableFieldExtractor extractor
                = new TableFieldExtractor((Element) fetcher.getHTMLDocument(),tableSelector, source);
        return extractor.run(fields);
    }

    /**
     * Closes browser driver
     */
    public void quit() {
        driver.quit();
    }

    @Override
    public Object extractFields(List<Field> cfields, List<Field> sfields) throws URISyntaxException, IOException, Exception {
        FieldExtractor extractor = new FieldExtractor((Element) fetcher.getHTMLDocument(), source);
        return new Pair(extractor.getExtractedFields(cfields), extractor.getExtractedFields(sfields));
    }

    @Override
    public Pair extractTable(String tableSelector, List<Field> cfields, List<Field> sfields) throws URISyntaxException, IOException, Exception {
        TableFieldExtractor extractor = new TableFieldExtractor((Element) fetcher.getHTMLDocument(),tableSelector, source);
        return new Pair(extractor.getExtractedFields(cfields), extractor.getExtractedFields(sfields));
    }
    
    public Document getHTMLDocument(){
        return (Document) fetcher.getHTMLDocument();
    }
}
