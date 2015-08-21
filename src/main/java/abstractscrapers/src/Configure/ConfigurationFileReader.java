package abstractscrapers.src.Configure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author vasgat
 */
public class ConfigurationFileReader {
   Configuration config;
   
   public ConfigurationFileReader(String filePath) throws FileNotFoundException{
      String content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      config = gson.fromJson(content, Configuration.class);
   }
   
   public Configuration getConfiguration(){
      return config;
   }
}
