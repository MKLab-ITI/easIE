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
package certh.iti.mklab.easie.metrics;

import certh.iti.mklab.easie.MongoUtils;
import com.mongodb.client.MongoCollection;
import java.net.UnknownHostException;
import java.util.Calendar;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Extends AbstractMetric and creates a Snippet object
 *
 * @author vasgat
 */
public class CompanyMetric extends AbstractMetric {

    private String name;
    private Object value;
    private String source;
    private Integer citeyear;
    private ObjectId reffered_company;
    private String brand;
    private String source_name;
    public Document json;

    /**
     * Each snippet has as characteristics:
     *
     * @param name
     * @param value
     * @param source
     * @param citation
     * @param citeyear
     * @param refCompany If any of the above characteristics are not available
     * then set as null
     */
    public CompanyMetric(String name, Object value, String source, ObjectId reffered_company, String source_name) {
        if (name == null || value == null || source == null || reffered_company == null || source_name == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.value = value;
        this.source = source;
        this.citeyear = Calendar.getInstance().get(Calendar.YEAR);
        this.reffered_company = reffered_company;
        this.brand = null;
        this.source_name = source_name;
        json = getMetricDBObject();
    }

    public void setCiteyear(int citeyear) {
        this.citeyear = citeyear;
        json.append("citeyear", citeyear);
    }

    public void setAdditionalField(String name, Object value) {
        json.append(name, value);
    }

    /**
     * Returns the snippet in JSON formulation
     *
     * @return DBObject
     */
    @Override
    protected Document getMetricDBObject() {
        json = new Document();
        json.append("referred_Company", reffered_company);
        if (brand != null) {
            json.append("referred_Brand", brand);
        }
        json.append("name", name);
        json.append("source_name", source_name);
        json.append("value", value);
        json.append("source", source);
        return json;
    }

    /**
     * Store Company Snippet to a specific collection in mongoDB
     *
     * @param snippet_collection
     * @throws UnknownHostException
     */
    @Override
    public void store(MongoCollection metrics_collection) {
        MongoUtils.insertDoc(metrics_collection, json);
    }

}
