package abstractscrapers.src.Executors;

import abstractscrapers.src.Configure.Configuration;
import abstractscrapers.src.Configure.EventType;
import abstractscrapers.src.Configure.RepetitionType;
import abstractscrapers.src.Scrapers.AbstractScraper;
import abstractscrapers.src.Scrapers.BunchScraper;
import abstractscrapers.src.Scrapers.DynamicHTMLScraper;
import abstractscrapers.src.Scrapers.PaginationIterator;
import abstractscrapers.src.Scrapers.StaticHTMLScraper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ScraperExecutor scrapes contents as defined in Configuration object
 * @author vasgat
 */
public class ScraperExecutor {
   private Configuration config;
   private ArrayList<HashMap> snippetFields;
   private ArrayList<HashMap> companyFields;
   
   /**
    * Creates ScraperExecutor Object and executes the scraping task based on the Configuration
    * @param config 
    * @throws URISyntaxException
    * @throws IOException
    * @throws Exception 
    */
   public ScraperExecutor(Configuration config) throws URISyntaxException, IOException, Exception{
      this.config = config;
      this.companyFields = new ArrayList<HashMap>();
      this.snippetFields = new ArrayList<HashMap>();
      exec();
   }
   
   /**
    * Stores the scraped data in database or in drive based on the Configuration
    * @throws Exception 
    */
   public void StoreScrapedInfo() throws Exception{
      SnippetHandler handler = new SnippetHandler(companyFields, snippetFields);
      handler.store(config.store, config.source_name);
   }
   
   /**
    * Executes the scraping task
    * @throws URISyntaxException
    * @throws IOException
    * @throws Exception 
    */
   private void exec() throws URISyntaxException, IOException, Exception{
      if(config.dynamicHTML){
         DynamicHTMLScraper scraper = null;
         if (config.url.baseURL!=null && config.url.relativeURL!=null){
            scraper = new DynamicHTMLScraper(config.url.baseURL, config.url.relativeURL);
         }
         else if(config.url.fullURL!=null){
            scraper = new DynamicHTMLScraper(config.url.fullURL);
         }
        if (config.event.sequence_of_events!=null){
           for(int i=0; i<config.event.sequence_of_events.size(); i++){
              if(config.event.sequence_of_events.get(i).equals(EventType.CLICK)){
                 scraper.clickEvent(config.event.sequence_of_selectors.get(i));
              }
              else if(config.event.sequence_of_events.get(i).equals(EventType.SCROLL_DOWN)){
                 scraper.scrollDownEvent();
              }
              if(config.table_selector!=null)
                 execScrapeTable(scraper);
              else
                 execScrapeFields(scraper);
           }
        }
        else if(config.event.type.equals(EventType.CLICK)){
           if(config.event.repetition_type.equals(RepetitionType.AFTER_ALL_EVENTS)){
               for (int i=0; i<config.event.timesToRepeat; i++){
                  scraper.clickEvent(config.event.selector);
               }
               if (config.table_selector!=null)
                  execScrapeTable(scraper);
               else
                  execScrapeFields(scraper);
               
           }
           else if(config.event.repetition_type.equals(RepetitionType.AFTER_EACH_EVENT)){
              for (int i=0; i<config.event.timesToRepeat; i++){
                 scraper.clickEvent(config.event.selector);
                 if (config.table_selector!=null)
                     execScrapeTable(scraper);
                  else
                     execScrapeFields(scraper);
              }
           }
           scraper.quit();
        }
        else if(config.event.type.equals(EventType.SCROLL_DOWN)){
           if (config.event.timesToRepeat==null){
              scraper.scrollDownEvent();
              if (config.table_selector!=null)
                  execScrapeTable(scraper);
               else
                  execScrapeFields(scraper);
           }
           else{
              scraper.scrollDownEvent(config.event.timesToRepeat);
              if (config.table_selector!=null)
                  execScrapeTable(scraper);
               else
                  execScrapeFields(scraper);
           }
        }
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
         else if(config.bunch_urls!=null&&config.url.baseURL!=null&&config.url.relativeURL==null&&config.url.fullURL==null){
            if(config.nextPageSelector!=null)
               throw new Exception("Pagination can not be conducted in Bunch Scraper mode");
            BunchScraper scraper = new BunchScraper(config.bunch_urls, config.url.baseURL);
            if (config.table_selector!=null)
               execScrapeTable(scraper);
            else
               execScrapeFields(scraper);
         }
         else{
            throw new Exception("Configuration error: In defining url field you need to determine either (\"baseURL\" and \"relativeURL\") or (\"fullURL\") or (\"baseURL\" alongside with \"bunch_urls\" field)");
         }
      }
   }
   
   public ArrayList<HashMap> getSnippetFields(){
      return this.snippetFields;
   };
   
   public ArrayList<HashMap> getCompanyFields(){
      return this.companyFields;
   };
   
   private void execScrapeTable(AbstractScraper scraper) throws Exception{
      if(config.company_fields!=null){         
         companyFields.addAll(
                  (ArrayList<HashMap>) scraper.scrapeTable(
                          config.table_selector, 
                          config.company_fields
                  ));
         if(config.snippet_fields!=null){
            snippetFields.addAll(
                    (ArrayList<HashMap>) scraper.scrapeTable(
                            config.table_selector, 
                            config.snippet_fields
                    ));
         }
   }
      else if(config.snippet_fields!=null){
         snippetFields.addAll(
                 (ArrayList<HashMap>) scraper.scrapeTable(
                         config.table_selector, 
                         config.snippet_fields
                 ));
      }
      else
         throw new Exception("In the Configuration you need to define either company_fields or snippet_fields to scrape (or both)");
   }
   
   private void execScrapeFields(AbstractScraper scraper) throws Exception{
      if(config.company_fields!=null){
         companyFields.addAll(
                  (ArrayList<HashMap>) scraper.scrapeFields(
                          config.company_fields
                  ));
         if(config.snippet_fields!=null){
            snippetFields.addAll(
                    (ArrayList<HashMap>) scraper.scrapeFields(
                            config.snippet_fields
                    ));
         }
   }
      else if(config.snippet_fields!=null){
         snippetFields.addAll(
                 (ArrayList<HashMap>) scraper.scrapeFields(
                         config.snippet_fields
                 ));
      }
      else
         throw new Exception("In the Configuration you need to define either company_fields or snippet_fields to scrape (or both)");
      
   }
}
