package com.mycompany.abstractscrapers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author vasgat
 */
public class main {
   public static void main(String[] args) throws URISyntaxException, IOException, Exception{
      String first = "tr:nth-child(1)";
      String last = "tr:nth-child(10)";
      
      Document document = Jsoup.connect(new URI("http://guide.ethical.org.au/company/?company=5362").toASCIIString())
                        .userAgent("Mozilla/37.0").timeout(60000).get(); 
      
      Elements table = document.select(".boxCompanyInfo > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(5) > td:nth-child(2) > div:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr");
      
      for (int i=0; i<table.size(); i++){
         System.out.println(table.get(i).select("td:nth-child(2)").text());
      }
      
      System.out.println(first.compareTo(last));      
      
      Field field = new Field(
              "Sector",
              "td:nth-child(2)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      
      Field field2 = new Field(
              "Participant",
              "td:nth-child(1)", 
              SelectorType.rawtext, 
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      
      List<Field> toScrape = new ArrayList<Field>();
      toScrape.add(field);
      toScrape.add(field2);
      
      Scraper scraper = new Scraper("https://www.unglobalcompact.org/participation/report/cop/create-and-submit/active");
      System.out.println(scraper.scrapeTable("tbody > tr",toScrape));
   }
}
