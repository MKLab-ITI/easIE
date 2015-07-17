package abstractscrapers.src;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vasgat
 */
public class main {
   public static void main(String[] args) throws URISyntaxException, IOException, Exception{  
      abstractscrapers.src.DynamicWebPagesScrapers.Scraper scraper = new abstractscrapers.src.DynamicWebPagesScrapers.Scraper("http://www.forbes.com/global2000/list/");
      Field field = new Field(
              "Company Name",
              "td:nth-child(3)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      List<Field> fields = new ArrayList<Field>();
      fields.add(field);
      System.out.println(scraper.scrapeTable("#list-table-body > tr.data", fields));
      for (int i=0; i<105; i++){
         System.out.println(i);
         scraper.scrollDownEvent();
      }
      System.out.println(scraper.scrapeTable("#list-table-body > tr.data", fields).size());
      scraper.quit();
   /*   
      Field field = new Field(
              "Sector",
              "td:nth-child(2)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      
      Field field2 = new Field(
              "Participant",
              "td:nth-child(1)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      
      List<Field> toScrape = new ArrayList<Field>();
      toScrape.add(field);
      toScrape.add(field2);
      
      Scraper scraper = new Scraper(
              "https://www.unglobalcompact.org",
              "/participation/report/cop/create-and-submit/advanced?page=1&per_page=250"
      );
      PaginationIterator iterator = new PaginationIterator(scraper, ".next_page");
      System.out.println(iterator.scrapeTable("tbody > tr", toScrape));
      List<String> ingredients = new ArrayList<String>();
      ingredients.add("peanut+butter");
      AllRecipesScraper allrecipesScraper = new AllRecipesScraper();
      allrecipesScraper.scrapeRecipesByIngedient(ingredients);*/
   }
}
