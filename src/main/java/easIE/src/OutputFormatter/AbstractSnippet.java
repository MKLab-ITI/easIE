package easIE.src.OutputFormatter;

import easIE.src.MongoUtils;
import com.mongodb.DBObject;

/**
 * AbstractSnippet Object
 * @author vasgat
 */
public abstract class AbstractSnippet {
      
   public abstract DBObject getSnippetDBObject();
   
   public abstract void store(String dbname, String collectionm, MongoUtils mongo);
}
