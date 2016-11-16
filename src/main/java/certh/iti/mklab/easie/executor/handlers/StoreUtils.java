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

import certh.iti.mklab.easie.Key;
import certh.iti.mklab.easie.companymatching.CompanyMatcher;
import certh.iti.mklab.easie.companymatching.CompanySearcher;
import certh.iti.mklab.easie.companymatching.CountryAbreviationsLoader;
import certh.iti.mklab.easie.metrics.CompanyMetric;
import certh.iti.mklab.easie.metrics.GenericMetric;
import com.mongodb.client.MongoCollection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author vasgat
 */
public class StoreUtils {

    private List<HashMap> extracted_company_info;
    private List<HashMap> extracted_metrics;
    private int number_of_metrics;

    public StoreUtils(List<HashMap> extracted_companies, List<HashMap> extracted_metrics) {
        this.extracted_company_info = extracted_companies;
        this.extracted_metrics = extracted_metrics;
    }

    public void toMongoDB(MongoCollection companies_collection, MongoCollection metrics_collection, String source_name) throws UnknownHostException, IOException {

        CompanySearcher searcher = new CompanySearcher(companies_collection);
        CountryAbreviationsLoader loader = new CountryAbreviationsLoader();

        for (int i = 0; i < extracted_company_info.size(); i++) {
            HashMap company_info = new HashMap(extracted_company_info.get(i));

            company_info.remove("source");

            if (!company_info.containsKey("company_name")) {
                continue;
            }
            CompanyMatcher matcher = matcher = new CompanyMatcher(
                    (String) company_info.get("company_name"),
                    (String) company_info.get("country"),
                    (String) company_info.get("website"),
                    companies_collection,
                    searcher,
                    loader
            );

            company_info.remove("company_name");
            company_info.remove("country");
            company_info.remove("website");

            Iterator iter = company_info.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String, Object> entry
                        = (Map.Entry<String, Object>) iter.next();

                matcher.insertInfo(
                        entry.getKey(),
                        entry.getValue().toString()
                );
            }

            HashMap metrics = new HashMap(extracted_metrics.get(i));

            String source = (String) metrics.get("source");
            metrics.remove("source");

            Iterator metrics_iterator = metrics.entrySet().iterator();
            while (metrics_iterator.hasNext()) {

                Map.Entry<Object, Object> metric
                        = (Map.Entry<Object, Object>) metrics_iterator.next();

                CompanyMetric company_metric = new CompanyMetric(
                        (String) ((Key) metric.getKey()).getX(),
                        metric.getValue(),
                        source,
                        matcher.getId(),
                        source_name
                );

                company_metric.setCiteyear((int) ((Key) metric.getKey()).getY());

                company_metric.store(
                        metrics_collection
                );
            }
        }
    }

    public void toMongoDB(MongoCollection metrics_collection) {

        if (extracted_company_info != null) {
            for (int i = 0; i < extracted_company_info.size(); i++) {
                HashMap company_info = new HashMap(extracted_company_info.get(i));

                company_info.remove("source");

                HashMap metrics = new HashMap();
                metrics.putAll(extracted_metrics.get(i));

                String source = (String) metrics.get("source");
                metrics.remove("source");

                Iterator metrics_iterator = metrics.entrySet().iterator();
                while (metrics_iterator.hasNext()) {

                    Map.Entry<Object, Object> metric
                            = (Map.Entry<Object, Object>) metrics_iterator.next();

                    GenericMetric generic_metric = new GenericMetric(
                            (String) ((Key) metric.getKey()).getX(),
                            metric.getValue(),
                            source,
                            (Integer) ((Key) metric.getKey()).getY()
                    );

                    Iterator iter = company_info.entrySet().iterator();

                    while (iter.hasNext()) {
                        Map.Entry<String, Object> entry
                                = (Map.Entry<String, Object>) iter.next();

                        generic_metric.setAdditionalField(
                                (String) entry.getKey(),
                                entry.getValue().toString()
                        );
                    }

                    generic_metric.store(metrics_collection);
                }
            }
        } else {
            for (int i = 0; i < extracted_metrics.size(); i++) {
                HashMap metrics = new HashMap();
                metrics.putAll(extracted_metrics.get(i));
                String source = (String) metrics.get("source");
                metrics.remove("source");

                Iterator it = metrics.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry<Key, Object> entry = (Map.Entry<Key, Object>) it.next();

                    GenericMetric generic_metric = new GenericMetric(
                            (String) entry.getKey().getX(),
                            entry.getValue(),
                            source,
                            (Integer) entry.getKey().getY()
                    );
                    generic_metric.store(metrics_collection);
                }

            }
        }
    }

    public void toFile(String filePath) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(filePath);
        writer.println(exportJson());
        writer.close();
    }

    /**
     * @returns the extracted data into JSON format
     */
    public String exportJson() {
        JSONArray results = new JSONArray();
        number_of_metrics = 0;

        for (int j = 0; j < extracted_company_info.size(); j++) {

            HashMap company_info = new HashMap();

            JSONObject current_object = new JSONObject();

            if (extracted_company_info.isEmpty()) {
                continue;
            }
            company_info = new HashMap(extracted_company_info.get(j));

            if (company_info.get("company_name") == null) {
                continue;
            }
            company_info.remove("source");

            Iterator iter = company_info.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String, Object> entry
                        = (Map.Entry<String, Object>) iter.next();

                current_object.append(
                        entry.getKey(),
                        entry.getValue().toString()
                );
            }

            HashMap metrics = new HashMap(extracted_metrics.get(j));

            String source = (String) metrics.get("source");
            metrics.remove("source");

            JSONArray array_of_metrics = new JSONArray();
            Iterator metrics_iterator = metrics.entrySet().iterator();
            while (metrics_iterator.hasNext()) {
                number_of_metrics++;
                Map.Entry<Object, Object> metric
                        = (Map.Entry<Object, Object>) metrics_iterator.next();

                GenericMetric generic_metric = new GenericMetric(
                        (String) ((Key) metric.getKey()).getX(),
                        metric.getValue(),
                        source,
                        (Integer) ((Key) metric.getKey()).getY()
                );
                array_of_metrics.put(new JSONObject(generic_metric.json.toJson()));
            }
            current_object.append("metrics", array_of_metrics);
            results.put(current_object);
        }

        if (extracted_company_info.size() == 0) {
            JSONArray array_of_metrics = new JSONArray();

            for (int j = 0; j < extracted_metrics.size(); j++) {
                HashMap metrics = new HashMap(extracted_metrics.get(j));

                String source = (String) metrics.get("source");
                metrics.remove("source");

                Iterator metrics_iterator = metrics.entrySet().iterator();
                while (metrics_iterator.hasNext()) {

                    number_of_metrics++;
                    Map.Entry<Object, Object> metric
                            = (Map.Entry<Object, Object>) metrics_iterator.next();

                    GenericMetric generic_metric = new GenericMetric(
                            (String) ((Key) metric.getKey()).getX(),
                            metric.getValue(),
                            source,
                            (Integer) ((Key) metric.getKey()).getY()
                    );
                    array_of_metrics.put(new JSONObject(generic_metric.json.toJson()));
                }
            }
            results.put(array_of_metrics);
        }

        return results.toString(4);
    }

    public int getNumberOfExtractedMetrics() {
        if (number_of_metrics == 0) {
            exportJson();
        }
        return number_of_metrics;
    }
}
