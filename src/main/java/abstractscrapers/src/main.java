package abstractscrapers.src;

import abstractscrapers.src.StaticWebPagesScrapers.PaginationIterator;
import abstractscrapers.src.StaticWebPagesScrapers.Scraper;
import abstractscrapers.src.examples.AllRecipesScraper;
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
      allrecipesScraper.scrapeRecipesByIngedient(ingredients);
   }
}
