package abstractscrapers.src;

import abstractscrapers.src.Configure.Configuration;
import abstractscrapers.src.Configure.ConfigurationFileReader;
import abstractscrapers.src.Executor.ScraperExecutor;
import java.io.IOException;
import java.net.URISyntaxException;
/**
 *
 * @author vasgat
 */
public class main {
   public static void main(String[] args) throws URISyntaxException, IOException, Exception{  
      ConfigurationFileReader reader = new ConfigurationFileReader("C:/Users/vasgat/Desktop/ConfigurationFile.json");
      Configuration config = reader.getConfiguration();
      ScraperExecutor executor = new ScraperExecutor(config);      
      executor.StoreScrapedInfo();
   }
}
