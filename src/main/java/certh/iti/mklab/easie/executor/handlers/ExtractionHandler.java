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

import certh.iti.mklab.easie.configuration.Configuration;
import certh.iti.mklab.easie.extractors.AbstractHTMLExtractor;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;

/**
 *
 * @author vasgat
 */
public class ExtractionHandler {

    private AbstractHTMLExtractor wrapper;
    private Configuration configuration;
    private ArrayList<HashMap> extracted_companies;
    private ArrayList<HashMap> extracted_metrics;

    public ExtractionHandler() {
        this.extracted_companies = new ArrayList<HashMap>();
        this.extracted_metrics = new ArrayList<HashMap>();
    }

    public void execute(AbstractHTMLExtractor wrapper, Configuration configuration) throws Exception {
        this.wrapper = wrapper;
        this.configuration = configuration;
        if (configuration.table_selector != null) {
            extractTable();
        } else {
            extractFields();
        }
    }

    public ArrayList getMetrics() {
        return extracted_metrics;
    }

    public ArrayList getCompanies() {
        return extracted_companies;
    }

    private void extractTable() throws Exception {
        if (configuration.company_info != null && configuration.metrics != null) {
            Pair temp = (Pair) wrapper.extractTable(
                    configuration.table_selector,
                    configuration.company_info,
                    configuration.metrics
            );
            extracted_companies.addAll(
                    (ArrayList<HashMap>) temp.getKey()
            );
            extracted_metrics.addAll(
                    (ArrayList<HashMap>) temp.getValue()
            );
        } else {
            extracted_metrics.addAll(
                    (ArrayList<HashMap>) wrapper.extractTable(
                            configuration.table_selector,
                            configuration.metrics
                    ));
        }
    }

    private void extractFields() throws Exception {

        if (configuration.company_info != null) {
            Pair temp = (Pair) wrapper.extractFields(
                    configuration.company_info,
                    configuration.metrics
            );
            extracted_companies.addAll(
                    (ArrayList<HashMap>) temp.getKey()
            );
            extracted_metrics.addAll(
                    (ArrayList<HashMap>) temp.getValue());
        } else {
            extracted_metrics.addAll(
                    (ArrayList<HashMap>) wrapper.extractFields(
                            configuration.metrics
                    ));
        }
    }
}
