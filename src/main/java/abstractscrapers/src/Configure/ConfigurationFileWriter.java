package abstractscrapers.src.Configure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author vasgat
 */
public class ConfigurationFileWriter {
   Configuration config;
   
   public ConfigurationFileWriter(Configuration config){
      this.config = config;
   }
   
   public void storeConfigurationFile(String filePath) throws FileNotFoundException{
      Gson gson = new GsonBuilder().setPrettyPrinting().create();      
      PrintWriter writer = new PrintWriter(filePath);
      writer.println(gson.toJson(config));
      writer.close();
   }
}
