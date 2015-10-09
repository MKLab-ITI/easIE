package easIE.src.Wrappers;

import easIE.src.Field;
import easIE.src.FieldType;
import easIE.src.SelectorType;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author vasgat
 */
public abstract class AbstractStaticContentExtractor extends AbstractContentExtractor {
    protected Element document;
    protected String source;

    abstract protected ArrayList run(List<Field> fields);

    public ArrayList getExtractedFields(List<Field> fields) {
        return run(fields);
    }

    /**
     * Returns the content of a specific field in the document
     *
     * @param field
     * @param element
     * @return a Pair of String, Object that corresponds to field name and field
     * value accordingly
     */
    @Override
    protected Pair<String, Object> getSelectedElement(Field field, Object e) {
        Element element = (Element) e;
        String tempName;
        Object tempValue;
        if (field.SelectorNameType.equals(SelectorType.rawtext)) {
            tempName = field.FieldName;
        } else if (field.FieldNameType.equals(FieldType.text)) {
            tempName = element.select(field.FieldName).text();
        } else if (field.FieldNameType.equals(FieldType.link)) {
            tempName = element.select(field.FieldName).attr("href");
        } else if ((field.FieldNameType.equals(FieldType.image))) {
            tempName = element.select(field.FieldName).attr("src");
        } else {
            tempName = element.select(field.FieldName).attr(field.FieldValueType);
        }
        if (field.SelectorValueType.equals(SelectorType.rawtext)) {
            tempValue = field.FieldValue;
        } else if (field.FieldValueType.equals(FieldType.text)) {
            tempValue = element.select(field.FieldValue).text();
        } else if (field.FieldValueType.equals(FieldType.link)) {
            tempValue = element.select(field.FieldValue).attr("href");
        } else if ((field.FieldValueType.equals(FieldType.image))) {
            tempValue = element.select(field.FieldValue).attr("src");
        } else if (field.FieldValueType.equals(FieldType.list)) {
            tempValue = extractList(field.FieldValue);
        } else {
            tempValue = element.select(field.FieldValue).attr(field.FieldValueType);
        }
        return new Pair<String, Object>(tempName, tempValue);
    }

    @Override
    protected ArrayList extractList(String listSelector) {
        ArrayList list = new ArrayList();
        Elements elements = document.select(listSelector);
        for (int i = 0; i < elements.size(); i++) {
            list.add(elements.get(i).text());
        }
        return list;
    }
}
