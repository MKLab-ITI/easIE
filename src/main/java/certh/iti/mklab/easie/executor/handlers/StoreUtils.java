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

import certh.iti.mklab.easie.companymatching.CompanyMatcher;
import certh.iti.mklab.easie.companymatching.CompanySearcher;
import certh.iti.mklab.easie.companymatching.CountryAbreviationsLoader;
import com.mongodb.client.MongoCollection;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

/**
 * @author vasgat
 */
public class StoreUtils {

    private List<Document> extracted_company_info;
    private List<ArrayList<Document>> extracted_metrics;
    private int number_of_metrics;
    private String entity_name;

    public StoreUtils(List<Document> extracted_companies, List<ArrayList<Document>> extracted_metrics, String entity_name) {
        this.extracted_company_info = extracted_companies;
        this.extracted_metrics = extracted_metrics;
        this.entity_name = entity_name;
    }

    public void toMongoDB(MongoCollection companies_collection, MongoCollection metrics_collection, String source_name) throws UnknownHostException, IOException {

        CompanySearcher searcher = new CompanySearcher(companies_collection);
        CountryAbreviationsLoader loader = new CountryAbreviationsLoader();

        for (int i = 0; i < extracted_company_info.size(); i++) {
            if (extracted_company_info.get(i).size() == 0) {
                continue;
            }
            Document company_info = extracted_company_info.get(i);

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

            Iterator iter = company_info.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String, Object> entry
                        = (Map.Entry<String, Object>) iter.next();
                if (!entry.getKey().equals("company_name")
                        && !entry.getKey().equals("country")
                        && !entry.getKey().equals("website")) {

                    matcher.insertInfo(
                            entry.getKey(),
                            entry.getValue().toString()
                    );
                }
            }

            ArrayList<Document> metrics = extracted_metrics.get(i);

            Iterator<Document> metrics_iterator = metrics.iterator();
            while (metrics_iterator.hasNext()) {

                Document metric = metrics_iterator.next();

                metric.append("company_id", matcher.getId()).append("source_name", source_name);

                metrics_collection.insertOne(metric);
                metric.remove("company_id");
                metric.remove("_id");
            }
        }
    }

    public void toMongoDB(MongoCollection metrics_collection) {

        if (extracted_company_info != null) {
            number_of_metrics = 0;

            for (int j = 0; j < extracted_company_info.size(); j++) {

                Document company = extracted_company_info.get(j);

                ArrayList metrics = new ArrayList();

                ArrayList<Document> temp_metrics = extracted_metrics.get(j);
                for (int i = 0; i < temp_metrics.size(); i++) {
                    if (!temp_metrics.get(i).getString("name").equals("crawl_to")) {
                        number_of_metrics++;
                        metrics.add(temp_metrics.get(i));
                    }
                }

                Document json = new Document().append("company", company).append("metrics", metrics);

                metrics_collection.insertOne(json);
            }

        } else {
            for (int j = 0; j < extracted_metrics.size(); j++) {
                ArrayList<Document> temp_metrics = extracted_metrics.get(j);

                Document json = new Document();
                for (int i = 0; i < temp_metrics.size(); i++) {
                    if (!temp_metrics.get(i).getString("name").equals("crawl_to")) {
                        number_of_metrics++;
                        json.append(temp_metrics.get(i).getString("name"), temp_metrics.get(i).getString("value"));
                    }
                }
                json.append("sourccoe", temp_metrics.get(0).getString("source"));
                json.append("citeyear", temp_metrics.get(0).getString("citeyear"));
                metrics_collection.insertOne(json);
            }
        }
    }

    public void toJSONFile(String filePath) throws IOException {
        PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath), "UTF-8"));
        writer.write(exportJson());

        writer.close();
        /*PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath), "UTF-8"));*/

        //writer.println(exportJson());
        //writer.close();
    }

    public void toCSVFile(String filePath, String metric_designer) throws
            FileNotFoundException, UnsupportedEncodingException, IOException {
        PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath), "UTF-8"));

        writer.println(exportCSV(metric_designer));
        writer.close();
    }

    /**
     * @returns the extracted data into JSON format
     */
    public String exportJson() {
        ArrayList<Document> results = new ArrayList<>();

        if (extracted_company_info != null) {

            for (int j = 0; j < extracted_company_info.size(); j++) {
                if (extracted_company_info.isEmpty()) {
                    continue;
                }

                Document company = extracted_company_info.get(j);

                ArrayList<Document> metrics = new ArrayList();

                ArrayList<Document> temp_metrics = extracted_metrics.get(j);
                for (int i = 0; i < temp_metrics.size(); i++) {
                    if (!temp_metrics.get(i).getString("name").equals("crawl_to")) {
                        number_of_metrics++;
                        metrics.add(Document.parse(temp_metrics.get(i).toJson()));
                    }
                }

                Document json = new Document().append(entity_name, company).append("metrics", metrics);

                results.add(json);
            }
        } else {
            ArrayList<Document> metrics = new ArrayList();

            for (int j = 0; j < extracted_metrics.size(); j++) {
                ArrayList<Document> temp_metrics = extracted_metrics.get(j);

                Document json = new Document();
                for (int i = 0; i < temp_metrics.size(); i++) {
                    if (!temp_metrics.get(i).getString("name").equals("crawl_to")) {
                        number_of_metrics++;
                        json.append(temp_metrics.get(i).getString("name"), temp_metrics.get(i).getString("value"));
                    }
                }
                json.append("source", temp_metrics.get(0).getString("source"));
                json.append("citeyear", temp_metrics.get(0).getString("citeyear"));
                metrics.add(json);
            }

            results.addAll(metrics);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        return new Document("results", results).append("timestamp", formatter.format(date)).toJson(JsonWriterSettings.builder().indent(true).build());

    }

    public String exportCSV(String wikirate_metric_designer) {
        String csv = "";

        if (extracted_company_info != null) {

            for (int j = 0; j < extracted_company_info.size(); j++) {
                if (extracted_company_info.isEmpty()) {
                    continue;
                }

                Document company = Document.parse(extracted_company_info.get(j).toJson());

                ArrayList<Document> temp_metrics = extracted_metrics.get(j);
                for (int i = 0; i < temp_metrics.size(); i++) {
                    if (!temp_metrics.get(i).getString("name").equals("crawl_to")) {
                        Document current_metric = Document.parse(temp_metrics.get(i).toJson());
                        String metric_string = wikirate_metric_designer + "+" + current_metric.getString("name") + ",\"" + company.getString("company_name").replace("\"", "") + "\"," + current_metric.get("citeyear") + "," + current_metric.get("value") + ",\"" + current_metric.get("source") + "\"\n";

                        csv += metric_string;
                        number_of_metrics++;
                    }
                }
            }
        }
        return csv;
    }

    public int getNumberOfExtractedMetrics() {
        if (number_of_metrics == 0) {
            exportJson();
        }
        return number_of_metrics;
    }
}
