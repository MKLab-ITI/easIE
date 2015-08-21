package abstractscrapers.src.Configure;

/**
 *
 * @author vasgat
 */
public class MongoInfo {
   public String dbname;
   public String companies_collection;
   public String snippets_collection;
   
   public MongoInfo(String dbname, String CompaniesCollection, String SnippetsCollection){
      
      if (!dbname.equals(null)&&!CompaniesCollection.equals(null)&&!SnippetsCollection.equals(null)){
         
         this.dbname = dbname;
         this.companies_collection = CompaniesCollection;
         this.snippets_collection = SnippetsCollection;
         
      }
      else{
         
         throw new NullPointerException(
                 "dbname,companies_collection and snippets_collection fields can not be null!"
         );
         
      }      
   }
   
   public MongoInfo(String dbname, String SnippetsCollection){
      
      if (!dbname.equals(null)&&!SnippetsCollection.equals("null")){
         
         this.dbname = dbname;
         this.snippets_collection = SnippetsCollection;
         
      }
      else{
         
         throw new NullPointerException(
                 "dbname and snippets_collection fields can not be null!"
         );
         
      }
   }   
}

