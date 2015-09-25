package easIE.src.Configure;

import easIE.src.Field;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Configuration Object 
 * @author vasgat
 */
public class Configuration {
   public URL url;
   public HashSet<String> bunch_urls;
   public String source_name;
   public String table_selector;
   public ArrayList<Field> snippet_fields;
   public ArrayList<Field> company_fields;
   public String nextPageSelector;
   public boolean dynamicHTML;
   public Event event;
   public Store store;
   
   public Configuration(URL url, String source_name, ArrayList<Field> fields, Store store) throws Exception{
      this.url = url;
      this.source_name = source_name;
      this.snippet_fields = fields;
      dynamicHTML = false;
      if (store.as.equals(SnippetType.ABSTRACT_SNIPPET)){
         this.store = store;
      }
      else{
         throw new Exception(
                 "Invalid Snippet Type. Snippet can not be defined as COMPANY_SNIPPET if company_fields are not defined in the Configuration File!"
         );
      }
   }
   
   public Configuration(URL url, String source_name, ArrayList<Field> fields, String nextPageSelector, Store store) throws Exception{
      this.url = url;
      this.source_name = source_name;
      this.snippet_fields = fields;
      this.nextPageSelector = nextPageSelector;
      dynamicHTML = false;
      if (store.as.equals(SnippetType.ABSTRACT_SNIPPET)){
         this.store = store;
      }
      else{
         throw new Exception(
                 "Invalid Snippet Type. Snippet can not be defined as COMPANY_SNIPPET if company_fields are not defined in the Configuration File!"
         );
      }
   }
   
   public Configuration(URL url, String source_name, String table_selector, ArrayList<Field> fields, Store store) throws Exception{
      this.url = url;
      this.source_name = source_name;
      this.table_selector = table_selector;
      this.snippet_fields = fields;      
      dynamicHTML = false;
      if (store.as.equals(SnippetType.ABSTRACT_SNIPPET)){
         this.store = store;
      }
      else{
         throw new Exception(
                 "Invalid Snippet Type. Snippet can not be defined as COMPANY_SNIPPET if company_fields are not defined in the Configuration File!"
         );
      }
   } 
   
   public Configuration(URL url, String source_name, String table_selector, ArrayList<Field> fields, String nextPageSelector, Store store) throws Exception{
      this.url = url;
      this.source_name = source_name;
      this.table_selector = table_selector;
      this.snippet_fields = fields;
      this.nextPageSelector = nextPageSelector;
      dynamicHTML = false;
      if (store.as.equals(SnippetType.ABSTRACT_SNIPPET)){
         this.store = store;
      }
      else{
         throw new Exception(
                 "Invalid Snippet Type. Snippet can not be defined as COMPANY_SNIPPET if company_fields are not defined in the Configuration File!"
         );
      }
   } 
   
   public Configuration(URL url, HashSet<String> banch_urls, String source_name, ArrayList<Field> fields, Store store) throws Exception{
      this.url = url;
      this.bunch_urls = banch_urls;
      this.source_name = source_name;
      this.snippet_fields = fields;
      this.dynamicHTML = false;
      if (store.as.equals(SnippetType.ABSTRACT_SNIPPET)){
         this.store = store;
      }
      else{
         throw new Exception(
                 "Invalid Snippet Type. Snippet can not be defined as COMPANY_SNIPPET if company_fields are not defined in the Configuration File!"
         );
      }
   }
   
   public Configuration(URL url, HashSet<String> banch_urls, String source_name, String table_selector, ArrayList<Field> fields, Store store) throws Exception{
      this.url = url;
      this.bunch_urls = banch_urls;
      this.source_name = source_name;
      this.table_selector = table_selector;
      this.snippet_fields = fields;
      this.dynamicHTML = false;
      if (store.as.equals(SnippetType.ABSTRACT_SNIPPET)){
         this.store = store;
      }
      else{
         throw new Exception(
                 "Invalid Snippet Type. Snippet can not be defined as COMPANY_SNIPPET if company_fields are not defined in the Configuration File!"
         );
      }
   }
   
   public Configuration(URL url, String source_name, String table_selector, ArrayList<Field> fields, Event event, Store store) throws IllegalConfigurationException{
      this.url = url;
      this.source_name = source_name;
      this.table_selector = table_selector;      
      this.snippet_fields = fields;
      if (dynamicHTML)
         this.event = event;
      else
         throw new IllegalConfigurationException();
      this.event = event;
      this.store = store;
   }   
   
   public Configuration(URL url, String source_name, ArrayList<Field> fields, Event event, Store store) throws Exception{
      this.url = url;
      this.source_name = source_name;
      this.snippet_fields = fields;
      if (dynamicHTML)
         this.event = event;
      else
         throw new IllegalConfigurationException();
      //this.event = event;
      if (store.as.equals(SnippetType.ABSTRACT_SNIPPET)){
         this.store = store;
      }
      else{
         throw new Exception(
                 "Invalid Snippet Type. Snippet can not be defined as COMPANY_SNIPPET if company_fields are not defined in the Configuration File!"
         );
      }
   }  
   
   public Configuration(URL url, String source_name, ArrayList<Field> company_fields, ArrayList<Field> fields, Store store){
      this.url = url;
      this.source_name = source_name;
      this.company_fields = company_fields;
      this.snippet_fields = fields;
      dynamicHTML = false;
      this.store = store;
   }
   
   public Configuration(URL url, String source_name, ArrayList<Field> company_fields, ArrayList<Field> fields, String nextPageSelector, Store store){
      this.url = url;
      this.source_name = source_name;
      this.snippet_fields = fields;
      this.company_fields = company_fields;
      this.nextPageSelector = nextPageSelector;
      dynamicHTML = false;
      this.store = store;
   }
   
   public Configuration(URL url, String source_name, String table_selector, ArrayList<Field> company_fields, ArrayList<Field> fields, Store store){
      this.url = url;
      this.source_name = source_name;
      this.table_selector = table_selector;
      this.company_fields = company_fields;
      this.snippet_fields = fields;      
      dynamicHTML = false;
      this.store = store;
   } 
   
   public Configuration(URL url, String source_name, String table_selector, ArrayList<Field> company_fields, ArrayList<Field> fields, String nextPageSelector, Store store){
      this.url = url;
      this.source_name = source_name;
      this.table_selector = table_selector;
      this.company_fields = company_fields;
      this.snippet_fields = fields;
      this.nextPageSelector = nextPageSelector;
      dynamicHTML = false;
      this.store = store;
   } 
   
   public Configuration(URL url, HashSet<String> banch_urls, String source_name, ArrayList<Field> company_fields, ArrayList<Field> fields, Store store){
      this.url = url;
      this.bunch_urls = banch_urls;
      this.source_name = source_name;
      this.company_fields = company_fields;
      this.snippet_fields = fields;
      this.dynamicHTML = false;
      this.store = store;
   }
   
   public Configuration(URL url, HashSet<String> banch_urls, String source_name, String table_selector, ArrayList<Field> company_fields, ArrayList<Field> fields, Store store){
      this.bunch_urls = banch_urls;
      this.source_name = source_name;
      this.table_selector = table_selector;
      this.company_fields = company_fields;
      this.snippet_fields = fields;
      this.dynamicHTML = false;
      this.store = store;
   }
   
   public Configuration(URL url, String source_name, String table_selector, ArrayList<Field> company_fields, ArrayList<Field> fields, Event event, boolean dynamicHTML, Store store) throws IllegalConfigurationException{
      this.url = url;
      this.source_name = source_name;
      this.table_selector = table_selector;
      this.company_fields = company_fields;
      this.snippet_fields = fields;
      if (dynamicHTML)
         this.event = event;
      else
         throw new IllegalConfigurationException();
      this.store = store;
   }   
   
   public Configuration(URL url, String source_name, ArrayList<Field> company_fields, ArrayList<Field> fields, Event event, boolean dynamicHTML, Store store) throws IllegalConfigurationException{
      this.url = url;
      this.source_name = source_name;
      this.company_fields = company_fields;
      this.snippet_fields = fields;
      if (dynamicHTML)
         this.event = event;
      else
         throw new IllegalConfigurationException();
      this.event = event;
      this.store = store;
   }   
}

