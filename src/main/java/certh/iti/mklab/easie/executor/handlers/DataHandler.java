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
package certh.iti.mklab.easie.executor.handlers;

import com.mongodb.MongoClient;
import certh.iti.mklab.easie.MongoUtils;
import certh.iti.mklab.easie.configuration.Configuration.Store;
import com.mongodb.client.MongoCollection;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

/**
 * DataHandler transforms the extracted data into Snippet Objects and stores
 * them into a mongodb or in the drive
 *
 * @author vasgat
 */
public class DataHandler {

    private List<HashMap> extracted_company_info;
    private List<HashMap> extracted_metrics;
    private StoreUtils storeUtils;

    /**
     * Creates SnippetHandler Object
     *
     * @param extracted_company_info: extracted company fields
     * @param extracted_metrics: extracted snippet fields
     * @throws Exception
     */
    public DataHandler(List<HashMap> extracted_company_info, List<HashMap> extracted_metrics) throws Exception {
        this.extracted_company_info = extracted_company_info;
        this.extracted_metrics = extracted_metrics;
        storeUtils = new StoreUtils(extracted_company_info, extracted_metrics);
    }

    /**
     * Stores the extracted data (companies_fields and extracted_metrics) into
     * mongodb or drive
     *
     * @param store contains information about where the data are going to be
     * stored
     * @param sourceName the data were collected
     * @throws UnknownHostException
     * @throws FileNotFoundException
     * @throws Exception
     */
    public void store(Store store, String source_name) throws UnknownHostException, FileNotFoundException, Exception {

        if (store.companies_collection != null && extracted_company_info != null) {
            MongoClient client;
            if (store.db_credentials != null) {
                client = MongoUtils.newClient(
                        store.db_credentials.server_address,
                        store.db_credentials.username,
                        store.db_credentials.password,
                        store.db_credentials.db
                );
            } else {
                client = MongoUtils.newClient();
            }

            MongoCollection companies_collection = MongoUtils.connect(client, store.database, store.companies_collection);
            MongoCollection metrics_collection = MongoUtils.connect(client, store.database, store.metrics_collection);

            storeUtils.toMongoDB(companies_collection, metrics_collection, source_name);

            client.close();

        } else if (store.companies_collection == null && store.metrics_collection != null) {
            MongoClient client;

            if (store.db_credentials != null) {
                client = MongoUtils.newClient(
                        store.db_credentials.server_address,
                        store.db_credentials.username,
                        store.db_credentials.password,
                        store.db_credentials.db
                );
            } else {
                client = MongoUtils.newClient();
            }

            MongoCollection metrics_collection = MongoUtils.connect(client, store.database, store.metrics_collection);

            storeUtils.toMongoDB(metrics_collection);

            client.close();
        } else {
            storeUtils.toFile(store.hd_path);
        }
    }

    public String exportJson() {
        return storeUtils.exportJson();
    }

    public int getNumberOfExtractedMetrics() {
        return storeUtils.getNumberOfExtractedMetrics();
    }
    
}
