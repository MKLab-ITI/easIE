package com.mycompany.abstractscrapers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Scraper Object is responsible for scraping defined fields (by css selectors)
 * from a webpage or fields from a table.
 * @author vasgat
 */
public class Scraper {
   private String source;
   private Document document;
   
   /**
    * Creates a new scraper for link webpage
    * @param link: webpage url
    * @throws URISyntaxException
    * @throws IOException 
    */
   public Scraper(String link) throws URISyntaxException, IOException{
      this.source = link;
      this.document = Jsoup.connect(new URI(link).toASCIIString())
                        .userAgent("Mozilla/37.0").timeout(60000).get(); 
   }
   
   /**
    * scrapes a list of specified fields from a webpage
    * @param fields: list of fields
    * @return a HashMap of the scraped fields
    * @throws Exception 
    */
   public HashMap scrapeFields(List<Field> fields) throws Exception{
      HashMap<String, String> ScrapedFields = new HashMap<String, String>();
      for (int i=0; i<fields.size(); i++){
         String tempName;
         String tempValue;
         if (fields.get(i).SelectorNameType.equals(SelectorType.rawtext)){
            tempName = fields.get(i).FieldName;
         }
         else{
            if (fields.get(i).FieldNameType.equals(FieldType.text)){
               tempName = document.select(fields.get(i).FieldName).text();
            }
            else if (fields.get(i).FieldNameType.equals(FieldType.link)){
               tempName = document.select(fields.get(i).FieldName).attr("href");
            }
            else if ((fields.get(i).FieldNameType.equals(FieldType.image))){
               tempName = document.select(fields.get(i).FieldName).attr("src");
            }
            else{
               throw new Exception("Unknown Field Type");
            }
         }
         if (fields.get(i).SelectorValueType.equals(SelectorType.rawtext)){
            tempValue = fields.get(i).FieldValue;
         }
         else{
            if (fields.get(i).FieldValueType.equals(FieldType.text)){
               tempValue = document.select(fields.get(i).FieldValue).text();
            }
            else if (fields.get(i).FieldValueType.equals(FieldType.link)){
               tempValue = document.select(fields.get(i).FieldValue).attr("href");
            }
            else if ((fields.get(i).FieldValueType.equals(FieldType.image))){
               tempValue = document.select(fields.get(i).FieldValue).attr("src");
            }
            else{
               throw new Exception("Unknown Field Type");
            }
         }
         ScrapedFields.put(tempName, tempValue);
      }
      return ScrapedFields;
   }
   
   /**
    * scrapes a table
    * @param tableSelector: CSS table selector
    * @param fields: list of table fields
    * @return an ArrayList of HashMap (corresponds to the scraped table fields)
    * @throws Exception in case of unknown field type
    */
   public ArrayList<HashMap<String, String>> scrapeTable(String tableSelector, List<Field> fields) throws Exception{
      ArrayList<HashMap<String, String>> scrapedTableFields = new ArrayList();
      Elements table = document.select(tableSelector);
      for (int i=0; i<table.size(); i++){
         scrapedTableFields.add(scrapeTableFields(fields, table.get(i)));
      }
      return scrapedTableFields;
   }
   
   /**
    * private function which scrapes a list of table fields from a table row
    * @param fields of the table
    * @param element row of the table
    * @return a HashMap of scraped fields
    * @throws Exception 
    */
   private HashMap<String, String> scrapeTableFields(List<Field> fields, Element element) throws Exception{
      HashMap<String, String> ScrapedFields = new HashMap<String, String>();
      for (int i=0; i<fields.size(); i++){
         String tempName;
         String tempValue;
         if (fields.get(i).SelectorNameType.equals(SelectorType.rawtext)){
            tempName = fields.get(i).FieldName;
         }
         else{
            if (fields.get(i).FieldNameType.equals(FieldType.text)){
               tempName = element.select(fields.get(i).FieldName).text();
            }
            else if (fields.get(i).FieldNameType.equals(FieldType.link)){
               tempName = element.select(fields.get(i).FieldName).attr("href");
            }
            else if ((fields.get(i).FieldNameType.equals(FieldType.image))){
               tempName = element.select(fields.get(i).FieldName).attr("src");
            }
            else{
               throw new Exception("Unknown Field Type");
            }
         }
         if (fields.get(i).SelectorValueType.equals(SelectorType.rawtext)){
            tempValue = fields.get(i).FieldValue;
         }
         else{
            if (fields.get(i).FieldValueType.equals(FieldType.text)){
               tempValue = element.select(fields.get(i).FieldValue).text();
            }
            else if (fields.get(i).FieldValueType.equals(FieldType.link)){
               tempValue = element.select(fields.get(i).FieldValue).attr("href");
            }
            else if ((fields.get(i).FieldValueType.equals(FieldType.image))){
               tempValue = element.select(fields.get(i).FieldValue).attr("src");
            }
            else{
               throw new Exception("Unknown Field Type");
            }
         }
         ScrapedFields.put(tempName, tempValue);
      }
      return ScrapedFields;
   }   
}
