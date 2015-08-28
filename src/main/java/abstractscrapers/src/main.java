package abstractscrapers.src;

import abstractscrapers.src.Configure.Configuration;
import abstractscrapers.src.Configure.ConfigurationFileReader;
import abstractscrapers.src.Executor.ScraperExecutor;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
/**
 *
 * @author vasgat
 */
public class main {
   public static void main(String[] args) throws URISyntaxException, IOException, Exception{ 
      ConfigurationFileReader reader = new ConfigurationFileReader("D:\\NetBeans\\AbstractScrapers\\src\\main\\java\\ConfigurationFiles\\EUtransparencyLinksConfig.json");
      Configuration config = reader.getConfiguration();
      
      ConfigurationFileReader reader2 = new ConfigurationFileReader("D:\\NetBeans\\AbstractScrapers\\src\\main\\java\\ConfigurationFiles\\EUtransparencyConfig.json");
      Configuration config2 = reader2.getConfiguration(); 
      
      char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
      
      for (int i=0; i<alphabet.length; i++){
         HashSet<String> bunch = new HashSet<String>();
         config.url.relativeURL = "/transparencyregister/public/consultation/listlobbyists.do?letter="+alphabet[i]+"&alphabetName=LatinAlphabet";
         System.out.println(alphabet[i]);
         ScraperExecutor executor = new ScraperExecutor(config);
         System.out.println(executor.getSnippetFields());
         for (int j=0; j<executor.getSnippetFields().size(); j++){
            bunch.add((String) executor.getSnippetFields().get(j).get("link"));
         }
         config2.bunch_urls = bunch;
         ScraperExecutor executor2 = new ScraperExecutor(config2);   
         System.out.println(executor2.getCompanyFields());
         System.out.println(executor2.getSnippetFields());
         executor2.StoreScrapedInfo(); 
      }
      /*ConfigurationFileReader reader = new ConfigurationFileReader("D:\\NetBeans\\AbstractScrapers\\src\\main\\java\\ConfigurationFiles\\EUtransparencyConfig.json");
      Configuration config = reader.getConfiguration();
      ScraperExecutor executor = new ScraperExecutor(config);    
      ArrayList<HashMap> clink = executor.getSnippetFields();
      System.out.println(executor.getCompanyFields());
      System.out.println(executor.getSnippetFields());
      /*HashSet links = new HashSet();
      for (int i=0; i<clink.size(); i++){
         links.add(clink.get(i).get("link"));
      }
      System.out.println(links);
      */
      //executor.StoreScrapedInfo(); 
   }
}
