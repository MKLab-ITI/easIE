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
public class SingleDynamicPageExtractor implements Callable {

    public String page;
    private String tableSelector;
    private List<Field> cfields;
    private List<Field> sfields;
    public Document document;

    public SingleDynamicPageExtractor(String url, List<Field> cfields, List<Field> sfields) {
        this.page = url;
        this.cfields = cfields;
        this.sfields = sfields;
    }

    public SingleDynamicPageExtractor(String url, List<Field> sfields) {
        this.page = url;
        this.sfields = sfields;
    }

    public SingleDynamicPageExtractor(String url, String tableSelector, List<Field> cfields, List<Field> sfields) {
        this.page = url;
        this.tableSelector = tableSelector;
        this.cfields = cfields;
        this.sfields = sfields;
    }

    public SingleDynamicPageExtractor(String url, String tableSelector, List<Field> sfields) {
        this.page = url;
        this.tableSelector = tableSelector;
        this.sfields = sfields;
    }

    @Override
    public Object call() throws Exception {
        try {
            DynamicHTMLWrapper wrapper = new DynamicHTMLWrapper(page);
            document = wrapper.getHTMLDocument();
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
