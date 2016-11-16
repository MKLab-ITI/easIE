package certh.iti.mklab.easie.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author vasgat
 */
public class ConfigurationWriter {
   Configuration config;
   
   /**
    * ConfigurationFileWriter Constructor
    * @param config: Configuration Object
    */
   public ConfigurationWriter(Configuration config){
      this.config = config;
   }
   
   /**
    * Stores the defined Configuration Object into drive in JSON format
    * @param filePath: the path that points out where the JSON file will be stored
    * @throws FileNotFoundException if the given file path does not exist
    */
   public void storeConfigurationFile(String filePath) throws FileNotFoundException{
      Gson gson = new GsonBuilder().setPrettyPrinting().create();      
      PrintWriter writer = new PrintWriter(filePath);
      writer.println(gson.toJson(config));
      writer.close();
   }    
}
