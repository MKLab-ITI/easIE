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
 *
 * @author vasgat
 */
public class Company {
   private String CompanyName;
   private String CompanyLink;
   private ObjectId CompanyId;
   private String dbname;
   private String collection;
   private MongoUtils mongo;
   
   public Company(String CompanyName, MongoUtils mongo, String CompanyLink, String dbname, String collection){
      this.CompanyName = CompanyName;
      this.CompanyLink = CompanyLink;
      this.dbname = dbname;
      this.collection = collection;
      this.mongo = mongo;
      this.CompanyId = findCompanyId(CompanyLink);
      if (CompanyId==null){
         CompanyId = insertCompany();
      }
   }

   public Company(String CompanyName, MongoUtils mongo, String dbname, String collection) throws UnknownHostException{
      this.CompanyName = CompanyName;
      this.dbname = dbname;
      this.collection = collection;
      this.mongo = mongo;
      this.CompanyId = findCompanyId();
      if (CompanyId==null){
         CompanyLink = findCandidateLink();
         CompanyId = insertCompanyWithCandidateLink();
      }
   }   
   
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
   
   public ObjectId getId(){
      return CompanyId;
   }
   
   public String getCompanyName(){
      return CompanyName;
   }
   
   public String getCompanyLink(){
      return CompanyLink;
   }
   
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
   
   private ObjectId findCompanyId(String CLink){
      CompanySearcher searcher = new CompanySearcher(CompanyName, mongo, dbname, collection);
      ObjectId tempId = searcher.searchByLink(CLink);
      if (tempId==null){
         tempId = searcher.searchForMatchInLookUpTable();           
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

   private ObjectId findCompanyId() throws UnknownHostException{
      CompanySearcher searcher = new CompanySearcher(CompanyName, mongo, dbname, collection);
      System.out.println("Look-up table Search");
      ObjectId tempId = searcher.searchForMatchInLookUpTable();
      if (tempId==null){
         System.out.println("SearchByGoogleResults");
         tempId = searcher.searchByGoogleResults();          
         if (tempId==null){
            System.out.println("SearchByName");
            tempId = searcher.searchByName();
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

