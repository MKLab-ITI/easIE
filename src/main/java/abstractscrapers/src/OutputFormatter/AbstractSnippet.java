package abstractscrapers.src.OutputFormatter;

import abstractscrapers.src.MongoUtils;
import com.mongodb.DBObject;

/**
 *
 * @author vasgat
 */
public abstract class AbstractSnippet {
      
   abstract DBObject getSnippetDBObject();
   
   abstract void store(String dbname, String collectionm, MongoUtils mongo);
}
