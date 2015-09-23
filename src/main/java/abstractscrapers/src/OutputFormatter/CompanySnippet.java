package abstractscrapers.src.OutputFormatter;

import abstractscrapers.src.MongoUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.net.UnknownHostException;
import org.bson.types.ObjectId;

/**
 * Extends AbstractSnippet and creates a Snippet object
 * @author vasgat
 */
public class CompanySnippet extends AbstractSnippet{
   public String name;
   public Object value;
   public String source;
   public String citation;
   public Integer citeyear;
   public ObjectId refCompany;
   public String brand;
   public String sourceName;
   public Object details;
   
/**
 * Each snippet has as characteristics:
 * @param name
 * @param value
 * @param source
 * @param citation
 * @param citeyear
 * @param refCompany 
 * If any of the above characteristics are not available then set as null
 */
   public CompanySnippet(String name, Object value, String source, String citation, 
                           Integer citeyear, ObjectId refCompany, String brand,
                                                String sourceName, Object details){
      if (name==null||value==null||source==null||refCompany==null||sourceName==null){
         throw new NullPointerException();
      }
      this.name = name;
      this.value = value;
      this.source = source;
      this.citation = citation;
      this.citeyear = citeyear;
      this.refCompany = refCompany;
      this.brand = brand;
      this.sourceName = sourceName;
      this.details = details;
   }
   
   /**
    * Returns the snippet in JSON formulation
    * @return DBObject
    */
   @Override
   public DBObject getSnippetDBObject(){
      BasicDBObject json = new BasicDBObject();
      json.append("referred_Company", refCompany);
      if (brand!=null)
         json.append("referred_Brand", brand);      
      json.append("name", name);
      json.append("source_name", sourceName);
      json.append("value", value);
      json.append("source", source);
      if (citation!=null)
         json.append("citation", citation);
      if (citeyear!=null)
         json.append("citeyear", citeyear);
      if (details!=null)
         json.append("details", details);
      
      return json;
   }
   
   /**
    * Store Company Snippet to a specific collection in mongoDB
    * @param dbname database name
    * @param collection name
    * @throws UnknownHostException 
    */
   @Override
   public void store(String dbname, String collection, MongoUtils mongo){
      mongo.insertDoc(dbname, collection, getSnippetDBObject());
   }
}
