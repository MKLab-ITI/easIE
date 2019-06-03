/*
 * Copyright 2016 vasgat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package certh.iti.mklab.easie.companymatching;

import certh.iti.mklab.easie.MapFunctionsUtils;
import certh.iti.mklab.jSimilarity.documentUtils.CompanyDocument;
import certh.iti.mklab.jSimilarity.documentUtils.Corpus;
import certh.iti.mklab.jSimilarity.stringsimilarities.CosineSimilarity;
import certh.iti.mklab.jSimilarity.tfidf.TFIDF;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.collections4.Predicate;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author vasgat
 */
public class CompanySearcher {

    private MongoCollection companies;
    private TFIDF tfidf;
    private Corpus corpus;

    /**
     * Creates a CompanySearcher object
     */
    public CompanySearcher(MongoCollection companies_collection) {
        this.companies = companies_collection;
        buildCorpus();
        tfidf = new TFIDF(corpus);
        tfidf.calculate();
    }

    public ObjectId search(String company_name, String country, String website) {

        Document query = new Document();

        if (website == null) {
            return null;
        }

        String domainName = getDomain(website);
        if (domainName.equals("")) {
            domainName = website.replaceAll(".*www\\.", "").replaceAll("/.*", "");
        }

        if (domainName.equals("")) {
            return null;
        }

        if ((domainName.contains("facebook.com") && !company_name.trim().toLowerCase().contains("facebook"))
                || (domainName.contains("twitter.com") && !company_name.trim().toLowerCase().contains("twitter"))
                || (domainName.contains("linkedin.com") && !company_name.trim().toLowerCase().contains("linkedin"))) {

            query.put("website", new Document().append("$regex",
                    website.replaceAll("http.*://", "")
                    .replaceAll("www*\\.", "").replaceAll("\\?.*", ""))
            );
        } else {
            query.put(
                    "website",
                    new Document().append("$regex", domainName)
            );
        }

        CosineSimilarity cosine = new CosineSimilarity();
        CompanyDocument input_company = new CompanyDocument.Builder(company_name).build();

        if (companies.count(query) >= 1) {
            MongoCursor<Document> tempCursor = companies.find(query).noCursorTimeout(true).iterator();
            while (tempCursor.hasNext()) {
                Document current_company = (Document) tempCursor.next();
                CompanyDocument document = new CompanyDocument.Builder(current_company.getString("company_name")).build();

                double similarity = cosine.calculate(
                        input_company.BagOfWords,
                        document.BagOfWords
                );

                if (current_company.getString("country").equals(country) && similarity >= 0.7) {
                    tempCursor.close();
                    return (ObjectId) current_company.get("_id");
                }
            }
            tempCursor.close();
        }
        return null;
    }

    public ObjectId search(String company_name, String country) {

        double threshold = 0.8;

        CompanyDocument document = new CompanyDocument.Builder(company_name)
                .id("candidate")
                .country(country)
                .build();

        tfidf.calculate(document);

        HashMap<String, Double> candidates = new HashMap();

        Predicate predicate = new Predicate<CompanyDocument>() {
            @Override
            public boolean evaluate(CompanyDocument object) {
                if (country == null && object.country == null) {
                    return true;
                }

                if (object.country == null) {
                    return false;
                }
                return object.country.equals(country);
            }
        };

        Iterator<CompanyDocument> it = corpus.iterator(predicate);

        int i = 0;
        while (it.hasNext()) {
            CompanyDocument company = it.next();

            double similarity = tfidf.similarity("candidate", company.id);

            if (similarity >= threshold && !company.equals(document)) {
                candidates.put(company.id, similarity);
            }
        }

        if (candidates.size() > 0) {
            String id = (String) MapFunctionsUtils.getTopValues2(candidates, 1).keySet().iterator().next();
            return new ObjectId(id.replaceAll("_.*", ""));
        }

        Document query = new Document("aliases", company_name).append("country", country);
        Document c = (Document) companies.find(query).iterator().tryNext();
        if (c != null) {
            return c.getObjectId("_id");
        } else {
            return null;
        }
    }

        public ObjectId search(String company_name) {

        double threshold = 0.9;

        CompanyDocument document = new CompanyDocument.Builder(company_name)
                .id("candidate")
                .build();

        tfidf.calculate(document);

        HashMap<String, Double> candidates = new HashMap();

        Iterator<CompanyDocument> it = corpus.iterator();

        while (it.hasNext()) {
            CompanyDocument company = it.next();

            double similarity = tfidf.similarity("candidate", company.id);

            if (similarity >= threshold && !company.equals(document)) {
                candidates.put(company.id, similarity);
            }
        }

        if (candidates.size() > 0) {
            String id = (String) MapFunctionsUtils.getTopValues2(candidates, 1).keySet().iterator().next();
            return new ObjectId(id.replaceAll("_.*", ""));
        }

        Document query = new Document("aliases", company_name);
        Document c = (Document) companies.find(query).iterator().tryNext();
        if (c != null) {
            return c.getObjectId("_id");
        } else {
            return null;
        }
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

    private void buildCorpus() {

        corpus = new Corpus();

        MongoCursor<Document> cursor = companies.find()
                .noCursorTimeout(true).iterator();

        while (cursor.hasNext()) {

            Document company = cursor.next();
            ArrayList list = (ArrayList) company.get("aliases");

            for (int i = 0; i < list.size(); i++) {
                CompanyDocument document = new CompanyDocument.Builder(list.get(i).toString())
                        .id(company.getObjectId("_id").toString() + "_" + i)
                        .country(company.getString("country"))
                        .build();

                corpus.addDocument(document);
            }
        }
        cursor.close();
    }
}
