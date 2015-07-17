package abstractscrapers.src.OutputFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author vasgat
 */
public class JSONFormatter {
   
   public static JSONObject toJSONObject(HashMap fields){
      JSONObject object = new JSONObject();
      Iterator iterator = fields.entrySet().iterator();
      while(iterator.hasNext()){
         Map.Entry entry = (Map.Entry) iterator.next();
         if(entry.getValue() instanceof ArrayList){ 
            object.put((String) entry.getKey(), toJSONArray2((ArrayList) entry.getValue()));
         }
         else{
            object.put((String) entry.getKey(), entry.getValue().toString());
         }
      }
      return object;
   }
   
   public static JSONArray toJSONArray(ArrayList fieldsList){
      JSONArray dbList = new JSONArray();
      for(int i=0; i<fieldsList.size(); i++){
         dbList.put(toJSONObject((HashMap) fieldsList.get(i)));
      }
      return dbList;
   }
   
      public static JSONArray toJSONArray2(ArrayList listField){
         JSONArray dbList = new JSONArray();
         for(int i=0; i<listField.size(); i++){
            dbList.put(listField.get(i));
         }
         return dbList;
      }
}
