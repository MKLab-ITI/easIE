package easIE.src.Configure;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * JSONConfigurationDeserializer extends JsonDeserializer and is responsible of 
 * the deserialization of a JSON formated file into Configuration Object.
 * Required fields can be set through registerRequiredField method. 
 * @author vasgat
 */
public class JSONConfigurationDeserializer implements JsonDeserializer<Configuration>{
   List<String> requiredFields = new ArrayList<String>();

   public void registerRequiredField(String fieldName){
     requiredFields.add(fieldName);
   }  
   
   @Override
   public Configuration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonObject = (JsonObject) json;
      
      Iterator iter = jsonObject.entrySet().iterator();
      for (String fieldName : requiredFields)
      {
        if (jsonObject.get(fieldName) == null)
        {
          throw new JsonParseException("Required Field Not Found: \"" + fieldName + "\"");
        }
        if(fieldName.equals("dynamicHTML") && jsonObject.get(fieldName).getAsBoolean()){
           if(jsonObject.get("event")==null)
              throw new JsonParseException("Required Field Not Found: \"event\"");
        }
      }
      return new Gson().fromJson(json, Configuration.class);
   }
   
}
