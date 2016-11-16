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
import certh.iti.mklab.easie.Triplet;
import certh.iti.mklab.easie.configuration.Configuration.ExtractionProperties;
import certh.iti.mklab.easie.configuration.Configuration.ReplaceField;
import certh.iti.mklab.easie.configuration.Configuration.ScrapableField;
import certh.iti.mklab.easie.exception.PostProcessingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author vasgat
 */
public abstract class AbstractContentExtractor {

    protected Element document;
    protected String source;

    abstract protected List run(List<ScrapableField> fields, FIELD_TYPE type);

    public List getExtractedFields(List<ScrapableField> fields, FIELD_TYPE type) {
        return run(fields, type);
    }

    /**
     * Returns the content of a specific field in the document
     *
     * @param field
     * @param element
     * @return a Pair of String, Object that corresponds to field name and field
     * value accordingly
     */
    protected Triplet<String, Object, Integer> getSelectedElement(ScrapableField field, Object e) throws PostProcessingException {
        Element element = (Element) e;

        String field_name;
        Object field_value;
        Integer field_citeyear;

        if (field.label instanceof String) {
            field_name = (String) field.label;
        } else {
            field_name = extractContent((ExtractionProperties) field.label, element).toString();
        }

        if (field.value instanceof String) {
            field_value = (String) field.value;
        } else {
            field_value = extractContent((ExtractionProperties) field.value, element).toString();

        }

        try {
            if (field.citeyear instanceof Double) {
                field_citeyear = ((Double) field.citeyear).intValue();
            } else {
                field_citeyear = (Integer) extractContent((ExtractionProperties) field.citeyear, element);
                if (field_citeyear == null) {
                    field_citeyear = Calendar.getInstance().get(Calendar.YEAR);
                }
            }
        } catch (NullPointerException ex) {
            field_citeyear = Calendar.getInstance().get(Calendar.YEAR);

        }
        return new Triplet<String, Object, Integer>(field_name, field_value, field_citeyear);
    }

    protected Object extractContent(ExtractionProperties extraction_properties, Element element) throws PostProcessingException {
        Object extracted_content;

        if (extraction_properties.type.equals("text") || extraction_properties.type.equals("integer") || extraction_properties.type.equals("double")) {
            extracted_content = element.select(extraction_properties.selector).text();
        } else if (extraction_properties.type.equals("link")) {
            extracted_content = element.select(extraction_properties.selector).attr("href");
            if (extracted_content.equals("")) {
                extracted_content = element.select(extraction_properties.selector).attr("data-tab-content");
                if (!extracted_content.equals("")) {
                    extracted_content = source + "#" + extracted_content;
                }
            }
        } else if (extraction_properties.type.equals("image")) {
            extracted_content = element.select(extraction_properties.selector).attr("src");
        } else if (extraction_properties.type.equals("html")) {
            extracted_content = element.select(extraction_properties.selector).html();
        } else if (extraction_properties.type.equals("list")) {
            extracted_content = extractList(element, extraction_properties.selector);
        } else {
            extracted_content = element.select(extraction_properties.selector).attr(extraction_properties.type);
        }

        if (extracted_content instanceof String && extraction_properties.replace != null) {
            extracted_content = processContent(
                    (String) extracted_content,
                    extraction_properties.replace
            );
        }
        if (extraction_properties.type.equals("integer")) {
            try {
                extracted_content = Integer.parseInt(((String) extracted_content).trim());
            } catch (NumberFormatException nfe) {
                extracted_content = null;
            }
        }
        if (extraction_properties.type.equals("double")) {
            extracted_content = Double.parseDouble(((String) extracted_content).trim());
        }

        return extracted_content;
    }

    protected String processContent(String content, ReplaceField replace_properties) throws PostProcessingException {
        if (replace_properties.regex.size() == replace_properties.with.size()) {
            for (int i = 0; i < replace_properties.regex.size(); i++) {
                content = content.replaceAll(
                        replace_properties.regex.get(i),
                        replace_properties.with.get(i)
                );
            }
        } else {
            throw new PostProcessingException("regex and with arrays need to be the same size in the replace field");
        }

        return content;
    }

    protected List extractList(Element element, String listSelector) {
        List list = new ArrayList();
        Elements elements = element.select(listSelector);
        for (int i = 0; i < elements.size(); i++) {
            list.add(elements.get(i).text()/*.attr("href")*/);
        }
        return list;
    }
}
