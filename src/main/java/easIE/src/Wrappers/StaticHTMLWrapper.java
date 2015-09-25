package easIE.src.Wrappers;

import easIE.src.Field;
import easIE.src.FieldType;
import easIE.src.SelectorType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * StaticHTMLWrapper Object extends AbstractWrapper and is responsible 
 * for extracting data from defined fields (by css selectors) from a static webpage or fields from a table.
 * @author vasgat
 */
public class StaticHTMLWrapper extends AbstractWrapper{
   public String baseURL;
   public String relativeURL;
   private String source;
   public Document document;
   private String init_baseURL;
   private String init_relativeURL;
   private Document init_document;
   
   /**
    * Creates a new StaticHTMLWrapper for a webpage
    * @param baseURL: webpage base url
    * @param relativeURL: path to the specific spot in the page
    * @throws URISyntaxException
    * @throws IOException 
    */
   public StaticHTMLWrapper(String baseURL, String relativeURL) throws URISyntaxException, IOException{
      this.baseURL = baseURL;
      this.source = baseURL+relativeURL;
      this.document = Jsoup.connect(new URI(source).toASCIIString())
                        .userAgent("Mozilla/37.0").timeout(60000).get(); 
      this.init_baseURL = baseURL;
      this.init_relativeURL = relativeURL;
      this.init_document = document;
   }
   
   /**
    * Creates a new StaticHTMLWrapper for a webpage
    * @param FullLink webpage full url
    * @throws URISyntaxException
    * @throws IOException 
    */
   public StaticHTMLWrapper(String FullLink) throws URISyntaxException, IOException{
      this.source = FullLink;
      try{
      this.document = Jsoup.connect(new URI(source).toASCIIString())
                        .userAgent("Mozilla/37.0").timeout(60000).get();  
      }catch(HttpStatusException ex){
         System.out.println(ex.getMessage());
      }
   }
   
   /**
    * extracts data from a list of specified fields from a webpage
    * @param fields: list of fields
    * @return a HashMap of the extracted fields
    * @throws Exception 
    */
   @Override
   public ArrayList<HashMap> extractFields(List<Field> fields){      
      HashMap<String, Object> ExtractedFields = new HashMap<String, Object>();
      if (document!=null){
         for (int i=0; i<fields.size(); i++){
            Pair<String, Object> pair = getSelectedElement(fields.get(i), document);
            String tempName = pair.getKey();
            Object tempValue = pair.getValue();
            if(fields.get(i).ReplaceInName!=null&&fields.get(i).ReplaceInName.regex.size()==fields.get(i).ReplaceInName.with.size()){
               for (int j=0; j<fields.get(i).ReplaceInName.regex.size(); j++){
                  tempName = tempName.replaceAll(
                          fields.get(i).ReplaceInName.regex.get(j), 
                          fields.get(i).ReplaceInName.with.get(j)
                  );
               }
            }
            if(fields.get(i).ReplaceInValue!=null&&fields.get(i).ReplaceInValue.regex.size()==fields.get(i).ReplaceInValue.with.size()){
               for (int j=0; j<fields.get(i).ReplaceInValue.regex.size(); j++){
                  tempValue = ((String) tempValue).replaceAll(
                          fields.get(i).ReplaceInValue.regex.get(j), 
                          fields.get(i).ReplaceInValue.with.get(j)
                  );
               }
            }
            ExtractedFields.put(tempName, tempValue);
         }
         ExtractedFields.put("source", source);
         if (!ExtractedFields.containsKey("citeyear")){
            ExtractedFields.put(
                    "citeyear",
                    Calendar.getInstance().get(Calendar.YEAR)
            );
         }
         ExtractedFields.values().removeAll(Collections.singleton(""));
         ExtractedFields.values().removeAll(Collections.singleton(null));
      }
      ArrayList<HashMap> result = new ArrayList<HashMap>();
      result.add(ExtractedFields);
      return result;
      
   }
   
   /**
    * extracts data from the specified table fields
    * @param tableSelector: CSS table selector
    * @param fields: list of table fields
    * @return an ArrayList of HashMap (corresponds to the extracted table fields)
    * @throws Exception in case of unknown field type
    */
   @Override
   public ArrayList<HashMap<String, Object>> extractTable(String tableSelector, List<Field> fields){
      ArrayList<HashMap<String, Object>> extractedTableFields = new ArrayList();
      if (document!=null){
         Elements table = document.select(tableSelector);   
         for (int i=0; i<table.size(); i++){
            extractedTableFields.add(extractTableFields(fields, table.get(i)));
         }      
      }
      return extractedTableFields;
   }
   
   /**
    * private function which extracts columns of a table row
    * @param fields/columns of the table
    * @param element row of the table
    * @return a HashMap of the extracted fields
    * @throws Exception 
    */
   private HashMap<String, Object> extractTableFields(List<Field> fields, Element element){
      HashMap<String, Object> ExtractedFields = new HashMap<String, Object>();
      for (int i=0; i<fields.size(); i++){
         Pair<String, Object> pair = getSelectedElement(fields.get(i), element);
         String tempName = pair.getKey();
         Object tempValue = pair.getValue();
         if(fields.get(i).ReplaceInName!=null&&fields.get(i).ReplaceInName.regex.size()==fields.get(i).ReplaceInName.with.size()){
            for (int j=0; j<fields.get(i).ReplaceInName.regex.size(); j++){
               tempName = tempName.replaceAll(
                       fields.get(i).ReplaceInName.regex.get(j), 
                       fields.get(i).ReplaceInName.with.get(j)
               );
            }
         }
         if(fields.get(i).ReplaceInValue!=null&&fields.get(i).ReplaceInValue.regex.size()==fields.get(i).ReplaceInValue.with.size()){
            for (int j=0; j<fields.get(i).ReplaceInValue.regex.size(); j++){
               tempValue = ((String) tempValue).replaceAll(
                       fields.get(i).ReplaceInValue.regex.get(j), 
                       fields.get(i).ReplaceInValue.with.get(j)
               );
            }
         }
         ExtractedFields.put(tempName, tempValue);
      }
      ExtractedFields.put("source", source);
      if (!ExtractedFields.containsKey("citeyear")){
         ExtractedFields.put(
                 "citeyear", 
                 Calendar.getInstance().get(Calendar.YEAR)
         );
      }
      ExtractedFields.values().removeAll(Collections.singleton(""));
      ExtractedFields.values().removeAll(Collections.singleton(null));
      return ExtractedFields;
   }   
   
   /**
    * extracts a list of values of a specific field
    * @param listSelecto CSS selector of list field
    * @return an ArrayList of values
    */
   private ArrayList extractList(String listSelector){
      ArrayList list = new ArrayList();
      Elements elements = document.select(listSelector);
      for (int i=0; i<elements.size(); i++){
         list.add(elements.get(i).text());
      }
      return list;
   }
   
   /**
    * reset document, baseURL and relativeURL to the initial values, 
    * they had when the object created
    */
   public void reset(){
      this.document = init_document;
      this.baseURL = init_baseURL;
      this.relativeURL = init_relativeURL;
   }
   
   /**
    * Returns the content of a specific field in the document
    * @param field 
    * @param element
    * @return a Pair of String, Object that corresponds to field name and field value accordingly
    */
   private Pair<String, Object> getSelectedElement(Field field, Element element){
      String tempName;
      Object tempValue;
      if (field.SelectorNameType.equals(SelectorType.rawtext)){
            tempName = field.FieldName;
         }
         else{
            if (field.FieldNameType.equals(FieldType.text)){
               tempName = element.select(field.FieldName).text();
            }
            else if (field.FieldNameType.equals(FieldType.link)){
               tempName = element.select(field.FieldName).attr("href");
            }
            else if ((field.FieldNameType.equals(FieldType.image))){
               tempName = element.select(field.FieldName).attr("src");
            }
            else{
               tempName = element.select(field.FieldName).attr(field.FieldValueType);
            }
         }
         if (field.SelectorValueType.equals(SelectorType.rawtext)){
            tempValue = field.FieldValue;
         }
         else{
            if (field.FieldValueType.equals(FieldType.text)){
               tempValue = element.select(field.FieldValue).text();
            }
            else if (field.FieldValueType.equals(FieldType.link)){
               tempValue = element.select(field.FieldValue).attr("href");
            }
            else if ((field.FieldValueType.equals(FieldType.image))){
               tempValue = element.select(field.FieldValue).attr("src");
            }
            else if (field.FieldValueType.equals(FieldType.list)){
               tempValue = extractList(field.FieldValue);
            }
            else{
               tempValue = element.select(field.FieldValue).attr(field.FieldValueType);
            }
         }
         return new Pair(tempName, tempValue);
   }
}
