package easIE.src;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.bson.types.ObjectId;

/**
 *
 * @author vasgat
 */
public class Company {
    private String name;
    private String country;
    private String link;
    private ObjectId ID;
    
    public Company(ObjectId id, MongoUtils mongo, String dbname, String CompaniesCollection){
        DBCollection companies = mongo.connect(dbname, CompaniesCollection);
        BasicDBObject dbcompany = (BasicDBObject) companies.findOne(id);
        ID = id;
        name = dbcompany.getString("Company_name");
        link = dbcompany.getString("Company_Link");
        country = dbcompany.getString("Country");
    }
    
    public String getName(){
        return name;
    }
    
    public String getLink(){
        return link;
    }
    
    public String getCountry(){
        return country;
    }
    
    public ObjectId getID(){
        return ID;
    }
}
