package easIE.src.Wrappers;

import easIE.src.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import org.jsoup.nodes.Element;

/**
 *
 * @author vasgat
 */
public class FieldExtractor extends AbstractStaticContentExtractor {

    FieldExtractor(Element document, String source) {
        super.document = document;
        this.source = source;
    }

    @Override
    protected ArrayList run(List<Field> fields) {
        HashMap extractedFields = new HashMap();
        if (document != null) {
            for (int i = 0; i < fields.size(); i++) {
                Pair<String, Object> pair = getSelectedElement(fields.get(i), document);
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
                extractedFields.put(tempName, tempValue);
            }
            extractedFields.values().removeAll(Collections.singleton(""));
            extractedFields.values().removeAll(Collections.singleton(null));
            if (!extractedFields.isEmpty()) {
                extractedFields.put("source", source);
                if (!extractedFields.containsKey("citeyear")) {
                    extractedFields.put(
                            "citeyear",
                            Calendar.getInstance().get(Calendar.YEAR)
                    );
                }
            }

        }
        ArrayList extractedContent = new ArrayList();
        extractedContent.add(extractedFields);
        return extractedContent;
    }

    public ArrayList getExtractedFields(List<Field> fields) {
        return run(fields);
    }
}
