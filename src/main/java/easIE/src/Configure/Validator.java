package easIE.src.Configure;

import easIE.src.Field;
import java.util.ArrayList;

/**
 * Validator Object is checking if a Configuration Object has been set properly
 * @author vasgat
 */
public class Validator {
   private static Configuration config;
   
   /**
    * Creates a Validator object
    * @param config: A Configuration object as input.
    */
   public Validator(Configuration config){
      this.config = config;
   }
   
   /**
    * This method validates, the schema of the Configuration Object 
    * that it had been given in the Construction
    * @throws IllegalConfigurationException if an Illegal configuration 
    * property has not been defined properly
    */
   public void validate() throws IllegalConfigurationException{
      if (config.url!=null){
         singlePageValidator();
         BunchValidator();
         NextPageValidator();         
         if (config.company_fields!=null){
            CompanyValidator();
            FieldsValidator(config.company_fields);
         }
         if (config.snippet_fields!=null)
            FieldsValidator(config.snippet_fields);
         if (config.event!=null)
            eventValidator();
         if(config.store!=null)
            storeValidator();
         
      }
      else{
         throw new IllegalConfigurationException(
                 new NullPointerException(
                     "null \"url\" field"
                 )
         );
      }
   }   
   
   private static void BunchValidator() throws IllegalConfigurationException{
      if(config.bunch_urls!=null&&config.url.baseURL==null){
         throw new IllegalConfigurationException("You need to define \"baseURL\" along with \"bunch_urls\"");
      }
      if (config.bunch_urls!=null&&config.url.baseURL!=null&&config.nextPageSelector!=null){
         throw new IllegalConfigurationException("You can not define \"nextPageSelector\" along with \"bunch_urls\"");
      }
   }
   
   private static void NextPageValidator() throws IllegalConfigurationException{
      if(config.nextPageSelector!=null&&config.url.baseURL==null){
         throw new IllegalConfigurationException("You need to define \"baseURL\" along with \"nextPageSelector\"");
      }
   }
   
   private static void singlePageValidator() throws IllegalConfigurationException{
      if (config.nextPageSelector==null&&config.bunch_urls==null)
         if (config.url.baseURL!=null && config.url.relativeURL==null)
            throw new IllegalConfigurationException("Besides \"baseURL\" you need to define also \"relativeURL\"");
   }
   
   private static void CompanyValidator() throws IllegalConfigurationException{
      if (config.company_fields!=null&&!config.company_fields.contains(new Field("Company Name",""))){
         throw new IllegalConfigurationException("In \"company_fields\" you need to define a field with \"FieldName\":\"Company Name\"");
      }
   }
   
   private static void FieldsValidator(ArrayList<Field> fields) throws IllegalConfigurationException{
      for (int i=0; i<fields.size(); i++){
         if (fields.get(i).FieldName==null||fields.get(i).FieldValue==null
                 ||fields.get(i).FieldValueType==null||fields.get(i).FieldNameType==null
                 ||fields.get(i).SelectorNameType==null||fields.get(i).SelectorValueType==null){
            throw new IllegalConfigurationException(new NullPointerException(
                    "In \"snippet_fields\" and \"company_fields\" you need to define all fields:"
                    + " \"FieldName\", \"FieldValue\", \"FieldNameType\", \"FieldValueType\", \"SelectorNameType\", \"SelectorValueType\""
            ));
         }
      }
   }
   
   private static void storeValidator() throws IllegalConfigurationException{
      if (config.store.toMongo!=null && config.store.toHardDrive==null){
         if (config.store.as.equals(SnippetType.COMPANY_SNIPPET)||config.store.as.equals(SnippetType.ABSTRACT_SNIPPET)){      
            if(config.store.equals(SnippetType.ABSTRACT_SNIPPET)){       
               if (config.store.toMongo.companies_collection!=null)
                  throw new IllegalConfigurationException(
                          "To store extracted data not need to define \"companies_collection\" field only \"snippets_collection\" field"
                  );         
            }
            else{            
               if (config.store.toMongo.companies_collection==null){
                  throw new IllegalConfigurationException(new NullPointerException(
                          "In order to store extracted data as COMPANY_SNIPPET you need to define \"companies_collection\" field and \"snippets_collection\" field"
                  ));
               }
               else
                  if(config.company_fields==null)
                     throw new IllegalConfigurationException(new NullPointerException(
                             "You need to define company_fields if you want to store the extracted data as COMPANY_SNIPPET"
                     ));
            }
         }      
         else{ 
            if (config.store.as==null){
               throw new IllegalConfigurationException(
                     new  NullPointerException("Define \"as\" field. Available options: COMPANY_SNIPPET, ABSTRACT_SNIPPET")
               );
            }
            throw new IllegalConfigurationException(
                    "Invalid SnippetType. Available Snippet Types: COMPANY_SNIPPET, ABSTRACT_SNIPPET"
            );         
         }
      }
      else if(config.store.toHardDrive!=null && config.store.toMongo==null){
         if (config.store.as.equals(SnippetType.COMPANY_SNIPPET))
            throw new IllegalConfigurationException("You cannot store extracted data in the hard drive as COMPANY_SNIPPET");
         else if(config.store.as.equals(SnippetType.ABSTRACT_SNIPPET))
         {
            if (config.store.toHardDrive==null)
               throw new IllegalConfigurationException(new NullPointerException(
                       "You have to define either \"storeToHardDrive\" field or \"storeToMongo\" field"
               ));
         }
      }
      else{
         throw new IllegalConfigurationException("You need to define in store field either toHardDriver field or toMongo (not both fields)");
      }
   }
   
   private static void eventValidator() throws IllegalConfigurationException{
      if (config.event.type.equals(EventType.CLICK)){
         if (config.event.selector==null){
            throw new IllegalConfigurationException(
               new NullPointerException(
                     "In the Configuration, you need to define event \"selector\" field"
                     + "in case of CLICK event to define the element in which the event will occur"
                     ));
         }
         if(!config.event.repetition_type.equals(RepetitionType.AFTER_ALL_EVENTS)
                 &&!config.event.repetition_type.equals(RepetitionType.AFTER_EACH_EVENT)){
            throw new IllegalConfigurationException(
                    "event \"repetition_type\" should be set as AFTER_ALL_EVENTS or AFTER_EACH_EVENT"
            );
         }
         if(config.event.sequence_of_events!=null||config.event.sequence_of_selectors!=null){
            throw new IllegalConfigurationException(
                    "You can not combine event \"type\" field with \"sequence_of_events\" or \"sequence_of_selectors\" fields"
            );
         }
      }
      else if(config.event.type.equals(EventType.SCROLL_DOWN)){
         if (config.event.timesToRepeat==null)
            config.event.timesToRepeat = 1;
         if (config.event.selector!=null){
            throw new IllegalConfigurationException(
                    "in SCROLL_DOWN events you dont neeed to define \"selector\" field"
            );
         }
         if(config.event.sequence_of_events!=null||config.event.sequence_of_selectors!=null){
            throw new IllegalConfigurationException(
                    "You can not combine event \"type\" field with \"sequence_of_events\" or \"sequence_of_selectors\" fields"
            );
         }
      }
      else if(config.event.type==null){
         if(config.event.sequence_of_events==null||config.event.sequence_of_selectors==null){
            throw new IllegalConfigurationException(
                    new NullPointerException(
                            "If event \"type\" field is not defined then you need to define both \"sequence_of_events\" and \"sequence_of_selectors\" fields"
                    )
            );
         }
         else{
            if(config.event.selector!=null||config.event.timesToRepeat!=null){
               throw new IllegalConfigurationException(
                       new IllegalConfigurationException(
                               "\"selector\" field and \"timesToRepeat\" field are defined on combination with \"sequence_of_events\" and \"sequence_of_selectors\" fields"
                       )
               );
            }
         }
      }
      else{
         throw new IllegalConfigurationException(
                 "False Event Type! Available Event Types: CLICK and SCROLL_DOWN"
         );
      }      
   }
   
}
