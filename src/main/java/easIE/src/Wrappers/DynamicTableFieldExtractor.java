package easIE.src.Wrappers;

import easIE.src.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author vasgat
 */
public class DynamicTableFieldExtractor extends AbstractDynamicContentExtractor {

    private String table_selector;

    DynamicTableFieldExtractor(String table_selector, WebDriver driver, String source) {
        this.driver = driver;
        this.source = source;
        this.table_selector = table_selector;
    }

    @Override
    protected ArrayList run(final List<Field> fields) {
        ArrayList<HashMap<String, Object>> extractedTableFields = new ArrayList();
        final List<WebElement> table = driver.findElements(By.cssSelector(table_selector));
        for (int i=0; i<table.size(); i++)
        extractedTableFields.add(extractTableFields(fields, table.get(i)));

        return extractedTableFields;
    }

    /**
     * private function which extracts columns of a table row
     * @param fields/columns of the table
     * @param element row of the table
     * @return a HashMap of the extracted fields
     */
    private HashMap<String, Object> extractTableFields(List<Field> fields, WebElement element) {
        HashMap<String, Object> ExtractedFields = new HashMap<String, Object>();
        for (int i = 0; i < fields.size(); i++) {
            Pair<String, Object> pair = getSelectedElement(fields.get(i), element);
            ExtractedFields.put(pair.getKey(), pair.getValue());
        }
        ExtractedFields.put("source", source);
        if (!ExtractedFields.containsKey("citeyear")) {
            ExtractedFields.put(
                    "citeyear",
                    Calendar.getInstance().get(Calendar.YEAR)
            );
        }
        return ExtractedFields;
    }
}
