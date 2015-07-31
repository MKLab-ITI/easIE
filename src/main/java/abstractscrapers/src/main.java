package abstractscrapers.src;

import abstractscrapers.src.StaticWebPagesScrapers.Scraper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
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
      Field field2 = new Field(
              "Logo",
              "td:nth-child(1) > a:nth-child(1) > img:nth-child(1)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.image
      );
      Field field3 = new Field(
              "Rank",
              "td:nth-child(2)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      Field field4 = new Field(
              "Country",
              "td:nth-child(4)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      
      Field field5 = new Field(
              "Sales",
              "td:nth-child(5)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );  
      
      Field field6 = new Field(
              "Sales",
              "td:nth-child(5)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );  
      
      Field field7 = new Field(
              "Profits",
              "td:nth-child(6)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      
      Field field8 = new Field(
              "Assets",
              "td:nth-child(7)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      
      Field field9 = new Field(
              "Market Value",
              "td:nth-child(8)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      
      Field field10 = new Field(
              "Company Link",
              "td:nth-child(3) > a:nth-child(1)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.link
      );      
      List<Field> fields = new ArrayList<Field>();
      fields.add(field);
      fields.add(field2);
      fields.add(field3);
      fields.add(field4);
      fields.add(field5);
      fields.add(field6);
      fields.add(field7);
      fields.add(field8);
      fields.add(field9);
      fields.add(field10);        
      
      scraper.scrollDownEvent();
      ArrayList result = scraper.scrapeTable("#list-table-body > tr.data", fields);
      System.out.println(result.size());
      System.out.println(result);
      
      scraper.quit(); 
      
      ArrayList<Field> fieldsSet = new ArrayList<Field>();
      Field cfield = new Field(
              "dt",
              "dd",  
              SelectorType.CSS, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
         );    
         fieldsSet.add(cfield);
         System.out.println(fieldsSet);      
      for (int i=0; i<result.size(); i++){
         HashMap Company = (HashMap) result.get(i);
         System.out.println(Company.get("Company Link"));
         Scraper StaticScraper = new Scraper((String) Company.get("Company Link"));        
         System.out.println(StaticScraper.scrapeTable(".data > dl", fieldsSet));
      }

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
