package easIE.src.Wrappers;

import easIE.src.Field;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * StaticHTMLWrapper Object extends AbstractWrapper and is responsible for
 * extracting data from defined fields (by css selectors) from a static webpage
 * or fields from a table.
 *
 * @author vasgat
 */
public class StaticHTMLWrapper extends AbstractWrapper {

    public String baseURL;
    public String relativeURL;
    private String source;
    public Document document;

    /**
     * Creates a new StaticHTMLWrapper for a webpage
     *
     * @param baseURL: webpage base url
     * @param relativeURL: path to the specific spot in the page
     * @throws URISyntaxException
     * @throws IOException
     */
    public StaticHTMLWrapper(String baseURL, String relativeURL) throws URISyntaxException, IOException {
        this.baseURL = baseURL;
        this.source = baseURL + relativeURL;
        this.document = Jsoup.connect(new URI(source).toASCIIString())
                .userAgent("Mozilla/37.0").timeout(60000).get();
        
        
    }
    /**
     * Creates a new StaticHTMLWrapper for a webpage
     *
     * @param FullLink webpage full url
     * @throws URISyntaxException
     * @throws IOException
     */
    public StaticHTMLWrapper(String FullLink) throws URISyntaxException, IOException {
        this.source = FullLink;
        this.document = Jsoup.connect(new URI(source).toASCIIString())
                            .userAgent("Mozilla/37.0").timeout(60000).get();
    }

    /**
     * extracts data from a list of specified fields from a webpage
     * @param fields: list of fields
     * @return a HashMap of the extracted fields
     */
    @Override
    public ArrayList<HashMap> extractFields(List<Field> sfields) {
        StaticFieldExtractor extractor = new StaticFieldExtractor(document, source);
        return extractor.getExtractedFields(sfields);        
    }

    /**
     * extracts data from the specified table fields
     * @param tableSelector: CSS table selector
     * @param fields: list of table fields
     * @return an ArrayList of HashMap (corresponds to the extracted table
     * fields)
     */
    @Override
    public ArrayList<HashMap<String, Object>> extractTable(String table_selector, List<Field> fields) {
        StaticTableFieldExtractor extractor = new StaticTableFieldExtractor(document, table_selector, source);
        return extractor.getExtractedFields(fields);    
    }

    @Override
    public Pair extractFields(List<Field> cfields, List<Field> sfields) {
        StaticFieldExtractor extractor = new StaticFieldExtractor(document, source);
        return new Pair(extractor.getExtractedFields(cfields), extractor.getExtractedFields(sfields));        
    }    

    @Override
    public Pair extractTable(String table_selector, List<Field> cfields, List<Field> sfields) {
        StaticTableFieldExtractor extractor = new StaticTableFieldExtractor(document, table_selector, source);
        return new Pair(extractor.getExtractedFields(cfields),extractor.getExtractedFields(sfields));
    }
}
