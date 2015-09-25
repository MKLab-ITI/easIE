package easIE.src;

import static com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.types.ObjectId;

/**
 * This class is responsible for connecting with MongoDB
 * @author vasgat
 */
public class MongoUtils {
    public MongoClient mongoClient;

    /**
     * Creates a client in MongoDB
     */
    public MongoUtils(){
        try {
           mongoClient = new MongoClient();
           mongoClient.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
        } catch (UnknownHostException ex) {
            Logger.getLogger(MongoUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Connects to mongoDB and returns a specified collection
     * @param mongoDB name
     * @param Collection collection
     * @return DBCollection object
     */
    public DBCollection connect(String mongoDB, String Collection){        
        DB db = mongoClient.getDB(mongoDB);
        DBCollection collection = db.getCollection(Collection);
        collection.addOption(QUERYOPTION_NOTIMEOUT);
        return collection;
    }
    
    /**
     * Inserts a document in mongoDB
     * @param mongoDB name
     * @param Collection name
     * @param document 
     */
    public ObjectId insertDoc(String mongoDB, String Collection, DBObject document){
        DBCollection collection = connect(mongoDB, Collection);
        System.out.println(document);
        collection.insert(document);
        return (ObjectId) document.get("_id");
    }

    
    public void closeConnection(){       
        mongoClient.close();
    }
}
