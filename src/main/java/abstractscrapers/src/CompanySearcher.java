package abstractscrapers.src;

import abstractscrapers.src.similarities.TFIDFSimilarity;
import com.mongodb.BasicDBObject;
import static com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * CompanySearcher searches for a match of a company in database
 * @author vasgat
 */
public class CompanySearcher {
   private String collection;
   private String dbname;
   private MongoUtils mongo;
   private HashMap<String, HashMap<String, Double>> TFIDF_weights;
   
   /**
    * Creates a CompanySearcher object
    * @param mongo a MongoUtils object
    * @param dbname database name
    * @param collection's name
    */
   public CompanySearcher(MongoUtils mongo, String dbname, String collection){
      this.collection = collection;
      this.dbname = dbname;
      this.mongo = mongo;
      this.TFIDF_weights = calculateTFIDF();
   }
   
   /**
    * Search for a company in the collection by company's website
    * @param CompanyLink
    * @returns company's id, if exists to db.
    */
   public ObjectId searchByLink(String CompanyLink){
      DBCollection companies = mongo.connect(dbname, collection);
      BasicDBObject query = new BasicDBObject();
      if (CompanyLink!=null){
            query.put("Company_Link", java.util.regex.Pattern.compile(
                                                      getDomain(CompanyLink))
            );            
            if (companies.find(query).size()==1){
               DBCursor tempCursor = (DBCursor) companies.find(query);
               ObjectId tid = (ObjectId) tempCursor.next().get("_id");
               tempCursor.close();
               return tid;
            }
            else{
               query.clear();
               query.put("candidate_Company_Link", 
                       java.util.regex.Pattern.compile(getDomain(CompanyLink)));
               if (companies.find(query).size()==1){
                  DBCursor tempCursor = (DBCursor) companies.find(query);
                  ObjectId tempId = (ObjectId) tempCursor.next().get("_id");
                  companies.update(new BasicDBObject("_id", tempId), 
                  new BasicDBObject("$rename", new BasicDBObject()
                     .append("candidate_Company_Link", "Company_Link")));  
                  tempCursor.close();
                  return tempId;
                  }   
            }
      }
      return null;
   }
   
   /**
    * Searches for a company in the collection by company's name
    * @param CompanyName
    * @returns company's id, if company exists to db. 
    */
   public ObjectId searchByName(String CompanyName){
      TFIDF_weights.put("candidate", Tokenizer.getTokenVectorFrequency2(CompanyName));
      Iterator it = TFIDF_weights.keySet().iterator();
      while(it.hasNext()){
         String doc_id = (String) it.next();
         double sim = TFIDFSimilarity.calculate(
                 TFIDF_weights,
                 "candidate", 
                 doc_id
         );
         if (sim>0.7&&!doc_id.equals("candidate")){
            return new ObjectId(doc_id);
         }
      }       
      return null;
   }
   
   /**
    * Searches for a match in the collection by candidate company's website 
    * based on the results a search engine returns (search by name)
    * @param CompanyName
    * @returns company's id, if company exists to db.
    */
   public ObjectId searchBySearchEngineResults(String CompanyName){
      try {
         try {
            String query = CompanyName.replace(" ", "+");
            Document googleResults = Jsoup.connect(
                    new URI("http://www.dogpile.com/search/web?q="+query)
                    .toASCIIString()).userAgent("Mozilla/37.0").timeout(60000).get();
            String firstResult = googleResults.getElementById("webResults")
                           .select("div.resultDisplayUrlPane").get(0).text();
            if (firstResult.contains("wikipedia")){
            firstResult = googleResults.getElementById("webResults")
                           .select("div.resultDisplayUrlPane").get(1).text();
            }
            return this.searchByLink(firstResult);
         } catch (IOException ex) {
            Logger.getLogger(CompanySearcher.class.getName()).log(Level.SEVERE, null, ex);
         }
         catch (NullPointerException ex){
            return null;
         }
      } catch (URISyntaxException ex) {
         Logger.getLogger(CompanySearcher.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }
   
   /**
    * Searches for a match of CompanyName in the aliases names of each company in the collection
    * @param CompanyName
    * @return 
    */
   public ObjectId searchForMatchInLookUpTable(String CompanyName){
      DBCollection companies = mongo.connect(dbname, collection);      
      DBCursor cursor = companies.find();
      cursor.addOption(QUERYOPTION_NOTIMEOUT);
      while(cursor.hasNext()){
         BasicDBObject currentCompany = (BasicDBObject) cursor.next();
         HashSet<String> aliases = new HashSet<String>( 
                                       LowerCaseCollection((ArrayList<String>) 
                                                currentCompany.get("Aliases")));
         if (aliases.contains(CompanyName.toLowerCase())){
            ObjectId oid = (ObjectId) currentCompany.get("_id");
            return oid;
         }
      }
      cursor.close();
      return null;
   }
   
   /**
    * Lowercases a set of String objects
    * @param collection 
    * @returns the set of strings 
    */
   public static ArrayList LowerCaseCollection(ArrayList<String> collection){
      for (int i=0; i<collection.size(); i++){
         collection.set(i, collection.get(i).toLowerCase());
      }
      return collection;
   }
   
   /**
    * Get the domain name of a given url
    * @param url
    * @return 
    */
   public static String getDomain(String url){  
      return "\\."+url.replaceAll("http.*://", "").replaceAll("www*\\.", "").replaceAll("/.*", "").replaceAll("\\.", "\\.");
   }
   
   private HashMap<String, HashMap<String, Double>> calculateTFIDF(){
      DBCollection companies = mongo.connect(dbname, collection);
      DBCursor cursor = companies.find();
      cursor.addOption(QUERYOPTION_NOTIMEOUT);
      HashMap<String,HashMap<String, Integer>> listOfDocs = 
              new HashMap<String,HashMap<String, Integer>>();
      while (cursor.hasNext()){         
         BasicDBObject currentCompany = (BasicDBObject) cursor.next();
         HashMap doc = Tokenizer.getTokenVectorFrequency(currentCompany
                                                   .getString("Company_name"));
         listOfDocs.put(currentCompany.getString("_id"), doc);         
      }
      cursor.close();
      return TFIDFSimilarity.TF_IDF(listOfDocs);
   }
}
