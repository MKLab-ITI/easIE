package abstractscrapers.src.Executor;

import abstractscrapers.src.Company;
import abstractscrapers.src.Configure.SnippetType;
import abstractscrapers.src.Configure.Store;
import abstractscrapers.src.MongoUtils;
import abstractscrapers.src.OutputFormatter.CompanySnippet;
import abstractscrapers.src.OutputFormatter.Snippet;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author vasgat
 */
public class SnippetHandler {
   ArrayList<HashMap> company_fields;
   ArrayList<HashMap> snippet_fields;
   
   public SnippetHandler(ArrayList<HashMap> company_fields, ArrayList<HashMap> snippet_fields) throws Exception{
      if (company_fields==null)
         throw new Exception("We can not determine Company Snippets without having available information about companies");
      this.company_fields = company_fields;
      this.snippet_fields = snippet_fields;
   }
   
   public void store(Store storeInfo, String sourceName) throws UnknownHostException, FileNotFoundException{
      MongoUtils mongo = new MongoUtils();
      String abstractJSON = "";
      
      for(int i=0; i<company_fields.size(); i++){
         if (storeInfo.toMongo!=null&&storeInfo.as.equals(SnippetType.COMPANY_SNIPPET)&&storeInfo.toHardDrive==null){
            HashMap companyInfo = company_fields.get(i);
            companyInfo.remove("citeyear");
            companyInfo.remove("source");
            Company company;
            if (companyInfo.containsKey("Website"))
               company = new Company(
                       (String) companyInfo.get("Company Name"), 
                       mongo,
                       (String) companyInfo.get("Website"),
                       storeInfo.toMongo.dbname,
                       storeInfo.toMongo.companies_collection
               );
            else
               company = new Company(
                       (String) companyInfo.get("Company Name"), 
                       mongo,
                       storeInfo.toMongo.dbname,
                       storeInfo.toMongo.companies_collection
               );
            companyInfo.remove("Company Name");
            companyInfo.remove("Website");
            Iterator iter = companyInfo.entrySet().iterator();
            while(iter.hasNext()){
               Map.Entry<String, String> entry = 
                       (Map.Entry<String, String>) iter.next();
               company.insertInfo(entry.getKey(), entry.getValue());
            }
            HashMap currentSnips = snippet_fields.get(i);
            int citeyear = (int) currentSnips.get("citeyear");
            currentSnips.remove("citeyear");
            String source = (String) currentSnips.get("source");
            currentSnips.remove("source");
            Iterator snipsIter = currentSnips.entrySet().iterator();
            while (snipsIter.hasNext()){
               Map.Entry<String, Object> entry = 
                       (Map.Entry<String, Object>) snipsIter.next();
               CompanySnippet snippet = new CompanySnippet(
                       (String) entry.getKey(),
                       entry.getValue(),
                       source, 
                       null,
                       citeyear,
                       company.getId(),
                       null,
                       sourceName,
                       null
               );
            snippet.store(
                    storeInfo.toMongo.dbname, 
                    storeInfo.toMongo.snippets_collection, 
                    mongo
            ); 
            }
         }
         else if (storeInfo.toMongo!=null&&storeInfo.as.equals(SnippetType.ABSTRACT_SNIPPET)&&storeInfo.toHardDrive==null){
               HashMap snips = company_fields.get(i);
               snips.putAll(snippet_fields.get(i));
               Snippet snippet = new Snippet(snips);
               snippet.store(storeInfo.toMongo.dbname, storeInfo.toMongo.snippets_collection, mongo);
         }
         else if(storeInfo.toHardDrive!=null&&storeInfo.as.equals(SnippetType.ABSTRACT_SNIPPET)&&storeInfo.toMongo==null){
               HashMap snips = company_fields.get(i);
               System.out.println(snippet_fields.size());
               
               snips.putAll(snippet_fields.get(i));
               Snippet snippet = new Snippet(snips);
               abstractJSON += snippet.getSnippetDBObject()+"\n";
               if(i==company_fields.size()-1){
                  PrintWriter writer = new PrintWriter(storeInfo.toHardDrive);
                  writer.println(abstractJSON);
                  writer.close();
               }
         }
      }
      mongo.closeConnection();
   }
}
