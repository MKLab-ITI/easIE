package com.mycompany.abstractscrapers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
//import org.json.JSONObject;

/**
 * Creates a Snippet object
 * @author vasgat
 */
public class Snippet {
   private String name;
   private Object value;
   private String source;
   private String citation;
   private Integer citeyear;
   private ObjectId refCompany;
   private String brand;
   private String sourceName;
   private Object details;
   
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
   public Snippet(String name, Object value, String source, String citation, 
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
   
   public String getName(){
      return name;
   }
   
   public Object getValue(){
      return value;
   }
   
   public String getSource(){
      return source;
   }
   
   public String getCitation(){
      return citation;
   }
   
   public Integer getCiteyear(){
      return citeyear;
   }
   
   public ObjectId getrefCompany(){
      return refCompany;
   }
   
   public String getrefBrand(){
      return brand;
   }
   
   public String getSourceName(){
      return sourceName;
   }
   
   public Object getDetails(){
      return details;
   }
   
   /**
    * Returns the snippet in JSON formulation
    * @return JSONObject
    */
   public DBObject getSnippetDBOject(){
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
   
}
