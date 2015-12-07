package easIE.src.Wrappers;

import easIE.src.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author vasgat
 */
public class TableFieldExtractor extends AbstractStaticContentExtractor {

    private String source_name;
    private String table_selector;

    TableFieldExtractor(Element document, String table_selector, String source_name) {
        this.document = document;
        this.table_selector = table_selector;
        this.source_name = source_name;
    }

    @Override
    public ArrayList run(List<Field> fields) {
        ArrayList extractedContent = new ArrayList();
        if (document != null) {
            Elements table = document.select(table_selector);
            for (int i = 0; i < table.size(); i++) {
                extractedContent.add(extractTableFields(fields, table.get(i)));
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
    private HashMap<String, Object> extractTableFields(List<Field> fields, Element element) {
        HashMap<String, Object> ExtractedFields = new HashMap<String, Object>();
        for (int i = 0; i < fields.size(); i++) {
            Pair<String, Object> pair = getSelectedElement(fields.get(i), element);
            String tempName = pair.getKey();
            Object tempValue = pair.getValue();
            if (fields.get(i).ReplaceInName != null && fields.get(i).ReplaceInName.regex.size() == fields.get(i).ReplaceInName.with.size()) {
                for (int j = 0; j < fields.get(i).ReplaceInName.regex.size(); j++) {
                    tempName = tempName.replaceAll(
                            fields.get(i).ReplaceInName.regex.get(j),
                            fields.get(i).ReplaceInName.with.get(j)
                    );
                }
            }
            if (fields.get(i).ReplaceInValue != null && fields.get(i).ReplaceInValue.regex.size() == fields.get(i).ReplaceInValue.with.size()) {
                for (int j = 0; j < fields.get(i).ReplaceInValue.regex.size(); j++) {
                    tempValue = ((String) tempValue).replaceAll(
                            fields.get(i).ReplaceInValue.regex.get(j),
                            fields.get(i).ReplaceInValue.with.get(j)
                    );
                }
            }
            ExtractedFields.put(tempName, tempValue);
        }
        ExtractedFields.values().removeAll(Collections.singleton(""));
        ExtractedFields.values().removeAll(Collections.singleton(null));
        if (!ExtractedFields.isEmpty()) {
            ExtractedFields.put("source", source_name);
            if (!ExtractedFields.containsKey("citeyear")) {
                ExtractedFields.put(
                        "citeyear",
                        Calendar.getInstance().get(Calendar.YEAR)
                );
            }
        }
        return ExtractedFields;
    }

}
