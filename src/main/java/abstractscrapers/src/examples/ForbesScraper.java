package abstractscrapers.src.examples;

import abstractscrapers.src.Company;
import abstractscrapers.src.CompanySearcher;
import abstractscrapers.src.Field;
import abstractscrapers.src.FieldType;
import abstractscrapers.src.MongoUtils;
import abstractscrapers.src.OutputFormatter.CompanySnippet;
import abstractscrapers.src.Scrapers.DynamicHTMLScraper;
import abstractscrapers.src.Scrapers.StaticHTMLScraper;
import abstractscrapers.src.SelectorType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vasgat
 */
public class ForbesScraper {
   private List<Field> DynamicTableCompanyFields;
   private List<Field> DynamicTableCompanySnippets;
   private List<Field> StaticTableFields;  
   public DynamicHTMLScraper scraper;
   
   public ForbesScraper() throws URISyntaxException, IOException, InterruptedException{
      DynamicTableCompanyFields = new ArrayList<Field>();
      DynamicTableCompanySnippets = new ArrayList<Field>();
      Field cfield = new Field(
              "Company Name",
              "td:nth-child(3)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      Field cfield2 = new Field(
              "Logo",
              "td:nth-child(1) > a:nth-child(1) > img:nth-child(1)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.image
      );      
      Field cfield3 = new Field(
              "Country",
              "td:nth-child(4)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );       
      Field field1 = new Field(
              "Rank in Forbes",
              "td:nth-child(2)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );      
      Field field2 = new Field(
              "Sales",
              "td:nth-child(5)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );  
      
      Field field3 = new Field(
              "Sales",
              "td:nth-child(5)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );  
      
      Field field4 = new Field(
              "Profits",
              "td:nth-child(6)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      
      Field field5 = new Field(
              "Assets",
              "td:nth-child(7)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      Field field6 = new Field(
              "Market Value",
              "td:nth-child(8)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      Field field7 = new Field(
              "Company Link",
              "td:nth-child(3) > a:nth-child(1)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.link
      );      
           
      DynamicTableCompanyFields.add(cfield);
      DynamicTableCompanyFields.add(cfield2);
      DynamicTableCompanyFields.add(cfield3);
      
      DynamicTableCompanySnippets.add(field1);
      DynamicTableCompanySnippets.add(field2);
      DynamicTableCompanySnippets.add(field3);
      DynamicTableCompanySnippets.add(field4);
      DynamicTableCompanySnippets.add(field5);
      DynamicTableCompanySnippets.add(field6);
      DynamicTableCompanySnippets.add(field7);
      
      StaticTableFields = new ArrayList<Field>();
      Field sfield = new Field(
              "dt",
              "dd",  
              SelectorType.CSS, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
         );    
      StaticTableFields.add(sfield);   
      
      scraper = new DynamicHTMLScraper("http://www.forbes.com/global2000/list/");
   }
   
   public ArrayList<HashMap<String, Object>> getCompaniesSnippets() throws URISyntaxException, IOException, InterruptedException{
      ArrayList CompanySnippets = 
              scraper.scrapeTable(
                      "#list-table-body > tr.data", 
                      DynamicTableCompanySnippets
              );
      for (int i=0; i<CompanySnippets.size(); i++){
         HashMap Company = (HashMap) CompanySnippets.get(i);
         StaticHTMLScraper StaticScraper = new StaticHTMLScraper((String) Company.get("Company Link"));   
         HashMap temp_Hash = (HashMap) CompanySnippets.get(i);
         temp_Hash.putAll(Company);
         ArrayList temp_Array = StaticScraper.scrapeTable(".data > dl", StaticTableFields);
         for (int j=0; j<temp_Array.size(); j++){
            temp_Hash.putAll((Map) temp_Array.get(j));
         }
         CompanySnippets.set(i, temp_Hash);
      }
      return CompanySnippets;
   }
   
   public ArrayList<HashMap<String, Object>> getCompaniesInfo(){
      ArrayList CompanyInfo = 
              scraper.scrapeTable(
                      "#list-table-body > tr.data", 
                      DynamicTableCompanyFields
              );
      return CompanyInfo;
   }
   
   public void store(ArrayList<HashMap<String, Object>> companies, ArrayList<HashMap<String, Object>> snippets) throws UnknownHostException{
      MongoUtils mongo = new MongoUtils();
      CompanySearcher searcher = new CompanySearcher(mongo, "Wikirate2", "Companies");
      for(int i=0; i<companies.size(); i++){
         HashMap temp_comp = companies.get(i);
         HashMap temp_snip = snippets.get(i);         
         Company company = new Company(
                 (String) temp_comp.get("Company Name"), 
                 mongo, 
                 (String) temp_snip.get("Website"), 
                 "Wikirate2", 
                 "Companies",
                 searcher
         );
         
         temp_comp.remove("Company Name");
         temp_comp.remove("citeyear");
         temp_comp.remove("source");
         temp_snip.remove("Website");
         temp_snip.remove("Company Link");
         temp_snip.remove("Country");
         Iterator c = temp_comp.entrySet().iterator();
         while(c.hasNext()){
            Map.Entry info = (Map.Entry) c.next();
            company.insertInfo((String) info.getKey(), (String) info.getValue());
         }
         int citeyear = (int) temp_snip.get("citeyear");
         String source = (String) temp_snip.get("source");
         temp_snip.remove("citeyear");
         temp_snip.remove("source");
         Iterator s = temp_snip.entrySet().iterator();
         while(s.hasNext()){
            Map.Entry snip = (Map.Entry) s.next();
            CompanySnippet snippet = new CompanySnippet(
                    (String) snip.getKey(),
                    snip.getValue(),
                    source, 
                    null,
                    citeyear,
                    company.getId(),
                    null,
                    "Forbes",
                    null
            );
            snippet.store("Wikirate2", "Snippets", mongo);            
         }
      }
      mongo.closeConnection();
   }
   
   public static void main(String[] args) 
           throws URISyntaxException, IOException, InterruptedException{
      ForbesScraper forbes = new ForbesScraper();
      forbes.scraper.scrollDownEvent();         
      ArrayList<HashMap<String, Object>> companies = 
                                    forbes.getCompaniesInfo();
      ArrayList<HashMap<String, Object>> snippets = 
                                    forbes.getCompaniesSnippets();
      forbes.store(companies, snippets);
      forbes.scraper.quit();
   }
}
