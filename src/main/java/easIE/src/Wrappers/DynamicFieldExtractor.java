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
public class DynamicFieldExtractor extends AbstractDynamicContentExtractor {

    
    DynamicFieldExtractor(WebDriver driver, String source){
        this.driver = driver;
        this.source = source;
    }
    
    @Override
    protected ArrayList run(List<Field> fields) {
        HashMap<String, Object> ExtractedFields = new HashMap<String, Object>();
        for (int i = 0; i < fields.size(); i++) {
            try {
                Pair<String, Object> pair = getSelectedElement(
                        fields.get(i),
                        (WebElement) driver.findElement(By.tagName("html"))
                );
                ExtractedFields.put(pair.getKey(), pair.getValue());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        ExtractedFields.put("source", source);
        if (!ExtractedFields.containsKey("citeyear")) {
            ExtractedFields.put(
                    "citeyear",
                    Calendar.getInstance().get(Calendar.YEAR)
            );
        }
        ArrayList result = new ArrayList();
        result.add(ExtractedFields);
        return result;
    }
}
