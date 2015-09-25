package easIE.src.Configure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * ConfigurationFileReader Object reads a JSON file or a String and transforms it to Configuration Object and validates it
 * @author vasgat
 */
public class ConfigurationFileReader {
    private String content;
    Configuration config;
   
    /**
     * ConfigurationFileReader Object constructor that created Configuration Object 
     * from a JSON file and validates it.
     * @param configurationFile: Configuration 
     * @throws FileNotFoundException in case of not valid file path
     * @throws IllegalConfigurationException in case of invalid Configuration schema
     */
   public ConfigurationFileReader(File configurationFile) throws FileNotFoundException, IllegalConfigurationException{
      this.content = new Scanner(configurationFile).useDelimiter("\\Z").next();
      exec();
   }
   
   public ConfigurationFileReader(String content) throws IllegalConfigurationException{
      this.content = content;
      exec();
   }
   
   private void exec() throws IllegalConfigurationException{
      GsonBuilder gsonBuilder = new GsonBuilder();
      JSONConfigurationDeserializer deserializer = new JSONConfigurationDeserializer();
      deserializer.registerRequiredField("url");
      deserializer.registerRequiredField("snippet_fields");
      deserializer.registerRequiredField("source_name");
      deserializer.registerRequiredField("dynamicHTML");
      gsonBuilder.registerTypeAdapter(Configuration.class, deserializer);
      Gson gson = gsonBuilder.setPrettyPrinting().create();
      config = gson.fromJson(content, Configuration.class);
      Validator VALIDATOR = new Validator(config);
      VALIDATOR.validate();
   }
   
   public Configuration getConfiguration(){
      return config;
   }
}
