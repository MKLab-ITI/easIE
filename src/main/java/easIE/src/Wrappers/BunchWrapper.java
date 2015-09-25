package easIE.src.Wrappers;

import easIE.src.Field;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * BunchWrapper object extends AbstractWrapper and is responsible for extracting data 
 * from a set of links - webpages with the same structure
 * @author vasgat
 */
public class BunchWrapper extends AbstractWrapper{
   private HashSet<String> BunchOfLinks;
   private String baseURL;
   
   /**
    * Creates a new BunchWrapper
    * @param BunchOfLinks a set of webpages
    */
   public BunchWrapper(HashSet<String> BunchOfLinks){
      this.baseURL = "";
      this.BunchOfLinks = BunchOfLinks;
   }

   public BunchWrapper(HashSet<String> BunchOfLinks, String baseURL){
      this.baseURL = baseURL;
      this.BunchOfLinks = BunchOfLinks;
   }
   /**
    * Extracts specified field for each webpage
    * @param fields set of fields
    * @return the extracted Fields
    * @throws URISyntaxException
    * @throws IOException
    * @throws Exception 
    */
   @Override
   public ArrayList<HashMap> extractFields(List<Field> fields) throws URISyntaxException, IOException, Exception{
      ArrayList<HashMap> extractedFields = new ArrayList();
      System.out.println(BunchOfLinks);
      Iterator links = BunchOfLinks.iterator();
      int i=0;
      while(links.hasNext()){
         System.out.println(i++);
         StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL+((String) links.next()).replace(baseURL, ""));
         extractedFields.addAll(wrapper.extractFields(fields));
      }
      return extractedFields;
   }
   
   /**
    * Extracts data from a table 
    * @param tableSelector
    * @param fields
    * @return the extracted table fields for each page
    * @throws URISyntaxException
    * @throws IOException
    * @throws Exception 
    */
   @Override
   public ArrayList<HashMap> extractTable(String tableSelector, List<Field> fields) throws URISyntaxException, IOException, Exception{
      ArrayList<HashMap> extractedFields = new ArrayList();
      Iterator links = BunchOfLinks.iterator();
      int i=0;
      while(links.hasNext()){
         System.out.println(i++);
         StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL+((String) links.next()).replace(baseURL, ""));
         extractedFields.addAll(wrapper.extractTable(tableSelector, fields));
      }
      return extractedFields;
   }   
}
