package abstractscrapers.src.Scrapers;

import abstractscrapers.src.Field;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jsoup.Jsoup;

/**
 * PaginationItearator object is responsible for scraping a bunch of similar pages
 * when the data are distributed in different pages
 * @author vasgat
 */
public class PaginationIterator extends AbstractScraper{
   private String nextPageSelector;
   private StaticHTMLScraper scraper;
   
   /**
    * Creates a new PaginationIterator
    * @param scraper Scraper object of an Instance page
    * @param nextPageSelector next Page CSS selector in the page
    * @throws URISyntaxException
    * @throws IOException 
    */
   public PaginationIterator(StaticHTMLScraper scraper, String nextPageSelector) throws URISyntaxException, IOException{
      this.nextPageSelector = nextPageSelector; 
      this.scraper = scraper;
   }
   
   /**
    * Scrapes the defined fields from each page until no next page exists
    * @param fields: List of fields we want to scrape
    * @return the scraped fields as a List of HashMaps
    * @throws URISyntaxException
    * @throws IOException
    * @throws Exception 
    */
   @Override
   public ArrayList<HashMap> scrapeFields(List<Field> fields) throws URISyntaxException, IOException, Exception{
      ArrayList<HashMap> scrapedFields = new ArrayList();
      scrapedFields.add(scraper.scrapeFields(fields));
      while(!scraper.document.select(nextPageSelector).attr("href").equals("")){         
         scraper.document = Jsoup.connect(new URI(scraper.baseURL+scraper.document.select(nextPageSelector).attr("href").replace(scraper.baseURL, "")).toASCIIString())
                              .userAgent("Mozilla/37.0").timeout(60000).get(); 
         scrapedFields.add(scraper.scrapeFields(fields));
      }
      return scrapedFields;
   }
   
   /**
    * Scrapes a table from each page until no next page exists
    * @param tableSelector CSS table selector 
    * @param fields List of table fields we want to scrape
    * @return the scraped table fields as a List of HashMaps
    * @throws URISyntaxException
    * @throws IOException
    * @throws Exception 
    */
   @Override
   public ArrayList<HashMap> scrapeTable(String tableSelector, List<Field> fields) throws URISyntaxException, IOException, Exception{
      ArrayList<HashMap> scrapedFields = new ArrayList();
      scrapedFields.addAll(scraper.scrapeTable(tableSelector, fields));
      while(!scraper.document.select(nextPageSelector).attr("href").equals("")){         
         scraper.document = Jsoup.connect(new URI(scraper.baseURL+scraper.document.select(nextPageSelector).attr("href").replace(scraper.baseURL, "")).toASCIIString())
                              .userAgent("Mozilla/37.0").timeout(60000).get();  
         scrapedFields.addAll(scraper.scrapeTable(tableSelector, fields));
      }  
      return scrapedFields;
   }
}
