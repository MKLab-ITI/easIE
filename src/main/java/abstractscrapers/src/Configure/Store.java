package abstractscrapers.src.Configure;

/**
 *
 * @author vasgat
 */
public class Store {
   public String as;
   public MongoInfo toMongo;
   public String toHardDrive;   
   
   public Store(String as, MongoInfo storeToMongo) throws Exception{
      if (as.equals(SnippetType.COMPANY_SNIPPET)||as.equals(SnippetType.ABSTRACT_SNIPPET)){
         this.as = as;
         
         if(as.equals(SnippetType.ABSTRACT_SNIPPET)){
            
            if (!storeToMongo.companies_collection.equals(null)){
               throw new Exception(
                       "\"companies_collection\" field is set in Configuration only if the type of Snippet is COMPANY_SNIPPET"
               );
            }
            else
               this.toMongo = storeToMongo;
            
         }
         else{
            
            if (storeToMongo.companies_collection.equals(null)){
               throw new NullPointerException(
                       "\"companies_collection\" is not set"
               );
            }
            else{
               this.toMongo = storeToMongo;
            }
         }
         
      }
      else{
         
         throw new Exception(
                 "Invalid SnippetType. Available Snippet Types: COMPANY_SNIPPET, ABSTRACT_SNIPPET"
         );
         
      }
   }
   
   public Store(String as, String storeToHardDrive){
      this.as = as;
      this.toHardDrive = storeToHardDrive;
   }
}
