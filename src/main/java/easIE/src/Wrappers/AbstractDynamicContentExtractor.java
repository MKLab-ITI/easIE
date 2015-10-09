package easIE.src.Wrappers;

import easIE.src.Field;
import easIE.src.FieldType;
import easIE.src.SelectorType;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author vasgat
 */
public abstract class AbstractDynamicContentExtractor extends AbstractContentExtractor {

    protected WebDriver driver;
    protected String source;

    abstract protected ArrayList run(List<Field> fields);

    
    @Override
    protected Pair<String, Object> getSelectedElement(Field field, Object e) {
        WebElement element = (WebElement) e;
        String tempName;
        Object tempValue;
        if (field.SelectorNameType.equals(SelectorType.rawtext)) {
            tempName = field.FieldName;
        } else if (field.FieldNameType.equals(FieldType.text)) {
            tempName = element.findElement(
                    By.cssSelector(field.FieldName)
            ).getText();
        } else if (field.FieldNameType.equals(FieldType.link)) {
            tempName = element.findElement(
                    By.cssSelector(field.FieldName)
            ).getAttribute("href");
        } else if ((field.FieldNameType.equals(FieldType.image))) {
            tempName = element.findElement(
                    By.cssSelector(field.FieldName)
            ).getAttribute("src");
        } else {
            tempName = element.findElement(
                    By.cssSelector(field.FieldName)
            ).getAttribute(field.FieldValueType);
        }
        if (field.SelectorValueType.equals(SelectorType.rawtext)) {
            tempValue = field.FieldValue;
        } else if (field.FieldValueType.equals(FieldType.text)) {
            try {
                tempValue = element.findElement(
                        By.cssSelector(field.FieldValue)
                ).getText();
            } catch (Exception ex) {
                tempValue = "";
            }
        } else if (field.FieldValueType.equals(FieldType.link)) {
            tempValue = element.findElement(
                    By.cssSelector(field.FieldValue)
            ).getAttribute("href");
        } else if ((field.FieldValueType.equals(FieldType.image))) {
            tempValue = element.findElement(
                    By.cssSelector(field.FieldValue)
            ).getAttribute("src");
        } else if (field.FieldValueType.equals(FieldType.list)) {
            tempValue = extractList(field.FieldValue);
        } else {
            tempValue = element.findElement(
                    By.cssSelector(field.FieldValue)
            ).getAttribute(field.FieldValueType);
        }
        return new Pair(tempName, tempValue);
    }

    /**
     * extracts a list of values from a specific field
     *
     * @param listSelecto CSS selector of list field
     * @return an ArrayList of values
     */
    @Override
    protected ArrayList extractList(String listSelector) {
        ArrayList list = new ArrayList();
        List<WebElement> elements = driver.findElements(By.cssSelector(listSelector));
        for (int i = 0; i < elements.size(); i++) {
            list.add(elements.get(i).getText());
        }
        return list;
    }

}
