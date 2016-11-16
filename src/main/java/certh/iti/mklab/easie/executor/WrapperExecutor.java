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
package certh.iti.mklab.easie.executor;

import certh.iti.mklab.easie.Key;
import certh.iti.mklab.easie.executor.handlers.DataHandler;
import certh.iti.mklab.easie.configuration.Configuration;
import certh.iti.mklab.easie.executor.generators.DynamicWrapperGenerator;
import certh.iti.mklab.easie.executor.generators.StaticWrapperGenerator;
import certh.iti.mklab.easie.executor.generators.WrapperGenerator;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

/**
 * WrapperExecutor extracts contents as defined in Configuration object
 *
 * @author vasgat
 */
public class WrapperExecutor {

    private Configuration config;
    private ArrayList<HashMap> metrics;
    private ArrayList<HashMap> company_info;
    private String ChromeDriverPath;

    /**
     * Creates WrapperExecutor Object that generates a wrapper for an HTML page
     * or a set of HTML pages based the Configuration
     *
     * @param config
     * @throws URISyntaxException
     * @throws IOException
     * @throws Exception
     */
    public WrapperExecutor(Configuration config, String ChromeDriverPath) throws URISyntaxException, IOException, Exception {
        this.config = config;
        this.company_info = new ArrayList<HashMap>();
        this.metrics = new ArrayList<HashMap>();
        this.ChromeDriverPath = ChromeDriverPath;
        this.wrapperGeneration();
    }

    public WrapperExecutor(Configuration config) throws URISyntaxException, IOException, Exception {
        this.config = config;
        this.company_info = new ArrayList<HashMap>();
        this.metrics = new ArrayList<HashMap>();
        this.ChromeDriverPath = "C:\\Users\\vasgat\\Desktop\\Scrapers";
        this.wrapperGeneration();
    }

    /**
     * Stores the extracted data in database or in drive based on the
     * Configuration
     *
     * @throws Exception
     */
    public void store() throws Exception {
        DataHandler handler = new DataHandler(company_info, metrics);
        handler.store(config.store, config.source_name);
    }

    /**
     * Generates and executes a Wrapper
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws Exception
     */
    private void wrapperGeneration() throws URISyntaxException, IOException, Exception {
        WrapperGenerator wrapper;
        if (config.dynamic_page) {
            wrapper = new DynamicWrapperGenerator(config, ChromeDriverPath);
        } else {
            wrapper = new StaticWrapperGenerator(config);

        }
        wrapper.execute();

        company_info = wrapper.extraction_handler.getCompanies();
        metrics = wrapper.extraction_handler.getMetrics();

        Configuration config_crawl = config.crawl;
        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        while (config_crawl != null) {

            HashMap<String, Integer> group_of_urls = new HashMap();

            for (int i = 0; i < metrics.size(); i++) {
                try {
                    group_of_urls.put(config.url.base_url + (((String) metrics.get(i).get(new Key("crawl_to", current_year))).replace(config.url.base_url, "")), i);
                } catch (NullPointerException e) {

                }
            }

            config_crawl.url = config.url;
            config_crawl.url.relative_url = null;
            config_crawl.source_name = config.source_name;
            config_crawl.group_of_urls = new HashSet(group_of_urls.keySet());
            config_crawl.store = config.store;

            WrapperGenerator wrapper_crawl;

            if (!config_crawl.dynamic_page) {
                wrapper_crawl = new StaticWrapperGenerator(config_crawl);
            } else {
                wrapper_crawl = new DynamicWrapperGenerator(config_crawl, ChromeDriverPath);
            }

            wrapper_crawl.execute();

            ArrayList<HashMap> temp_company_info = wrapper_crawl.extraction_handler.getCompanies();
            ArrayList<HashMap> temp_metrics = wrapper_crawl.extraction_handler.getMetrics();

            if (company_info.isEmpty()) {
                for (int i = 0; i < metrics.size(); i++) {
                    company_info.add(new HashMap());
                }
            }

            for (int i = 0; i < temp_metrics.size(); i++) {
                try {

                    int index = group_of_urls.get(temp_metrics.get(i).get("source"));
                    HashMap temp_metric = metrics.get(index);
                    temp_metric.putAll(temp_metrics.get(i));
                    temp_metric.remove("crawl_to");

                    metrics.set(index, temp_metric);

                    HashMap temp_company = company_info.get(index);
                    if (temp_company_info.size() > 0) {
                        temp_company.putAll(temp_company_info.get(i));
                    }

                    company_info.set(index, temp_company);

                } catch (NullPointerException e1) {
                    try {
                        int index = group_of_urls.get(temp_company_info.get(i).get("source"));
                        HashMap temp_metric = metrics.get(index);
                        temp_metric.putAll(temp_metrics.get(i));
                        temp_metric.remove("crawl_to");
                        temp_metric.put("source", temp_company_info.get(i).get("source"));

                        metrics.set(index, temp_metric);

                        HashMap temp_company = company_info.get(index);
                        temp_company.putAll(temp_company_info.get(i));

                        company_info.set(index, temp_company);
                    } catch (NullPointerException e2) {

                    } catch (IndexOutOfBoundsException e3) {

                    }
                }
            }
            config_crawl = config_crawl.crawl;
        }
        for (int i = 0; i < metrics.size(); i++) {
            metrics.get(i).remove(new Key("crawl_to", current_year));
        }
    }

    public ArrayList<HashMap> getExtractedMetrics() {
        return this.metrics;
    }

    public ArrayList<HashMap> getCompanyInfo() {
        return this.company_info;
    }
}
