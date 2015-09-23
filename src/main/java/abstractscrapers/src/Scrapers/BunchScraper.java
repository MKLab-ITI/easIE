package abstractscrapers.src.Scrapers;

import abstractscrapers.src.Field;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * BunchScraper object extends AbstractScraper and is responsible for scraping a set of links - webpages 
 * with the same structure
 * @author vasgat
 */
public class BunchScraper extends AbstractScraper{
   private HashSet<String> BunchOfLinks;
   private String baseURL;
   
   /**
    * Creates a new BunchScraper
    * @param BunchOfLinks a set of webpages
    */
   public BunchScraper(HashSet<String> BunchOfLinks){
      this.baseURL = "";
      this.BunchOfLinks = BunchOfLinks;
   }

   public BunchScraper(HashSet<String> BunchOfLinks, String baseURL){
      this.baseURL = baseURL;
      this.BunchOfLinks = BunchOfLinks;
   }
   /**
    * Scrapes specified field for each webpage
    * @param fields set of fields
    * @return the scraped Fields
    * @throws URISyntaxException
    * @throws IOException
    * @throws Exception 
    */
   @Override
   public ArrayList<HashMap> scrapeFields(List<Field> fields) throws URISyntaxException, IOException, Exception{
      ArrayList<HashMap> scrapedFields = new ArrayList();
      System.out.println(BunchOfLinks);
      Iterator links = BunchOfLinks.iterator();
      int i=0;
      while(links.hasNext()){
         System.out.println(i++);
         StaticHTMLScraper scraper = new StaticHTMLScraper(baseURL+((String) links.next()).replace(baseURL, ""));
         scrapedFields.addAll(scraper.scrapeFields(fields));
      }
      return scrapedFields;
   }
   
   /**
    * Scrapes a table 
    * @param tableSelector
    * @param fields
    * @return the scraped table fields for each page
    * @throws URISyntaxException
    * @throws IOException
    * @throws Exception 
    */
   @Override
   public ArrayList<HashMap> scrapeTable(String tableSelector, List<Field> fields) throws URISyntaxException, IOException, Exception{
      ArrayList<HashMap> scrapedFields = new ArrayList();
      Iterator links = BunchOfLinks.iterator();
      int i=0;
      while(links.hasNext()){
         System.out.println(i++);
         StaticHTMLScraper scraper = new StaticHTMLScraper(baseURL+((String) links.next()).replace(baseURL, ""));
         scrapedFields.addAll(scraper.scrapeTable(tableSelector, fields));
      }
      return scrapedFields;
   }   
}
