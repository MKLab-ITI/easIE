package easIE.src;

import com.mongodb.BasicDBList;
import easIE.src.similarities.TFIDFSimilarity;
import com.mongodb.BasicDBObject;
import static com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import easIE.src.similarities.CosineSimilarity;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * CompanySearcher searches for a match of a company in database
 *
 * @author vasgat
 */
public class CompanySearcher {

    private String collection;
    private String dbname;
    private MongoUtils mongo;
    private HashMap<String, HashMap<String, Double>> TFIDF_weights;

    /**
     * Creates a CompanySearcher object
     *
     * @param mongo a MongoUtils object
     * @param dbname database name
     * @param collection's name
     */
    public CompanySearcher(MongoUtils mongo, String dbname, String collection) {
        this.collection = collection;
        this.dbname = dbname;
        this.mongo = mongo;
        this.TFIDF_weights = calculateTFIDF();
    }

    /**
     * Search for a company in the collection by company's website
     *
     * @param CompanyLink
     * @returns company's id, if exists to db.
     */
    public ObjectId searchByLink(String CompanyLink) {
        DBCollection companies = mongo.connect(dbname, collection);
        BasicDBObject query = new BasicDBObject();
        if (CompanyLink != null) {
            query.put("Company_Link", new BasicDBObject().append("$regex",
                    getDomain(CompanyLink))
            );
            if (companies.find(query).size() == 1) {
                DBCursor tempCursor = (DBCursor) companies.find(query);
                ObjectId tid = (ObjectId) tempCursor.next().get("_id");
                tempCursor.close();
                return tid;
            }
        }
        return null;
    }

    public ObjectId searchByLinkANDCountry(String CompanyName, String CompanyLink, String Country) {
        DBCollection companies = mongo.connect(dbname, collection);
        BasicDBObject query = new BasicDBObject();
        if (CompanyLink != null) {
            String domainName = getDomain(CompanyLink);
            if (domainName.equals("")) {
                domainName = CompanyLink.replaceAll(".*www\\.", "").replaceAll("/.*", "");
            }
            if (domainName.equals("")) {
                return null;
            }
            if ((domainName.contains("facebook.com") && !CompanyName.trim().toLowerCase().contains("facebook"))
                    || (domainName.contains("twitter.com") && !CompanyName.trim().toLowerCase().contains("twitter"))
                    || (domainName.contains("linkedin.com") && !CompanyName.trim().toLowerCase().contains("linkedin"))) {
                query.put("Company_Link", new BasicDBObject().append("$regex",
                        CompanyLink.replaceAll("http.*://", "")
                        .replaceAll("www*\\.", "").replaceAll("\\?.*", ""))
                );
            } else {
                System.out.println(domainName);
                query.put("Company_Link", new BasicDBObject().append("$regex", domainName)
                );
            }
            System.out.println(companies.find(query).size() >= 1);
            if (companies.find(query).size() >= 1) {
                DBCursor tempCursor = (DBCursor) companies.find(query);
                while (tempCursor.hasNext()) {
                    BasicDBObject comp = (BasicDBObject) tempCursor.next();
                    //System.out.println(comp.get("Company_name"));
                    if (CosineSimilarity.calculate(comp.getString("Country"), Country) >= 0.5 && CosineSimilarity.calculate(comp.getString("Company_name"), CompanyName) >= 0.7) {
                        tempCursor.close();
                        return (ObjectId) comp.get("_id");
                    }
                }
                tempCursor.close();
            }
        }
        return null;
    }

    /**
     * Searches for a company in the collection by company's name
     *
     * @param CompanyName
     * @returns company's id, if company exists to db.
     */
    public ObjectId searchByName(String CompanyName) {
        TFIDF_weights.put("candidate", Tokenizer.getTokenVectorFrequency2(CompanyName));
        Iterator it = TFIDF_weights.keySet().iterator();
        while (it.hasNext()) {
            String doc_id = (String) it.next();
            double sim = TFIDFSimilarity.calculate(
                    TFIDF_weights,
                    "candidate",
                    doc_id
            );
            if (sim >= 0.75 && !doc_id.equals("candidate")) {
                return new ObjectId(doc_id.replaceAll("_.*", ""));
            }
        }
        return null;
    }

    public ObjectId searchByNameANDCountry(String CompanyName, String Country) {
        TFIDF_weights.put("candidate", Tokenizer.getTokenVectorFrequency2(CompanyName));
        Iterator it = TFIDF_weights.keySet().iterator();
        while (it.hasNext()) {
            String doc_id = (String) it.next();
            double sim = TFIDFSimilarity.calculate(
                    TFIDF_weights,
                    "candidate",
                    doc_id
            );
            if (sim >= 0.75 && !doc_id.equals("candidate")) {
                Company company = new Company(new ObjectId(doc_id.replaceAll("_.*", "")), mongo, dbname, collection);
                if (CosineSimilarity.calculate(company.getCountry(), Country) >= 0.5) {
                    return new ObjectId(doc_id.replaceAll("_.*", ""));
                }
            }
        }
        return null;
    }

    public HashSet<ObjectId> getCandidates(String CompanyName, String Country) {
        TFIDF_weights.put("candidate", Tokenizer.getTokenVectorFrequency2(CompanyName));
        Iterator it = TFIDF_weights.keySet().iterator();
        HashSet<ObjectId> candidates = new HashSet();
        while (it.hasNext()) {
            String doc_id = (String) it.next();
            double sim = TFIDFSimilarity.calculate(
                    TFIDF_weights,
                    "candidate",
                    doc_id
            );
            if (sim >= 0.8 && !doc_id.equals("candidate")) {
                Company company = new Company(new ObjectId(doc_id.replaceAll("_.*", "")), mongo, dbname, collection);
                if (CosineSimilarity.calculate(company.getCountry(), Country) >= 0.5) {
                    candidates.add(new ObjectId(doc_id.replaceAll("_.*", "")));
                }
            }
        }
        return candidates;
    }

    /**
     * Searches for a match in the collection by candidate company's website
     * based on the results a search engine returns (search by name)
     *
     * @param CompanyName
     * @returns company's id, if company exists to db.
     */
    public Pair<ObjectId, String> searchBySearchEngineResults(String CompanyName) {
        try {
            try {
                String query = CompanyName.replace(" ", "+");
                Document googleResults = Jsoup.connect(
                        new URI("http://www.dogpile.com/search/web?q=" + query)
                        .toASCIIString()).userAgent("Mozilla/37.0").timeout(60000).get();
                String firstResult = googleResults.getElementById("webResults")
                        .select("div.resultDisplayUrlPane").get(0).text();
                if (firstResult.contains("wikipedia")) {
                    firstResult = googleResults.getElementById("webResults")
                            .select("div.resultDisplayUrlPane").get(1).text();
                    if (firstResult.contains("wikipedia")) {
                        firstResult = googleResults.getElementById("webResults")
                                .select("div.resultDisplayUrlPane").get(2).text();
                    }
                }
                return new Pair(this.searchByLink(firstResult), firstResult);
            } catch (IOException ex) {
                Logger.getLogger(CompanySearcher.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                return null;
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(CompanySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ObjectId searchBySearchEngineResultsANDCountry(String CompanyName, String Country) {
        try {
            try {
                String query = CompanyName.replace(" ", "+");
                Document googleResults = Jsoup.connect(
                        new URI("http://www.dogpile.com/search/web?q=" + query)
                        .toASCIIString()).userAgent("Mozilla/37.0").timeout(60000).get();
                String firstResult = googleResults.getElementById("webResults")
                        .select("div.resultDisplayUrlPane").get(0).text();
                if (firstResult.contains("wikipedia")) {
                    firstResult = googleResults.getElementById("webResults")
                            .select("div.resultDisplayUrlPane").get(1).text();
                    if (firstResult.contains("wikipedia")) {
                        firstResult = googleResults.getElementById("webResults")
                                .select("div.resultDisplayUrlPane").get(2).text();
                    }
                }
                return this.searchByLinkANDCountry(CompanyName, firstResult, Country);
            } catch (IOException ex) {
                Logger.getLogger(CompanySearcher.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                return null;
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(CompanySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Searches for a match of CompanyName in the aliases names of each company
     * in the collection
     *
     * @param CompanyName
     * @return
     */
    public ObjectId searchForMatchInLookUpTable(String CompanyName) {
        DBCollection companies = mongo.connect(dbname, collection);
        DBCursor cursor = companies.find();
        cursor.addOption(QUERYOPTION_NOTIMEOUT);
        while (cursor.hasNext()) {
            BasicDBObject currentCompany = (BasicDBObject) cursor.next();
            HashSet<String> aliases = new HashSet<String>(
                    LowerCaseCollection((ArrayList<String>) currentCompany.get("Aliases")));
            if (aliases.contains(CompanyName.toLowerCase())) {
                ObjectId oid = (ObjectId) currentCompany.get("_id");
                return oid;
            }
        }
        cursor.close();
        return null;
    }

    public ObjectId searchForMatchInLookUpTableANDCountry(String CompanyName, String Country) {
        DBCollection companies = mongo.connect(dbname, collection);
        DBCursor cursor = companies.find();
        cursor.addOption(QUERYOPTION_NOTIMEOUT);
        while (cursor.hasNext()) {
            BasicDBObject currentCompany = (BasicDBObject) cursor.next();
            HashSet<String> aliases = new HashSet<String>(
                    LowerCaseCollection((ArrayList<String>) currentCompany.get("Aliases")));
            if (aliases.contains(CompanyName.toLowerCase())) {
                if (CosineSimilarity.calculate(currentCompany.getString("Country"), Country) >= 0.5) {
                    ObjectId oid = (ObjectId) currentCompany.get("_id");
                    return oid;
                }
            }
        }
        cursor.close();
        return null;
    }

    /**
     * Lowercases a set of String objects
     *
     * @param collection
     * @returns the set of strings
     */
    public static ArrayList LowerCaseCollection(ArrayList<String> collection) {
        for (int i = 0; i < collection.size(); i++) {
            collection.set(i, collection.get(i).toLowerCase());
        }
        return collection;
    }

    /**
     * Get the domain name of a given url
     *
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        return url.replaceAll("http.*://", "").replaceAll("www*\\.", "").replaceAll("/.*", "").replaceAll("\\.", "\\.");
    }

    public boolean isRealCandidate(String CompanyName, ObjectId id) {
        if (id != null) {
            TFIDF_weights.put("candidate", Tokenizer.getTokenVectorFrequency2(CompanyName));
            int numOfAliases = ((BasicDBList) mongo.connect(dbname, collection).findOne(id).get("Aliases")).size();
            System.out.println("Aliases: " + numOfAliases);
            if (TFIDF_weights.get(id.toString()) != null) {
                for (int i = 0; i < numOfAliases; i++) {
                    double sim = TFIDFSimilarity.calculate(
                            TFIDF_weights,
                            "candidate",
                            id.toString() + "_" + i
                    );
                    if (sim > 0.7) {
                        System.out.println(i);
                        return true;
                    }
                }
            } else {
                BasicDBList Aliases = (BasicDBList) mongo.connect(dbname, collection).findOne(id).get("Aliases");
                for (int i = 0; i < Aliases.size(); i++) {
                    if (CompanyName.trim().toLowerCase().equals(Aliases.get(i).toString().trim().toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public HashMap calculateTFIDF() {
        DBCollection companies = mongo.connect(dbname, collection);
        DBCursor cursor = companies.find();
        cursor.addOption(QUERYOPTION_NOTIMEOUT);
        HashMap listOfDocs = new HashMap();
        while (cursor.hasNext()) {
            BasicDBObject currentCompany = (BasicDBObject) cursor.next();
            BasicDBList list = (BasicDBList) currentCompany.get("Aliases");
            for (int i = 0; i < list.size(); i++) {
                HashMap doc = Tokenizer.getTokenVectorFrequency(
                        (String) list.get(i)
                );
                doc.remove("");
                listOfDocs.put(currentCompany.get("_id").toString() + "_" + i, doc);
            }
        }
        cursor.close();
        System.out.println(listOfDocs.size());
        return TFIDFSimilarity.TF_IDF(listOfDocs);
    }
}
