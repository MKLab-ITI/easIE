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

import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author vasgat
 */
public class Company {

    private String name;
    private String country;
    private String link;
    private ObjectId id;
    private ArrayList Aliases;

    public Company(ObjectId _id, MongoCollection companies) {
        Document dbcompany = (Document) companies.find(
                new Document(
                        "_id",
                        _id))
                .iterator().next();
        id = _id;
        name = dbcompany.getString("company_name");
        link = dbcompany.getString("website");
        country = dbcompany.getString("country");
        Aliases = (ArrayList) dbcompany.get("aliases");
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public String getCountry() {
        return country;
    }

    public ObjectId getID() {
        return id;
    }

    public ArrayList getAliases() {
        return Aliases;
    }
}
