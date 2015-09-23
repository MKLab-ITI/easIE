package abstractscrapers.src;

import abstractscrapers.src.Configure.ConfigurationFileReader;
import abstractscrapers.src.Configure.IllegalConfigurationException;
import abstractscrapers.src.Executors.ScraperExecutor;
import abstractscrapers.src.Executors.SnippetHandler;
import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author vasgat
 */
public class main {
   public static void main(String[] args) throws FileNotFoundException, IllegalConfigurationException, Exception{     
      ConfigurationFileReader reader = new ConfigurationFileReader(new File("src/main/java/ConfigurationFiles/BCorporationConfigurationFile.json"));
      ScraperExecutor executor = new ScraperExecutor(reader.getConfiguration());    

      SnippetHandler handler = new SnippetHandler(executor.getCompanyFields(), executor.getSnippetFields());
      System.out.println(handler.getJSON());
   }
}
