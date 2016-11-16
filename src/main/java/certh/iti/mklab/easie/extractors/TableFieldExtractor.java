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
import certh.iti.mklab.easie.Key;
import certh.iti.mklab.easie.Triplet;
import certh.iti.mklab.easie.configuration.Configuration.ScrapableField;
import certh.iti.mklab.easie.exception.PostProcessingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public List run(List<ScrapableField> fields, FIELD_TYPE type) {
        ArrayList extractedContent = new ArrayList();
        if (document != null) {
            Elements table = document.select(table_selector);
            for (int i = 0; i < table.size(); i++) {
                try {
                    extractedContent.add(extractTableFields(fields, table.get(i), type));
                } catch (PostProcessingException ex) {
                    Logger.getLogger(TableFieldExtractor.class.getName()).log(Level.SEVERE, null, ex);
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
    private Map<Object, Object> extractTableFields(List<ScrapableField> fields, Element element, FIELD_TYPE type) throws PostProcessingException {
        HashMap<Object, Object> ExtractedFields = new HashMap<Object, Object>();
        for (int i = 0; i < fields.size(); i++) {
            Triplet<String, Object, Integer> triplet = getSelectedElement(fields.get(i), element);

            String tempName = triplet.first;
            Object tempValue = triplet.second;
            Integer tempCiteyear = triplet.third;

            if (!tempName.equals("") && !tempValue.equals("")) {
                if (type.equals(FIELD_TYPE.COMPANY_INFO)) {
                    ExtractedFields.put(tempName, tempValue);
                } else {
                    ExtractedFields.put(new Key(tempName, tempCiteyear), tempValue);
                }
            }
        }
        ExtractedFields.values().removeAll(Collections.singleton(""));
        ExtractedFields.values().removeAll(Collections.singleton(null));
        if (!ExtractedFields.isEmpty()) {
            ExtractedFields.put("source", source);
        }
        return ExtractedFields;
    }

}
