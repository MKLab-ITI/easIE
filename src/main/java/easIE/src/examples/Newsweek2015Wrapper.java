package easIE.src.examples;

import easIE.src.Company;
import easIE.src.CompanySearcher;
import easIE.src.Field;
import easIE.src.FieldType;
import easIE.src.MongoUtils;
import easIE.src.OutputFormatter.CompanySnippet;
import easIE.src.Wrappers.DynamicHTMLWrapper;
import easIE.src.SelectorType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author vasgat
 */
public class Newsweek2015Wrapper {
   private ArrayList<Field> CompaniesInfo;
   private ArrayList<Field> CompaniesSnippets;
   public DynamicHTMLWrapper wrapper;
   
   public Newsweek2015Wrapper(String listLink) throws URISyntaxException, IOException, InterruptedException{
      CompaniesInfo = new ArrayList<Field>();
      CompaniesSnippets = new ArrayList<Field>();
      
      Field cfield1 = new Field(
              "Company Name",
              "td:nth-child(3)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      Field cfield2 = new Field(
              "Country",
              "td:nth-child(4)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      Field cfield3 = new Field(
              "Sector",
              "td:nth-child(5)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      Field cfield4 = new Field(
              "Industry",
              "td:nth-child(6)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );      
      CompaniesInfo.add(cfield1);
      CompaniesInfo.add(cfield2);
      CompaniesInfo.add(cfield3);
      CompaniesInfo.add(cfield4);
      
      Field sfield1 = new Field(
              "Energy Productivity",
              "td:nth-child(7)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field sfield2 = new Field(
              "Carbon Productivity",
              "td:nth-child(8)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field sfield3 = new Field(
              "Water Productivity",
              "td:nth-child(9)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field sfield4 = new Field(
              "Waste Productivity",
              "td:nth-child(10)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field sfield5 = new Field(
              "Green Revenue",
              "td:nth-child(11)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field sfield6 = new Field(
              "Pay Link",
              "td:nth-child(12)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field sfield7 = new Field(
              "Sustainability Themed Committee",
              "td:nth-child(13)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field sfield8 = new Field(
              "Audit",
              "td:nth-child(14)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      ); 
      Field sfield9 = new Field(
              "Newsweek Green Score",
              "td:nth-child(2)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      Field sfield10 = new Field(
              "Newsweek Rank 2015",
              "td:nth-child(1)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );    
      Field sfield11 = new Field(
              "citeyear",
              "2015",
              SelectorType.rawtext,
              SelectorType.rawtext,
              FieldType.text,
              FieldType.text
      );          
      CompaniesSnippets.add(sfield1);
      CompaniesSnippets.add(sfield2);
      CompaniesSnippets.add(sfield3);
      CompaniesSnippets.add(sfield4);
      CompaniesSnippets.add(sfield5);
      CompaniesSnippets.add(sfield6);
      CompaniesSnippets.add(sfield7);
      CompaniesSnippets.add(sfield8);
      CompaniesSnippets.add(sfield9);
      CompaniesSnippets.add(sfield10);
      CompaniesSnippets.add(sfield11);
      
      this.wrapper = new DynamicHTMLWrapper(
              listLink
      );   
   }
   
   private void getCompaniesSnippets() throws UnknownHostException{
      ArrayList<HashMap<String, Object>> info = wrapper.extractTable(
              "table.ranking-table:nth-child(2) > tbody:nth-child(3) > tr", 
              CompaniesInfo
      );
      System.out.println(info.size());
      ArrayList<HashMap<String, Object>> snips =wrapper.extractTable(
              "table.ranking-table:nth-child(2) > tbody:nth-child(3) > tr", 
              CompaniesSnippets
      );      
      System.out.println(snips.size());
      System.out.println(snips);
      MongoUtils mongo = new MongoUtils();
            CompanySearcher searcher = new CompanySearcher(mongo, "Wikirate2", "Companies");
      for (int i=0; i<info.size(); i++){
         HashMap temp_Comp = info.get(i);
         HashMap temp_Snips = snips.get(i);
         if(temp_Comp.get("Company Name")!=null&&
                                       !temp_Comp.equals("")){
            Company company = new Company(
                    (String) temp_Comp.get("Company Name"), 
                    mongo, 
                    "WikiRate_NEW", 
                    "Companies",
                    searcher
            );
            temp_Comp.remove("Company Name");
            temp_Comp.remove("citeyear");
            temp_Comp.remove("source");
            Iterator CompIter = temp_Comp.entrySet().iterator();
            while (CompIter.hasNext()){
               Map.Entry entry = (Map.Entry) CompIter.next();
               company.insertInfo(
                       (String) entry.getKey(), 
                       (String)entry.getValue()
               ); 
            }

            String source = (String) temp_Snips.get("source");
            int citeyear = Integer.parseInt((String)temp_Snips.get("citeyear"));
            temp_Snips.remove("source");
            temp_Snips.remove("citeyear");
            Iterator SnipsIter = temp_Snips.entrySet().iterator();
            while(SnipsIter.hasNext()){
               Map.Entry entry = (Map.Entry) SnipsIter.next();
               CompanySnippet snippet = new CompanySnippet(
                    (String) entry.getKey(),
                    entry.getValue(),
                    source,
                    null, 
                    citeyear,
                    company.getId(),
                    null,
                    "Newsweek",
                    null
               );
               snippet.store("WikiRate_NEW", "Snippets", mongo);
            }
         }
      }
   }
   
   public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException{
      Newsweek2015Wrapper newsweek = new Newsweek2015Wrapper("http://www.newsweek.com/green-2015/top-green-companies-world-2015");
      //newsweek.wrapper.clickEvent(".table-links > li:nth-child(3)");
      newsweek.wrapper.clickEvent(".view-toggle");
      newsweek.getCompaniesSnippets();
      newsweek.wrapper.quit();
   }   
}
