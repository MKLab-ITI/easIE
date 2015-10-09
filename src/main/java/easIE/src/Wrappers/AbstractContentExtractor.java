package easIE.src.Wrappers;

import easIE.src.Field;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author vasgat
 */
public abstract class AbstractContentExtractor {
    
    abstract protected ArrayList run(List<Field> fields);
    
    public ArrayList getExtractedFields(List<Field> fields){
        return run(fields);
    }
    
    protected abstract Pair<String, Object> getSelectedElement(Field field, Object element);

    protected abstract ArrayList extractList(String listSelector);
}
