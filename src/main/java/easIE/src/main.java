package easIE.src;

import easIE.src.Configure.ConfigurationFileReader;
import easIE.src.Configure.IllegalConfigurationException;
import easIE.src.Executors.WrapperExecutor;
import easIE.src.Executors.SnippetHandler;
import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author vasgat
 */
public class main {
   public static void main(String[] args) throws FileNotFoundException, IllegalConfigurationException, Exception{     
      ConfigurationFileReader reader = new ConfigurationFileReader(new File("src/main/java/ConfigurationFiles/BCorporationConfigurationFile.json"));
      WrapperExecutor executor = new WrapperExecutor(reader.getConfiguration());    

      SnippetHandler handler = new SnippetHandler(executor.getCompanyFields(), executor.getSnippetFields());
      System.out.println(handler.getJSON());
   }
}
