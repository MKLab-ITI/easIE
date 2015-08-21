package abstractscrapers.src.OutputFormatter;

import abstractscrapers.src.MongoUtils;
import com.mongodb.DBObject;

/**
 *
 * @author vasgat
 */
public abstract class AbstractSnippet {
      
   public abstract DBObject getSnippetDBObject();
   
   public abstract void store(String dbname, String collectionm, MongoUtils mongo);
}
