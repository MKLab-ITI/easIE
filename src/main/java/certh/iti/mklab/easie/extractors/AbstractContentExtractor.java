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
import certh.iti.mklab.easie.configuration.Configuration.ExtractionProperties;
import certh.iti.mklab.easie.configuration.Configuration.ReplaceField;
import certh.iti.mklab.easie.configuration.Configuration.ScrapableField;
import certh.iti.mklab.easie.exception.HTMLElementNotFoundException;
import certh.iti.mklab.easie.exception.PostProcessingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author vasgat
 */
public abstract class AbstractContentExtractor {

    protected Element document;
    protected String source;

    abstract protected List run(List<ScrapableField> fields, FIELD_TYPE type) throws HTMLElementNotFoundException;

    public List getExtractedFields(List<ScrapableField> fields, FIELD_TYPE type) throws HTMLElementNotFoundException {
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
    protected Document getSelectedElement(ScrapableField field, Object e) throws PostProcessingException {
        Element element = (Element) e;

        String field_name;
        Object field_value;
        Integer field_citeyear;

        if (field.label instanceof String) {
            field_name = (String) field.label;
        } else {
            field_name = extractContent((ExtractionProperties) field.label, element).toString();
        }

        try {
            if (field.citeyear instanceof Double) {
                field_citeyear = ((Double) field.citeyear).intValue();
            } else {
                try {
                    field_citeyear = (Integer) Integer.parseInt((String) extractContent((ExtractionProperties) field.citeyear, element));
                } catch (NumberFormatException ex) {
                    field_citeyear = null;
                }
                if (field_citeyear == null) {
                    field_citeyear = Calendar.getInstance().get(Calendar.YEAR);
                }
            }
        } catch (NullPointerException ex) {
            field_citeyear = Calendar.getInstance().get(Calendar.YEAR);

        }

        Document extracted_content = new Document();
        extracted_content
                .append("name", field_name)
                .append("citeyear", field_citeyear);
        if (field.value instanceof String) {
            field_value = (String) field.value;

            if (field_value.toString().trim().equals("0") || field_value.toString().trim().toLowerCase().equals("false") || field_value.toString().trim().toLowerCase().equals("no")) {
                extracted_content.append("value", false);
                extracted_content.append("type", "boolean");
            } else if (field_value.toString().trim().equals("1") || field_value.toString().trim().toLowerCase().equals("true") || field_value.toString().trim().toLowerCase().equals("yes")) {
                extracted_content.append("value", true);
                extracted_content.append("type", "boolean");
            } else if (field_value.toString().replaceAll("[0-9\\.,]", "").trim().equals("")) {
                try {
                    field_value = Double.parseDouble(field_value.toString().replaceAll("[^0-9\\.]", "").trim());
                } catch (NumberFormatException nfe) {
                    field_value = null;
                }
                extracted_content.append("type", "numerical");
            } else {
                extracted_content.append("value", field_value).append("type", "textual");
            }
        } else {
            field_value = extractContent((ExtractionProperties) field.value, element).toString();

            ExtractionProperties properties = (ExtractionProperties) field.value;

            if (properties.type.equals("text")) {
                extracted_content.append("type", "textual");
                extracted_content.append("value", field_value);
            } else if (properties.type.equals("numerical")) {
                String extracted_field_value = field_value.toString().toLowerCase();
                try {
                    field_value = Double.parseDouble(extracted_field_value.replaceAll("[^0-9\\.]", "").trim());
                    if (extracted_field_value.contains("million")) {
                        extracted_field_value = extracted_field_value.replaceAll("millions of", "");                        
                        extracted_field_value = extracted_field_value.replaceAll("millions", "");
                        extracted_field_value = extracted_field_value.replaceAll("million", "");
                       
                        field_value = ((Double) field_value) * 1000000;
                    } else if (extracted_field_value.contains("billion")) {
                        extracted_field_value = extracted_field_value.replaceAll("billions of", "");
                        extracted_field_value = extracted_field_value.replaceAll("billions", "");
                        extracted_field_value = extracted_field_value.replaceAll("billion", "");
                        field_value = ((Double) field_value) * 1000000000;
                    }
                } catch (NumberFormatException nfe) {
                    field_value = null;
                }
                String units = "";
                try {
                    units = extracted_field_value.replaceAll("[0-9\\.,]", "").trim();
                } catch (NullPointerException ex) {
                    System.out.println(ex.getMessage());
                }
                extracted_content.append("value", field_value);
                extracted_content.append("type", "numerical");
                if (!units.equals("")) {
                    extracted_content.append("units", units);
                }
            } else if (properties.type.equals("boolean")) {
                if (field_value.toString().trim().equals("0") || field_value.toString().trim().toLowerCase().equals("false") || field_value.toString().trim().toLowerCase().equals("no")) {
                    extracted_content.append("value", false);
                    extracted_content.append("type", "boolean");
                } else if (field_value.toString().trim().equals("1") || field_value.toString().trim().toLowerCase().equals("true") || field_value.toString().trim().toLowerCase().equals("yes")) {
                    extracted_content.append("value", true);
                    extracted_content.append("type", "boolean");
                } else {
                    extracted_content.append("type", "categorical");
                    extracted_content.append("value", field_value);
                }
            } else if (properties.type.equals("categorical")) {
                extracted_content.append("type", "categorical");
                extracted_content.append("value", field_value);
            } else if (properties.type.equals("link") || properties.type.equals("src")) {
                extracted_content.append("type", "link");
                extracted_content.append("value", field_value);
            } else {
                extracted_content.append("type", "other");
                extracted_content.append("value", field_value);
            }
        }
        return extracted_content;
    }

    protected Object extractContent(ExtractionProperties extraction_properties, Element element) throws PostProcessingException {
        Object extracted_content;

        if (extraction_properties.type.equals("text") || extraction_properties.type.equals("numerical") || extraction_properties.type.equals("boolean") || extraction_properties.type.equals("categorical")) {
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

        if (extracted_content.equals("")) {
            System.out.println("WARNING: No content found in the specified element:|" + element.cssSelector() + " " + extraction_properties.selector + "| Please check the correction of the defined extraction rule or for possible changes in the source!");
        }
        if (extracted_content instanceof String && extraction_properties.replace != null) {
            extracted_content = processContent(
                    (String) extracted_content,
                    extraction_properties.replace
            );
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
