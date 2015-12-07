package easIE.src;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import easIE.src.similarities.CosineSimilarity;
import java.net.UnknownHostException;
import org.bson.types.ObjectId;

/**
 * @author vasgat
 */
public class CompanyMatcher {

    private String CompanyName;
    private String CompanyLink;
    private ObjectId CompanyId;
    private String dbname;
    private String collection;
    private MongoUtils mongo;
    private CompanySearcher searcher;
    private String Country;

    /**
     * Creates a Company object that connects the Company Name and Website with
     * an entry from the dataset or creates a new one
     *
     * @param CompanyName the name of the Company
     * @param mongo a MongoUtils object
     * @param CompanyLink the Website of the company
     * @param dbname database's name
     * @param collection's name
     * @param searcher A CompanySearcher object
     */
    public CompanyMatcher(String CompanyName, String Country, MongoUtils mongo, String CompanyLink, String dbname, String collection, CompanySearcher searcher, CountryAbreviationsLoader loader) {
        this.CompanyName = CompanyName.trim();
        this.CompanyLink = CompanyLink.trim();
        this.dbname = dbname;
        this.collection = collection;
        this.mongo = mongo;
        this.searcher = searcher;
        if (Country != null) {
            this.Country = Country.replaceAll("\\.", "");
            if (Country.trim().length() == 2 && loader.TwoLetterABR.containsKey(Country.trim())) {
                this.Country = loader.TwoLetterABR.get(Country.trim());
            } else if (Country.trim().length() == 3 && loader.ThreeLetterABR.containsKey(Country.trim())) {
                this.Country = loader.ThreeLetterABR.get(Country.trim());
            }
        }
        this.CompanyId = findCompanyId(this.CompanyName, this.CompanyLink, this.Country);
        if (CompanyId == null) {
            System.out.println("isNull");
            CompanyId = insertCompany();
        }
        if (Country != null) {
            insertInfo("Country", this.Country);
        }
    }

    /**
     * Creates a Company Object that connects the Company name with an entry
     * from the companies collection or creates a new entry.
     *
     * @param CompanyName CompanyName the name of the Company
     * @param mongo a MongoUtils object
     * @param dbname database's name
     * @param collection's name
     * @param searcher A CompanySearcher object
     * @throws UnknownHostException
     */
    public CompanyMatcher(String CompanyName, String Country, MongoUtils mongo, String dbname, String collection, CompanySearcher searcher, CountryAbreviationsLoader loader) throws UnknownHostException {
        this.CompanyName = CompanyName.trim();
        this.dbname = dbname;
        this.collection = collection;
        this.mongo = mongo;
        this.searcher = searcher;
        if (Country != null) {
            this.Country = Country.replaceAll("\\.", "");
            if (Country.trim().length() == 2 && loader.TwoLetterABR.containsKey(Country.trim())) {
                this.Country = loader.TwoLetterABR.get(Country.trim());
            } else if (Country.trim().length() == 3 && loader.ThreeLetterABR.containsKey(Country.trim())) {
                this.Country = loader.ThreeLetterABR.get(Country.trim());
            }
        }
        this.CompanyId = findCompanyId(this.CompanyName, this.Country);
        if (CompanyId == null) {
            CompanyId = insertCompany();
        }
        if (Country != null) {
            insertInfo("Country", this.Country);
        }
    }

    /**
     * This method inserts a field with extra infomation for the company in the
     * database
     *
     * @param fieldName
     * @param fieldValue
     */
    public void insertInfo(String fieldName, String fieldValue) {
        DBCollection companies = mongo.connect(dbname, collection);
        DBCursor result = companies.find(new BasicDBObject("_id", CompanyId)
                .append(fieldName.trim(), new BasicDBObject("$exists", true)));
        if (result.size() == 0) {
            companies.update(new BasicDBObject("_id", CompanyId),
                    new BasicDBObject("$set",
                            new BasicDBObject()
                            .append(fieldName.trim(),
                                    fieldValue.trim())));
        }
    }

    /**
     * @returns the company id
     */
    public ObjectId getId() {
        return CompanyId;
    }

    /**
     * @returns Company's name
     */
    public String getCompanyName() {
        return CompanyName;
    }

    /**
     * @returns Company's website
     */
    public String getCompanyLink() {
        return CompanyLink;
    }

    /**
     * Inserts Company to the database based on the available company name and
     * website
     *
     * @return company's id
     */
    private ObjectId insertCompany() {
        BasicDBObject object = new BasicDBObject();
        object.append("Company_name", CompanyName);
        if (CompanyLink != null) {
            object.append("Company_Link", CompanyLink.toLowerCase());
        }
        BasicDBList list = new BasicDBList();
        list.add(CompanyName);
        object.append("Aliases", list);
        ObjectId id = mongo.insertDoc(dbname, collection, object);
        return id;
    }

    /**
     * Searches if the company exists to the database by having available
     * company's website
     *
     * @param CLink company's website
     * @returns company's id if the company exists to database
     */
    private ObjectId findCompanyId(String CompanyName, String CLink, String Country) {
        ObjectId tempId = searcher.searchByLinkANDCountry(CompanyName, CLink.toLowerCase(), Country);
        if (tempId == null) {
            System.out.println("SearchByName");
            tempId = searcher.searchByNameANDCountry(CompanyName, Country);
            if (tempId != null && new Company(tempId, mongo, dbname, collection).getLink() == null) {
                DBCollection companies = mongo.connect(dbname, collection);
                DBCursor tempCursor = companies.find(new BasicDBObject()
                        .append("_id", tempId));
                BasicDBList aliases = (BasicDBList) tempCursor.next()
                        .get("Aliases");
                if (!aliases.contains(CompanyName)) {
                    aliases.add(CompanyName);
                    companies.update(new BasicDBObject("_id", tempId),
                            new BasicDBObject("$set",
                                    new BasicDBObject()
                                    .append("Aliases",
                                            aliases)));
                    companies.update(new BasicDBObject("_id", tempId),
                            new BasicDBObject("$set",
                                    new BasicDBObject()
                                    .append("Company_Link",
                                            CLink.toLowerCase())));
                }
            }
            else{
                tempId=null;
            }
        } else {
            DBCollection companies = mongo.connect(dbname, collection);
            DBCursor tempCursor = companies.find(new BasicDBObject()
                    .append("_id", tempId));
            BasicDBList aliases = (BasicDBList) tempCursor.next().get("Aliases");
            if (!aliases.contains(CompanyName)) {
                aliases.add(CompanyName);
                companies.update(new BasicDBObject("_id", tempId),
                        new BasicDBObject("$set",
                                new BasicDBObject()
                                .append("Aliases",
                                        aliases)));
            }
        }
        return tempId;
    }

    /**
     * Searches if the company exists to the database by having available only
     * Company's name
     *
     * @returns company's id, if it is exists in db.
     * @throws UnknownHostException
     */
    private ObjectId findCompanyId(String CompanyName, String Country) throws UnknownHostException {
        System.out.println("searchBySearchEngineResults");
        ObjectId tempId = searcher.searchBySearchEngineResultsANDCountry(CompanyName, Country);
        boolean isCandidate = false;
        if (tempId != null) {
            System.out.println(CompanyName);
            isCandidate = searcher.isRealCandidate(
                    CompanyName,
                    tempId
            );
        }
        if (tempId == null || !isCandidate) {
            System.out.println("SearchByName");
            tempId = searcher.searchByNameANDCountry(CompanyName, Country);
            if (tempId != null) {
                DBCollection companies = mongo.connect(dbname, collection);
                DBCursor tempCursor = companies.find(new BasicDBObject()
                        .append("_id", tempId));
                BasicDBList aliases = (BasicDBList) tempCursor.next()
                        .get("Aliases");
                if (!aliases.contains(CompanyName)) {
                    aliases.add(CompanyName);
                    companies.update(new BasicDBObject("_id", tempId),
                            new BasicDBObject("$set",
                                    new BasicDBObject()
                                    .append("Aliases",
                                            aliases)));
                }
            }
        } else {
            DBCollection companies = mongo.connect(dbname, collection);
            DBCursor tempCursor = companies.find(new BasicDBObject()
                    .append("_id", tempId));
            BasicDBList aliases = (BasicDBList) tempCursor.next().get("Aliases");
            if (!aliases.contains(CompanyName)) {
                aliases.add(CompanyName);
                companies.update(new BasicDBObject("_id", tempId),
                        new BasicDBObject("$set",
                                new BasicDBObject()
                                .append("Aliases",
                                        aliases)));
            }
        }
        return tempId;
    }
}
