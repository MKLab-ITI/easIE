package easIE.src.Wrappers;

import easIE.src.Field;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * AbstractWrapper Object
 * @author vasgat
 */
public abstract class AbstractWrapper {
   
   public abstract Object extractFields(List<Field> fields) throws URISyntaxException, IOException, Exception;
   
   public abstract Object extractTable(String tableSelector, List<Field> fields) throws URISyntaxException, IOException, Exception;
   
   public abstract Object extractFields(List<Field> cfields, List<Field> sfields) throws URISyntaxException, IOException, Exception;
   
   public abstract Object extractTable(String tableSelector, List<Field> cfields, List<Field> sfields) throws URISyntaxException, IOException, Exception;
}
