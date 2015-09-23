package abstractscrapers.src;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author vasgat
 */
public class Company {
   private String CompanyName;
   private String CompanyLink;
   private ObjectId CompanyId;
   private String dbname;
   private String collection;
   private MongoUtils mongo;
   private CompanySearcher searcher;
   
   /**
    * Creates a Company object that connects the Company Name and Website with an entry from the dataset or creates a new one
    * @param CompanyName the name of the Company 
    * @param mongo a MongoUtils object
    * @param CompanyLink the Website of the company
    * @param dbname database's name
    * @param collection's name
    * @param searcher A CompanySearcher object
    */
   public Company(String CompanyName, MongoUtils mongo, String CompanyLink, String dbname, String collection, CompanySearcher searcher){
      this.CompanyName = CompanyName;
      this.CompanyLink = CompanyLink;
      this.dbname = dbname;
      this.collection = collection;
      this.mongo = mongo;
      this.searcher = searcher;
      this.CompanyId = findCompanyId(CompanyLink);
      if (CompanyId==null){
         CompanyId = insertCompany();
      }
   }

   /**
    * Creates a Company Object that connects the Company name with an entry from the companies collection or creates a new entry.
    * @param CompanyName CompanyName the name of the Company 
    * @param mongo a MongoUtils object
    * @param dbname database's name
    * @param collection's name
    * @param searcher A CompanySearcher object
    * @throws UnknownHostException 
    */
   public Company(String CompanyName, MongoUtils mongo, String dbname, String collection, CompanySearcher searcher) throws UnknownHostException{
      this.CompanyName = CompanyName;
      this.dbname = dbname;
      this.collection = collection;
      this.mongo = mongo;
      this.searcher = searcher;
      this.CompanyId = findCompanyId();
      if (CompanyId==null){
         CompanyLink = findCandidateLink();
         CompanyId = insertCompanyWithCandidateLink();
      }
   }   
   
   /**
    * This method inserts a field with extra infomation for the company in the database
    * @param fieldName
    * @param fieldValue 
    */
   public void insertInfo(String fieldName, String fieldValue){
      DBCollection companies = mongo.connect(dbname, collection);
      DBCursor result = companies.find(new BasicDBObject("_id",CompanyId)
              .append(fieldName, new BasicDBObject("$exists", true)));
      if (result.size()==0)
      companies.update(new BasicDBObject("_id", CompanyId), 
                                    new BasicDBObject("$set", 
                                          new BasicDBObject()
                                                   .append(fieldName, 
                                                            fieldValue)));
   }
   
   /**
    * @returns the company id
    */
   public ObjectId getId(){
      return CompanyId;
   }
   
   /**
    * @returns Company's name 
    */
   public String getCompanyName(){
      return CompanyName;
   }
   
   /**
    * @returns Company's website 
    */
   public String getCompanyLink(){
      return CompanyLink;
   }
   
   /**
    * Inserts Company to the database based on the available company name and website
    * @return company's id
    */
   private ObjectId insertCompany(){
      BasicDBObject object = new BasicDBObject();
      object.append("Company_name", CompanyName);
      object.append("Company_Link", CompanyLink);   
      BasicDBList list = new BasicDBList();
      list.add(CompanyName);
      object.append("Aliases", list);  
      ObjectId id = mongo.insertDoc(dbname, collection, object);
      return id;
   }
   
   /**
    * Inserts Company to the database based on company's name and candidate website 
    * Candidate website is obtained by a simple request to a search engine based
    * on the company's name.
    * @returns company's id 
    */
   private ObjectId insertCompanyWithCandidateLink(){
      BasicDBObject object = new BasicDBObject();
      object.append("Company_name", CompanyName);
      object.append("candidate_Company_Link", CompanyLink);     
      BasicDBList list = new BasicDBList();
      list.add(CompanyName);
      object.append("Aliases", list);
      ObjectId id = mongo.insertDoc(dbname, collection, object);     
      return id;
   }   
   
   /**
    * Searches if the company exists to the database by having available
    * company's website
    * @param CLink company's website
    * @returns company's id if the company exists to database
    */
   private ObjectId findCompanyId(String CLink){
      ObjectId tempId = searcher.searchByLink(CLink);
      if (tempId==null){
         tempId = searcher.searchForMatchInLookUpTable(CompanyName);           
      }
      else{
         DBCollection companies = mongo.connect(dbname, collection);         
         DBCursor tempCursor = companies.find(new BasicDBObject()
                                             .append("_id", tempId));
         BasicDBList aliases = (BasicDBList) tempCursor.next().get("Aliases");
         if (!aliases.contains(CompanyName)){
            aliases.add(CompanyName);
            companies.update(new BasicDBObject("_id", tempId), 
                                          new BasicDBObject("$set", 
                                                 new BasicDBObject()
                                                         .append("Aliases", 
                                                                     aliases)));
         }
      }
      return tempId;
   }

   /**
    * Searches if the company exists to the database by having available only Company's name
    * @returns company's id, if it is exists in db. 
    * @throws UnknownHostException 
    */
   private ObjectId findCompanyId() throws UnknownHostException{
      System.out.println("Look-up table Search");
      ObjectId tempId = searcher.searchForMatchInLookUpTable(CompanyName);
      if (tempId==null){
         System.out.println("SearchByGoogleResults");
         tempId = searcher.searchBySearchEngineResults(CompanyName);          
         if (tempId==null){
            System.out.println("SearchByName");
            tempId = searcher.searchByName(CompanyName);
            if(tempId!=null){
               DBCollection companies = mongo.connect(dbname, collection);
               System.out.println("Insert Company");
               DBCursor tempCursor = companies.find(new BasicDBObject()
                                                   .append("_id", tempId));
               BasicDBList aliases = (BasicDBList) tempCursor.next()
                                                         .get("Aliases");
               aliases.add(CompanyName);
               companies.update(new BasicDBObject("_id", tempId), 
                                             new BasicDBObject("$set", 
                                                   new BasicDBObject()
                                                            .append("Aliases", 
                                                                     aliases)));  
            }
         }
         else{
            DBCollection companies = mongo.connect(dbname, collection);
            DBCursor tempCursor = companies.find(new BasicDBObject()
                                                .append("_id", tempId));
            BasicDBList aliases = (BasicDBList) tempCursor.next().get("Aliases");
            aliases.add(CompanyName);
            companies.update(new BasicDBObject("_id", tempId), 
                                          new BasicDBObject("$set", 
                                                new BasicDBObject()
                                                         .append("Aliases", 
                                                                  aliases)));
         }
      }   
      
      return tempId;
   }   
   
   /**
    * This function searches by Company name in a search engine and returns the first result
    * @returns the candidate company's website.
    */
   private String findCandidateLink(){
      try {
         String query = CompanyName.replace(" ", "+");
         Document googleResults = Jsoup.connect(
               new URI("http://www.dogpile.com/search/web?q="+query)
                    .toASCIIString()).userAgent("Mozilla/36.0").timeout(60000).get();
         String firstResult = googleResults.getElementById("webResults")
                           .select("div.resultDisplayUrlPane").get(0).text();  
         return firstResult;
      } catch (URISyntaxException ex) {
         Logger.getLogger(Company.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         Logger.getLogger(Company.class.getName()).log(Level.SEVERE, null, ex);
      } catch (NullPointerException e){
         return null;
      }
      return null;
   }
   
}

