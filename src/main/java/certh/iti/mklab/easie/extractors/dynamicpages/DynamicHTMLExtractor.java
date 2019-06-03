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
package certh.iti.mklab.easie.extractors.dynamicpages;

import certh.iti.mklab.easie.configuration.Configuration.ScrapableField;
import certh.iti.mklab.easie.extractors.AbstractHTMLExtractor;
import certh.iti.mklab.easie.FIELD_TYPE;
import certh.iti.mklab.easie.exception.HTMLElementNotFoundException;
import certh.iti.mklab.easie.extractors.FieldExtractor;
import certh.iti.mklab.easie.extractors.TableFieldExtractor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;

/**
 * DynamicHTMLExtractor object extends AbstractHTMLExtractor and is responsible
 * for extracting content from defined fields (by css selectors) from a dynamic
 * webpage or fields from a table.
 *
 * @author vasgat
 */
public class DynamicHTMLExtractor extends AbstractHTMLExtractor {

    public String base_url;
    public String source;
    public BrowserEmulator browser_emulator;

    /**
     * Creates a new DynamicHTMLWrapper for a webpage
     *
     * @param base_url:     webpage base url
     * @param relative_url: path to the specific spot in the page
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public DynamicHTMLExtractor(String base_url, String relative_url, String ChromeDriverPath) throws URISyntaxException, IOException, InterruptedException {
        this.base_url = base_url;
        this.source = base_url + relative_url;
        this.browser_emulator = new BrowserEmulator(base_url + relative_url, ChromeDriverPath);
    }

    /**
     * Creates a new DynamicHTMLWrapper for a webpage
     *
     * @param FullLink: webpage's full url
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public DynamicHTMLExtractor(String FullLink, String ChromeDriverPath) throws URISyntaxException, IOException, InterruptedException {
        this.source = FullLink;
        this.browser_emulator = new BrowserEmulator(FullLink, ChromeDriverPath);
    }

    /**
     * extracts data from a list of specified fields from a webpage
     *
     * @param fields: list of fields
     * @return a HashMap of the extracted data fields
     */
    @Override
    public List extractFields(List<ScrapableField> fields) {
        FieldExtractor extractor = new FieldExtractor(
                (Element) browser_emulator.getHTMLDocument(),
                source
        );
        return extractor.getExtractedFields(fields, FIELD_TYPE.METRIC);
    }

    /**
     * extracts data from the specified table fields
     *
     * @param tableSelector: CSS table selector
     * @param fields:        list of table fields
     * @return an ArrayList of HashMap (corresponds to the extracted table
     * fields)
     */
    @Override
    public List extractTable(String tableSelector, List<ScrapableField> fields) {
        TableFieldExtractor extractor
                = new TableFieldExtractor(
                (Element) browser_emulator.getHTMLDocument(),
                tableSelector, source
        );
        try {
            return extractor.getExtractedFields(fields, FIELD_TYPE.METRIC);
        } catch (HTMLElementNotFoundException ex) {
            System.out.println(ex.getMessage());
            return new ArrayList();
        }
    }

    @Override
    public Pair extractFields(List<ScrapableField> cfields, List<ScrapableField> sfields) throws URISyntaxException, IOException {
        FieldExtractor extractor = new FieldExtractor((Element) browser_emulator.getHTMLDocument(), source);
        return Pair.of(
                extractor.getExtractedFields(cfields, FIELD_TYPE.COMPANY_INFO),
                extractor.getExtractedFields(sfields, FIELD_TYPE.METRIC)
        );
    }

    @Override
    public Pair extractTable(String tableSelector, List<ScrapableField> cfields, List<ScrapableField> sfields) throws URISyntaxException, IOException {

        TableFieldExtractor extractor = new TableFieldExtractor((Element) browser_emulator.getHTMLDocument(), tableSelector, source);
        List extracted_company_info = new ArrayList();
        List extracted_metric_info = new ArrayList();
        try {
            extracted_company_info = extractor.getExtractedFields(cfields, FIELD_TYPE.COMPANY_INFO);
            extracted_metric_info = extractor.getExtractedFields(sfields, FIELD_TYPE.METRIC);
        } catch (HTMLElementNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

        return Pair.of(
                extracted_company_info,
                extracted_metric_info
        );

    }

}
