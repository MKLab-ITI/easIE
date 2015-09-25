package easIE.src.examples;

import easIE.src.Company;
import easIE.src.CompanySearcher;
import easIE.src.Field;
import easIE.src.FieldType;
import easIE.src.MongoUtils;
import easIE.src.OutputFormatter.CompanySnippet;
import easIE.src.Wrappers.BunchWrapper;
import easIE.src.Wrappers.PaginationIterator;
import easIE.src.Wrappers.StaticHTMLWrapper;
import easIE.src.SelectorType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author vasgat
 */
public class FairlaborWrapper {
   private ArrayList<Field> init;
   private ArrayList<Field> flaInfo;
   
   public FairlaborWrapper() throws URISyntaxException, IOException, Exception{
      
      init = new ArrayList<Field>();
      Field flaLink = new Field(
              "Company Link",
              "div:nth-child(1) > a:nth-child(1)",  
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.link
      );
      init.add(flaLink);
      
      flaInfo = new ArrayList<Field>();
      Field flaCompany = new Field(
              "Company Name",
              "#page-title",  
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );      
      
      Field flaCompany1 = new Field(
              "Company Link",
              "div.views-field:nth-child(4) > div:nth-child(1) > a:nth-child(1)",  
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.link
      );
      
      Field flaCompany2 = new Field(
              "About",
              ".field-item",  
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      
      Field flaCompany3 = new Field(
              "Logo",
              ".views-field-field-logo > div:nth-child(1) > img:nth-child(1)",  
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.image
      );  
      
      Field flaCompany4 = new Field(
              "FLA Participating Company",
              "true",  
              SelectorType.rawtext, 
              SelectorType.rawtext,
              FieldType.text,
              FieldType.text
      );       
      flaInfo.add(flaCompany);
      flaInfo.add(flaCompany1);
      flaInfo.add(flaCompany2);
      flaInfo.add(flaCompany3);
      flaInfo.add(flaCompany4);
   }
   
   public void getCompaniesSnippets() throws URISyntaxException, IOException, Exception{
      StaticHTMLWrapper wrapper = new StaticHTMLWrapper("http://www.fairlabor.org","/affiliates/participating-companies");
      PaginationIterator iter = new PaginationIterator(wrapper, ".pager-next > a:nth-child(1)");
      ArrayList<HashMap> result = iter.extractTable("div.view-content > div.views-row", init);
      HashSet Links = new HashSet();
      for (int i=0; i<result.size(); i++){
         Links.add(result.get(i).get("Company Link"));
      }

      BunchWrapper butch = new BunchWrapper(Links, "http://www.fairlabor.org");
      ArrayList<HashMap> flaFields = butch.extractFields(flaInfo); 
      
      MongoUtils mongo = new MongoUtils();
      CompanySearcher searcher = new CompanySearcher(mongo, "Wikirate2", "Companies");
      for (int i=0; i<flaFields.size(); i++){
         HashMap temp_Comp = flaFields.get(i);
         if (temp_Comp.get("Company Name")!=null){
            Company company;
            if (!temp_Comp.get("Company Link").equals("")&&
                           temp_Comp.get("Company Link")!=null){
               System.out.println(temp_Comp.get("Company Name"));
               System.out.println(temp_Comp.get("Company Link"));
               company = new Company(
                       (String) temp_Comp.get("Company Name"), 
                       mongo,
                       (String) temp_Comp.get("Company Link"),
                       "Wikirate2",
                       "Companies",
                       searcher
            );}            
            else{
               company = new Company(
                       (String) temp_Comp.get("Company Name"), 
                       mongo,
                       "Wikirate2",
                       "Companies",
                       searcher
            );}
            company.insertInfo("Logo", (String) temp_Comp.get("Logo"));
            String source = (String) temp_Comp.get("source");
            int citeyear = (int) temp_Comp.get("citeyear");
            temp_Comp.remove("Company Name");
            temp_Comp.remove("Company Link");
            temp_Comp.remove("source");
            temp_Comp.remove("citeyear");
            temp_Comp.remove("Logo");
            Iterator it = temp_Comp.entrySet().iterator();
            while(it.hasNext()){
               Map.Entry entry = (Map.Entry) it.next();
               CompanySnippet snippet = new CompanySnippet(
                    (String) entry.getKey(),
                    entry.getValue(),
                    source, 
                    null,
                    citeyear,
                    company.getId(),
                    null,
                    "Fairlabor",
                    null
               );
               snippet.store("Wikirate2", "Snippets", mongo);
            }
         }
      }
   }
   
   public static void main(String[] args) throws IOException, Exception{
      FairlaborWrapper fla = new FairlaborWrapper();
      fla.getCompaniesSnippets();
   }
}
