package abstractscrapers.src.examples;

import abstractscrapers.src.Company;
import abstractscrapers.src.Field;
import abstractscrapers.src.FieldType;
import abstractscrapers.src.MongoUtils;
import abstractscrapers.src.OutputFormatter.CompanySnippet;
import abstractscrapers.src.SelectorType;
import abstractscrapers.src.StaticWebPagesScrapers.PaginationIterator;
import abstractscrapers.src.StaticWebPagesScrapers.Scraper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author vasgat
 */
public class wob2020Scraper {
   public Scraper scraper;
   private ArrayList<Field> tfields; 
   
   public wob2020Scraper() throws URISyntaxException, IOException{
      tfields = new ArrayList<Field>();
      
      Field field1 = new Field(
              "Company Name",
              "td:nth-child(1)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field field2 = new Field(
              "Sector",
              "td:nth-child(6)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field field3 = new Field(
              "% of Women on the board of directors",
              "td:nth-child(5)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field field4 = new Field(
              "Country",
              "United States",
              SelectorType.rawtext,
              SelectorType.rawtext,
              FieldType.text,
              FieldType.text
      );      
      
      tfields.add(field1);
      tfields.add(field2);
      tfields.add(field3);
      tfields.add(field4);
      
      scraper = new Scraper("http://www.2020wob.com","/company-directory");
   }
   
   public void getCompaniesSnippets() throws URISyntaxException, IOException, Exception{
      PaginationIterator iterator = new PaginationIterator(
              scraper, 
              ".pager-next > a:nth-child(1)"
      );
      ArrayList<HashMap> result = iterator.scrapeTable(".views-table > tbody:nth-child(2) > tr", tfields);

      for (int i=0; i<result.size(); i++){
         HashMap temp_Comp = result.get(i);
         if(temp_Comp.get("Company Name")!=null){
         MongoUtils mongo = new MongoUtils();
         Company company = new Company(
                 (String) temp_Comp.get("Company Name"), 
                 mongo, "Wikirate2", 
                 "Companies"
         );
         company.insertInfo("Sector", (String) temp_Comp.get("Sector"));
         company.insertInfo("Country", (String) temp_Comp.get("Country"));
         temp_Comp.remove("Sector");
         temp_Comp.remove("Country");
         int citeyear = (int) temp_Comp.get("citeyear");
         String source = (String) temp_Comp.get("source");
         temp_Comp.remove("citeyear");
         temp_Comp.remove("source");
         temp_Comp.remove("Company Name");
         Iterator CompanyIter = temp_Comp.entrySet().iterator();
         while(CompanyIter.hasNext()){
            Map.Entry entry = (Map.Entry) CompanyIter.next();
            CompanySnippet snippet = new CompanySnippet(
                 (String) entry.getKey(),
                 entry.getValue(),
                 source,
                 null, 
                 citeyear,
                 company.getId(),
                 null,
                 "2020wob",
                 null
            );
            snippet.store("Wikirate2", "Snippets", mongo);
         }
      }
      }  
   }
   
   public static void main(String[] args) throws URISyntaxException, IOException, Exception{
      wob2020Scraper wob2020 = new wob2020Scraper();
      wob2020.getCompaniesSnippets();
   }
}
