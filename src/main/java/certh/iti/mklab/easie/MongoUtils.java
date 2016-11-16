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
package certh.iti.mklab.easie;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author vasgat
 */
public final class MongoUtils {

    /**
     * Creates new mongodb client
     *
     * @param username
     * @param password
     * @param db
     * @return
     */
    public static MongoClient newClient(String server_address, String username, String password, String db) {
        List<ServerAddress> seeds = new ArrayList<ServerAddress>();
        seeds.add(new ServerAddress(server_address));
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(
                MongoCredential.createScramSha1Credential(
                        username,
                        db,
                        password.toCharArray()
                )
        );

        return new MongoClient(seeds, credentials);
    }

    /**
     * Creates new mongodb client
     *
     * @return
     */
    public static MongoClient newClient() {
        return new MongoClient();
    }

    /**
     * Connects to mongoDB and returns a specified collection
     *
     * @param mongoDB name
     * @param Collection collection
     * @return DBCollection object
     */
    public static MongoCollection connect(MongoClient client, String dbname, String cname) {
        MongoDatabase db = client.getDatabase(dbname);
        MongoCollection collection = db.getCollection(cname);
        return collection;
    }

    /**
     * Inserts a document in mongoDB
     *
     * @param mongoDB name
     * @param Collection name
     * @param document
     */
    public static ObjectId insertDoc(MongoClient mongoClient, String dbname, String cname, Document document) {
        MongoCollection collection = connect(mongoClient, dbname, cname);
        collection.insertOne(document);
        return (ObjectId) document.get("_id");
    }

    public static ObjectId insertDoc(MongoCollection collection, Document document) {
        collection.insertOne(document);
        return (ObjectId) document.get("_id");
    }
}
