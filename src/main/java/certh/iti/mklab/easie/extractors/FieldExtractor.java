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
import certh.iti.mklab.easie.exception.PostProcessingException;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
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
        ArrayList extractedFields = new ArrayList();

        Document temp_company = new Document();
        if (document != null) {
            for (int i = 0; i < fields.size(); i++) {
                try {
                    Document extracted_content = getSelectedElement(fields.get(i), document);

                    if (!extracted_content.getString("name").equals("") && !extracted_content.get("value").toString().equals("") && extracted_content.get("value") != null) {

                        if (type.equals(FIELD_TYPE.COMPANY_INFO)) {
                            temp_company.append(extracted_content.getString("name"), extracted_content.getString("value"));
                        } else {
                            extracted_content.append("source", source);
                            extractedFields.add(extracted_content);
                        }

                    }
                } catch (PostProcessingException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        if (type.equals(FIELD_TYPE.COMPANY_INFO)) {
            extractedFields.add(temp_company);
            return extractedFields;
        } else if (type.equals(FIELD_TYPE.METRIC)) {
            ArrayList temp = new ArrayList();
            temp.add(extractedFields);
            return temp;
        } else {
            return new ArrayList();
        }
    }

    public List getExtractedFields(List<ScrapableField> fields, FIELD_TYPE type) {
        return run(fields, type);
    }
}
