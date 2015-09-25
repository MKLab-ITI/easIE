package easIE.src.Wrappers;

import easIE.src.Field;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jsoup.Jsoup;

/**
 * PaginationItearator object extends AbstractWrapper and is responsible for extracting content 
 * that is distributed to different pages
 * @author vasgat
 */
public class PaginationIterator extends AbstractWrapper{
   private String nextPageSelector;
   private StaticHTMLWrapper wrapper;
   
   /**
    * Creates a new PaginationIterator
    * @param wrapper StaticHTMLWrapper object of an Instance page
    * @param nextPageSelector next Page CSS selector in the page
    * @throws URISyntaxException
    * @throws IOException 
    */
   public PaginationIterator(StaticHTMLWrapper wrapper, String nextPageSelector) throws URISyntaxException, IOException{
      this.nextPageSelector = nextPageSelector; 
      this.wrapper = wrapper;
   }
   
   /**
    * Extracts data from the defined fields of each page until no next page exists
    * @param fields: List of fields we want to extract
    * @return the extracted data fields as a List of HashMaps
    * @throws URISyntaxException
    * @throws IOException
    * @throws Exception 
    */
   @Override
   public ArrayList<HashMap> extractFields(List<Field> fields) throws URISyntaxException, IOException, Exception{
      ArrayList<HashMap> extractedFields = new ArrayList();
      extractedFields.addAll(wrapper.extractFields(fields));
      while(!wrapper.document.select(nextPageSelector).attr("href").equals("")){         
         wrapper.document = Jsoup.connect(new URI(wrapper.baseURL+wrapper.document.select(nextPageSelector).attr("href").replace(wrapper.baseURL, "")).toASCIIString())
                              .userAgent("Mozilla/37.0").timeout(60000).get(); 
         extractedFields.addAll(wrapper.extractFields(fields));
      }
      wrapper.reset();
      return extractedFields;
   }
   
   /**
    * Extracts data from a table from each page until no next page exists
    * @param tableSelector CSS table selector 
    * @param fields List of table fields we want to extract
    * @return the extracted table fields as a List of HashMaps
    * @throws URISyntaxException
    * @throws IOException
    * @throws Exception 
    */
   @Override
   public ArrayList<HashMap> extractTable(String tableSelector, List<Field> fields) throws URISyntaxException, IOException, Exception{
      ArrayList<HashMap> extractedFields = new ArrayList();
      extractedFields.addAll(wrapper.extractTable(tableSelector, fields));
      while(!wrapper.document.select(nextPageSelector).attr("href").equals("")){         
         wrapper.document = Jsoup.connect(new URI(wrapper.baseURL+wrapper.document.select(nextPageSelector).attr("href").replace(wrapper.baseURL, "")).toASCIIString())
                              .userAgent("Mozilla/37.0").timeout(60000).get();  
         extractedFields.addAll(wrapper.extractTable(tableSelector, fields));
      }  
      wrapper.reset();
      return extractedFields;
   }
}
