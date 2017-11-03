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
package certh.iti.mklab.easie.extractors;

import certh.iti.mklab.easie.FIELD_TYPE;
import certh.iti.mklab.easie.configuration.Configuration.ScrapableField;
import certh.iti.mklab.easie.exception.HTMLElementNotFoundException;
import certh.iti.mklab.easie.exception.PostProcessingException;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author vasgat
 */
public class TableFieldExtractor extends AbstractContentExtractor {

    private String table_selector;

    public TableFieldExtractor(Element document, String table_selector, String source) {
        super.document = document;
        this.table_selector = table_selector;
        super.source = source;
    }

    @Override
    public List run(List<ScrapableField> fields, FIELD_TYPE type) throws HTMLElementNotFoundException {
        ArrayList extractedContent = new ArrayList();
        if (document != null) {
            Elements table = document.select(table_selector);

            if (table.size() == 0) {
                throw new HTMLElementNotFoundException("WARNING: The defined table element was not found. Check the correctness of your table selector |" + table_selector + "| or for possible changes in the structure of your source.");
            }

            for (int i = 0; i < table.size(); i++) {
                try {
                    ArrayList temp = new ArrayList();
                    if (type.equals(type.COMPANY_INFO)) {
                        extractedContent.add(extractTableFields(fields, table.get(i), type));
                    } else {
                        temp.addAll((List) extractTableFields(fields, table.get(i), type));
                        extractedContent.add(temp);
                    }

                } catch (PostProcessingException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return extractedContent;
    }

    /**
     * extracts data from the specified table fields
     *
     * @param tableSelector: CSS table selector
     * @param fields: list of table fields
     * @return an ArrayList of HashMap (corresponds to the extracted table
     * fields)
     */
    private Object extractTableFields(List<ScrapableField> fields, Element element, FIELD_TYPE type) throws PostProcessingException {
        ArrayList extractedFields = new ArrayList();

        Document temp_company_info = new Document();
        for (int i = 0; i < fields.size(); i++) {
            Document extracted_content = getSelectedElement(fields.get(i), element);

            if (!extracted_content.getString("name").equals("") && extracted_content.get("value") != null && !extracted_content.get("value").toString().equals("") && extracted_content.get("value") != null) {

                if (type.equals(FIELD_TYPE.COMPANY_INFO)) {
                    temp_company_info.append(extracted_content.getString("name"), extracted_content.get("value"));
                } else {
                    extracted_content.append("source", source);
                    extractedFields.add(extracted_content);
                }
            }
        }

        if (type.equals(FIELD_TYPE.COMPANY_INFO) && !temp_company_info.isEmpty()) {
            return temp_company_info;
        } else if (type.equals(FIELD_TYPE.METRIC) && !extractedFields.isEmpty()) {
            return extractedFields;
        } else {
            return new ArrayList();
        }
    }

}
