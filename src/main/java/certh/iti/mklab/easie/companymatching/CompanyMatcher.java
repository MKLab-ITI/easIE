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

import certh.iti.mklab.easie.MongoUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * @author vasgat
 */
public class CompanyMatcher {

    private String company_name;
    private String website;
    private ObjectId company_id;
    private MongoCollection companies;
    private CompanySearcher searcher;
    private String country;

    /**
     * Creates a Company object that connects the Company Name and Website with
     * * an entry from the dataset or creates a new one
     *
     * @param company_name
     * @param country
     * @param website
     * @param companies_collection
     * @param searcher
     * @param loader
     * @throws UnknownHostException
     */
    public CompanyMatcher(String company_name, String country, String website, MongoCollection companies_collection, CompanySearcher searcher, CountryAbreviationsLoader loader) throws UnknownHostException {
        this.company_name = company_name.trim();
        if (this.website != null) {
            this.website = website.trim();
        }
        this.companies = companies_collection;
        this.searcher = searcher;
        if (country != null) {
            this.country = country.replaceAll("\\.", "");
            if (this.country.trim().length() == 2 && loader.TwoLetterABR.containsKey(this.country.trim())) {
                this.country = loader.TwoLetterABR.get(this.country.trim());
            } else if (this.country.trim().length() == 3 && loader.ThreeLetterABR.containsKey(this.country.trim())) {
                this.country = loader.ThreeLetterABR.get(this.country.trim());
            }
        }
        if (this.website != null && !this.website.equals("-") && !this.website.equals("")) {
            this.company_id = findCompanyId(this.company_name, this.website, this.country);
        } else {
            this.company_id = findCompanyId(this.company_name, this.country);
        }
        if (company_id == null) {
            company_id = insertCompany();
        }
        if (country != null) {
            insertInfo("country", this.country);
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
        long result_size = companies.count(
                new Document(
                        "_id",
                        company_id
                ).append(
                        fieldName.trim(),
                        new Document(
                                "$exists",
                                true
                        )
                )
        );
        if (result_size == 0) {
            companies.updateOne(new Document("_id", company_id),
                    new Document("$set",
                            new Document()
                                    .append(fieldName.trim(),
                                            fieldValue.trim())));
        }
    }

    /**
     * @returns the company id
     */
    public ObjectId getId() {
        return company_id;
    }

    /**
     * @returns Company's name
     */
    public String getCompanyName() {
        return company_name;
    }

    /**
     * @returns Company's website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Inserts Company to the database based on the available company name and
     * website
     *
     * @return company's id
     */
    private ObjectId insertCompany() {
        Document object = new Document();

        object.append("company_name", company_name.trim());
        if (website != null) {
            object.append("website", website.toLowerCase());
        }
        ArrayList list = new ArrayList();
        list.add(company_name.trim());
        object.append("aliases", list);
        ObjectId id = MongoUtils.insertDoc(companies, object);
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
        ObjectId tempId = searcher.search(CompanyName, CLink.toLowerCase(), Country);
        if (tempId == null) {
            tempId = searcher.search(CompanyName, Country);
            if (tempId != null && new Company(tempId, companies).getLink() == null) {
                MongoCursor<Document> tempCursor = companies
                        .find(new Document()
                                .append("_id", tempId))
                        .iterator();

                ArrayList aliases = (ArrayList) tempCursor.next().get("aliases");

                if (!aliases.contains(CompanyName)) {
                    aliases.add(CompanyName);
                    companies.updateOne(new Document("_id", tempId),
                            new Document("$set",
                                    new Document()
                                            .append("aliases",
                                                    aliases)));
                    companies.updateOne(new Document("_id", tempId),
                            new Document("$set",
                                    new Document()
                                            .append("website",
                                                    CLink.toLowerCase())));
                }
            } else {
                tempId = null;
            }
        } else {
            MongoCursor<Document> tempCursor = companies.find(new Document()
                    .append("_id", tempId)).noCursorTimeout(true).iterator();

            ArrayList aliases = (ArrayList) tempCursor.next().get("aliases");

            if (!aliases.contains(CompanyName)) {
                aliases.add(CompanyName);
                companies.updateOne(new Document("_id", tempId),
                        new Document("$set",
                                new Document()
                                        .append("aliases",
                                                aliases)));
            }
        }
        return tempId;
    }

    /**
     * Searches if the company exists to the database by having available only
     * Company's name
     *
     * @throws UnknownHostException
     * @returns company's id, if it is exists in db.
     */
    private ObjectId findCompanyId(String CompanyName, String Country) throws UnknownHostException {

        ObjectId tempId = searcher.search(CompanyName, Country);
        if (tempId != null) {
            MongoCursor<Document> tempCursor = companies.find(
                    new Document()
                            .append(
                                    "_id",
                                    tempId
                            )
            ).iterator();

            ArrayList aliases = (ArrayList) tempCursor.next()
                    .get("aliases");

            if (!aliases.contains(CompanyName)) {
                aliases.add(CompanyName);
                companies.updateOne(new Document("_id", tempId),
                        new Document("$set",
                                new Document()
                                        .append("aliases",
                                                aliases)));
            }
        }
        return tempId;
    }
}
