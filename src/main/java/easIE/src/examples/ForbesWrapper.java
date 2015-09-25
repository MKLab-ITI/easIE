package easIE.src.examples;

import easIE.src.Company;
import easIE.src.CompanySearcher;
import easIE.src.Field;
import easIE.src.FieldType;
import easIE.src.MongoUtils;
import easIE.src.OutputFormatter.CompanySnippet;
import easIE.src.Wrappers.DynamicHTMLWrapper;
import easIE.src.Wrappers.StaticHTMLWrapper;
import easIE.src.SelectorType;
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
public class ForbesWrapper {
   private List<Field> DynamicTableCompanyFields;
   private List<Field> DynamicTableCompanySnippets;
   private List<Field> StaticTableFields;  
   public DynamicHTMLWrapper Wrapper;
   
   public ForbesWrapper() throws URISyntaxException, IOException, InterruptedException{
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
      
      Wrapper = new DynamicHTMLWrapper("http://www.forbes.com/global2000/list/");
   }
   
   public ArrayList<HashMap<String, Object>> getCompaniesSnippets() throws URISyntaxException, IOException, InterruptedException{
      ArrayList CompanySnippets = 
              Wrapper.extractTable(
                      "#list-table-body > tr.data", 
                      DynamicTableCompanySnippets
              );
      for (int i=0; i<CompanySnippets.size(); i++){
         HashMap Company = (HashMap) CompanySnippets.get(i);
         StaticHTMLWrapper StaticScraper = new StaticHTMLWrapper((String) Company.get("Company Link"));   
         HashMap temp_Hash = (HashMap) CompanySnippets.get(i);
         temp_Hash.putAll(Company);
         ArrayList temp_Array = StaticScraper.extractTable(".data > dl", StaticTableFields);
         for (int j=0; j<temp_Array.size(); j++){
            temp_Hash.putAll((Map) temp_Array.get(j));
         }
         CompanySnippets.set(i, temp_Hash);
      }
      return CompanySnippets;
   }
   
   public ArrayList<HashMap<String, Object>> getCompaniesInfo(){
      ArrayList CompanyInfo = 
              Wrapper.extractTable(
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
      ForbesWrapper forbes = new ForbesWrapper();
      forbes.Wrapper.scrollDownEvent();         
      ArrayList<HashMap<String, Object>> companies = 
                                    forbes.getCompaniesInfo();
      ArrayList<HashMap<String, Object>> snippets = 
                                    forbes.getCompaniesSnippets();
      forbes.store(companies, snippets);
      forbes.Wrapper.quit();
   }
}
