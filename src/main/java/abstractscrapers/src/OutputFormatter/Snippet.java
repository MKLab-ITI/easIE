package abstractscrapers.src.OutputFormatter;

import abstractscrapers.src.MongoUtils;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Snippet extends AbstractSnippet 
 * @author vasgat
 */
public class Snippet extends AbstractSnippet{
   private HashMap Info;
   
   /**
    * Creates a Snippet Object
    * @param Info: a Set of key,values that constitute the snippet
    */
   public Snippet(HashMap Info){
      this.Info = Info;
   }

   /**
    * Transforms Info that snippet contains into JSON format
    * @returns DBObject
    */
   @Override
   public DBObject getSnippetDBObject() {
      BasicDBObject snippet = new BasicDBObject();
      Iterator it = Info.entrySet().iterator();
      while(it.hasNext()){
         Map.Entry entry = (Map.Entry) it.next();
         if (entry.getValue() instanceof ArrayList){
            ArrayList temp_list = (ArrayList) entry.getValue();
            BasicDBList list = new BasicDBList();
            for (int i=0; i<temp_list.size(); i++){
               if (temp_list.get(i) instanceof HashMap){
                  BasicDBObject subdoc = new BasicDBObject();
                  HashMap tempHashMap = (HashMap) temp_list.get(i);
                  Iterator subit = tempHashMap.entrySet().iterator();
                  while(subit.hasNext()){
                     Map.Entry subentry = (Map.Entry) subit.next();
                     subdoc.append((String) subentry.getKey(), subentry.getValue());
                  }
                  list.add(subdoc);
               }
               else{
                  list.add(temp_list.get(i));
               }
            }
            snippet.append((String) entry.getKey(), list);
         }
         else if(entry.getValue() instanceof HashMap){
            BasicDBObject subdoc = new BasicDBObject();
            HashMap tempHashMap = (HashMap) entry.getValue();
            Iterator subit = tempHashMap.entrySet().iterator();
            while(subit.hasNext()){
               Map.Entry subentry = (Map.Entry) subit.next();
               subdoc.append((String) subentry.getKey(), subentry.getValue());
            }
            snippet.append((String) entry.getKey(), subdoc);
         }
         else{
            snippet.append((String) entry.getKey(), entry.getValue());
         }
      }
      return snippet;
   }
   
   /**
    * Store Snippet on mongodb database in JSON format
    * @param dbname
    * @param collection
    * @param mongo 
    */
   @Override
   public void store(String dbname, String collection, MongoUtils mongo) {
      mongo.insertDoc(dbname, collection, getSnippetDBObject());
   }
   
}
