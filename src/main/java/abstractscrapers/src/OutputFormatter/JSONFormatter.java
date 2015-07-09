package abstractscrapers.src.OutputFormatter;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author vasgat
 */
public class JSONFormatter {
   
   public static BasicDBObject toJSONObject(HashMap fields){
      BasicDBObject object = new BasicDBObject();
      Iterator iterator = fields.entrySet().iterator();
      while(iterator.hasNext()){
         Map.Entry entry = (Map.Entry) iterator.next();
         object.append((String) entry.getKey(), entry.getValue());
      }
      return object;
   }
   
   public static BasicDBList toJSONArray(ArrayList fieldsList){
      BasicDBList dbList = new BasicDBList();
      for(int i=0; i<fieldsList.size(); i++){
         dbList.add(toJSONObject((HashMap) fieldsList.get(i)));
      }
      return dbList;
   }
}
