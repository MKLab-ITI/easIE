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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Element;

/**
 *
 * @author vasgat
 */
public class FieldExtractor extends AbstractContentExtractor {

    public FieldExtractor(Element document, String source) {
        super.document = document;
        super.source = source;
    }

    @Override
    protected List run(List<ScrapableField> fields, FIELD_TYPE type) {
        HashMap extractedFields = new HashMap();
        if (document != null) {
            for (int i = 0; i < fields.size(); i++) {
                try {
                    Triplet<String, Object, Integer> triplet = getSelectedElement(fields.get(i), document);
                    String tempName = triplet.first;
                    Object tempValue = triplet.second;
                    Integer tempCiteyear = triplet.third;

                    if (!tempName.equals("") && !tempValue.equals("")) {

                        if (type.equals(FIELD_TYPE.COMPANY_INFO)) {
                            extractedFields.put(tempName, tempValue);
                        } else {
                            extractedFields.put(new Key(tempName, tempCiteyear), tempValue);
                        }

                    }
                } catch (PostProcessingException ex) {
                    Logger.getLogger(FieldExtractor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            extractedFields.values().removeAll(Collections.singleton(""));
            extractedFields.values().removeAll(Collections.singleton(null));
            if (!extractedFields.isEmpty()) {
                extractedFields.put("source", source);
            }
        }

        List extractedContent = new ArrayList();
        extractedContent.add(extractedFields);
        return extractedContent;
    }

    public List getExtractedFields(List<ScrapableField> fields, FIELD_TYPE type) {
        return run(fields, type);
    }
}
