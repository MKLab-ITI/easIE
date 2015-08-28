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
            if (storeToMongo.companies_collection==null)
               throw new Exception("To store scraped data not need to define \"companies_collection\" field only \"snippets_collection\" field");
            else
               this.toMongo = storeToMongo;            
         }
         else{            
            if (storeToMongo.companies_collection==null){
               throw new NullPointerException(
                       "In order to store scraped data as COMPANY_SNIPPET you need to define \"companies_collection\" field and \"snippets_collection\" field"
               );
            }
            else{
               this.toMongo = storeToMongo;
            }
         }         
      }
      else{ 
         if (as==null){
            throw new NullPointerException("Define \"as\" field. Available options: COMPANY_SNIPPET, ABSTRACT_SNIPPET");
         }
         throw new Exception(
                 "Invalid SnippetType. Available Snippet Types: COMPANY_SNIPPET, ABSTRACT_SNIPPET"
         );         
      }
   }
   
   public Store(String as, String storeToHardDrive) throws Exception{
      this.as = as;
      if (as.equals(SnippetType.COMPANY_SNIPPET))
         throw new Exception("You cannot store scraped data in the hard drive as COMPANY_SNIPPET");
      else if(as.equals(SnippetType.ABSTRACT_SNIPPET))
      {
         if (storeToHardDrive!=null)
            this.toHardDrive = storeToHardDrive;
         else
            throw new NullPointerException("You have to define either \"storeToHardDrive\" field or \"storeToMongo\" field");
      }
   }
}
