package easIE.src.Wrappers;

import easIE.src.Field;
import java.util.List;
import java.util.concurrent.Callable;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

/**
 *
 * @author vasgat
 */
public class SingleStaticPageExtractor implements Callable {

    public String page;
    private String tableSelector;
    private List<Field> cfields;
    private List<Field> sfields;
    public Document document;


    public SingleStaticPageExtractor(String url, String tableSelector, List<Field> cfields, List<Field> sfields) {
        this.page = url;
        this.tableSelector = tableSelector;
        this.cfields = cfields;
        this.sfields = sfields;
    }

    @Override
    public Object call() throws Exception {
        try {
            StaticHTMLWrapper wrapper = new StaticHTMLWrapper(page);
            document = wrapper.document;
            if (tableSelector != null && sfields != null && cfields != null) {
                return wrapper.extractTable(tableSelector, cfields, sfields);
            } else if (tableSelector != null && sfields != null) {
                return wrapper.extractTable(tableSelector, sfields);
            } else if (cfields != null && sfields != null) {
                return wrapper.extractFields(cfields, sfields);
            } else if (sfields != null) {
                return wrapper.extractFields(sfields);
            }
        } catch (HttpStatusException ex) {
            return null;
        }
        return null;
    }

}
