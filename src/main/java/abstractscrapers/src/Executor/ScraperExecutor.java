package abstractscrapers.src.Executor;

import abstractscrapers.src.Configure.Configuration;
import abstractscrapers.src.Scrapers.AbstractScraper;
import abstractscrapers.src.Scrapers.BunchScraper;
import abstractscrapers.src.Scrapers.PaginationIterator;
import abstractscrapers.src.Scrapers.StaticHTMLScraper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author vasgat
 */
public class ScraperExecutor {
   private Configuration config;
   private ArrayList<HashMap> snippetFields;
   private ArrayList<HashMap> companyFields;
   
   public ScraperExecutor(Configuration config) throws URISyntaxException, IOException, Exception{
      this.config = config;
      exec();
   }
   
   public void StoreScrapedInfo() throws Exception{
      SnippetHandler handler = new SnippetHandler(companyFields, snippetFields);
      handler.store(config.store, config.source_name);
   }
   
   private void exec() throws URISyntaxException, IOException, Exception{
      if(config.dynamicHTML){
         
      }
      else{
         if(config.url.baseURL==null&&config.url.relativeURL==null&&config.url.fullURL!=null){            
            if(config.bunch_urls!=null)
               throw new Exception("Scraping a banch of urls you need to define in the Configuration \"baseURL\" field.");
            if (config.nextPageSelector!=null)
               throw new Exception("For scraping a set of similar Pages (next Pagination) corresponding to the same site you need to define \"baseURL\" field");
            StaticHTMLScraper scraper = new StaticHTMLScraper(config.url.fullURL);
            if (config.table_selector!=null)
               execScrapeTable(scraper);
            else
               execScrapeFields(scraper);
            
         }
         else if(config.url.baseURL!=null&&config.url.relativeURL!=null){        
            StaticHTMLScraper scraper = new StaticHTMLScraper(config.url.baseURL,config.url.relativeURL);
            if (config.nextPageSelector!=null){
               PaginationIterator pagination = new PaginationIterator(scraper,config.nextPageSelector);
               if (config.table_selector!=null)
                  execScrapeTable(pagination);
               else
                  execScrapeFields(pagination);
            }
            else{               
               if (config.table_selector!=null)
                  execScrapeTable(scraper);
               else
                  execScrapeFields(scraper);
            }
         }
         else if(config.bunch_urls!=null&&config.url.baseURL==null&&config.url.relativeURL==null&&config.url.fullURL==null){
            if(config.nextPageSelector!=null)
               throw new Exception("Pagination can not be conducted in Bunch Scraper mode");
            BunchScraper scraper = new BunchScraper(config.bunch_urls, config.url.baseURL);
            if (config.table_selector!=null)
               if(config.company_fields!=null)
                  execScrapeTable(scraper);
               else
                  execScrapeFields(scraper);
         }
         else{
            throw new Exception("Configuration error: In defining url field you need to determine either (\"baseURL\" and \"relativeURL\") or (\"fullURL\") or (\"baseURL\" alongside with \"banch_urls\" field)");
         }
      }
   }
   
   private void execScrapeTable(AbstractScraper scraper) throws Exception{
      if(config.company_fields!=null){
         companyFields = 
                  (ArrayList<HashMap>) scraper.scrapeTable(
                          config.table_selector, 
                          config.company_fields
                  );
         if(config.snippet_fields!=null){
            snippetFields = 
                    (ArrayList<HashMap>) scraper.scrapeTable(
                            config.table_selector, 
                            config.snippet_fields
                    );
         }
   }
      else if(config.snippet_fields!=null){
         snippetFields = 
                 (ArrayList<HashMap>) scraper.scrapeTable(
                         config.table_selector, 
                         config.snippet_fields
                 );
      }
      else
         throw new Exception("In the Configuration you need to define either company_fields or snippet_fields to scrape (or both)");
   }
   
   private void execScrapeFields(AbstractScraper scraper) throws Exception{
      if(config.company_fields!=null){
         companyFields = 
                  (ArrayList<HashMap>) scraper.scrapeFields(
                          config.company_fields
                  );
         if(config.snippet_fields!=null){
            snippetFields = 
                    (ArrayList<HashMap>) scraper.scrapeFields(
                            config.snippet_fields
                    );
         }
   }
      else if(config.snippet_fields!=null){
         snippetFields = 
                 (ArrayList<HashMap>) scraper.scrapeFields(
                         config.snippet_fields
                 );
      }
      else
         throw new Exception("In the Configuration you need to define either company_fields or snippet_fields to scrape (or both)");  
   }
}
