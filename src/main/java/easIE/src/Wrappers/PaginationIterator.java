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

/**
 * PaginationItearator object extends AbstractWrapper and is responsible for
 * extracting content that is distributed to different pages
 *
 * @author vasgat
 */
public class PaginationIterator extends AbstractWrapper {

    private String nextPageSelector;
    private String baseURL;
    private String relativeURL;

    /**
     * Creates a new PaginationIterator
     *
     * @param wrapper StaticHTMLWrapper object of an Instance page
     * @param nextPageSelector next Page CSS selector in the page
     * @throws URISyntaxException
     * @throws IOException
     */
    public PaginationIterator(String baseURL, String relativeURL, String nextPageSelector) throws URISyntaxException, IOException {
        this.nextPageSelector = nextPageSelector;
    }

    /**
     * Extracts data from the defined fields of each page until no next page
     * exists
     *
     * @param fields: List of fields we want to extract
     * @return the extracted data fields as a List of HashMaps
     * @throws URISyntaxException
     * @throws IOException
     * @throws Exception
     */
    @Override
    public ArrayList<HashMap> extractFields(List<Field> fields) throws URISyntaxException, IOException, Exception {
        ArrayList<HashMap> extractedFields = new ArrayList();
        StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL, relativeURL);
        extractedFields.addAll(wrapper.extractFields(fields));
        while (!wrapper.document.select(nextPageSelector).attr("href").equals("")) {
            wrapper.document = Jsoup.connect(new URI(wrapper.baseURL + wrapper.document.select(nextPageSelector).attr("href").replace(wrapper.baseURL, "")).toASCIIString())
                    .userAgent("Mozilla/37.0").timeout(60000).get();
            extractedFields.addAll(wrapper.extractFields(fields));
        }
        return extractedFields;
    }

    /**
     * Extracts data from a table from each page until no next page exists
     *
     * @param tableSelector CSS table selector
     * @param fields List of table fields we want to extract
     * @return the extracted table fields as a List of HashMaps
     * @throws URISyntaxException
     * @throws IOException
     * @throws Exception
     */
    @Override
    public ArrayList<HashMap> extractTable(String tableSelector, List<Field> fields) throws URISyntaxException, IOException, Exception {
        ArrayList<HashMap> extractedFields = new ArrayList();
        StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL, relativeURL);
        extractedFields.addAll(wrapper.extractTable(tableSelector, fields));
        while (!wrapper.document.select(nextPageSelector).attr("href").equals("")) {
            wrapper.document = Jsoup.connect(new URI(wrapper.baseURL + wrapper.document.select(nextPageSelector).attr("href").replace(wrapper.baseURL, "")).toASCIIString())
                    .userAgent("Mozilla/37.0").timeout(60000).get();
            extractedFields.addAll(wrapper.extractTable(tableSelector, fields));
        }
        return extractedFields;
    }

    @Override
    public Pair extractFields(List<Field> cfields, List<Field> sfields) throws URISyntaxException, IOException, Exception {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();
        StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL, relativeURL);
        extractedCFields.addAll(wrapper.extractFields(cfields));
        extractedSFields.addAll(wrapper.extractFields(sfields));
        while (!wrapper.document.select(nextPageSelector).attr("href").equals("")) {
            wrapper.document = Jsoup.connect(new URI(wrapper.baseURL + wrapper.document.select(nextPageSelector).attr("href").replace(wrapper.baseURL, "")).toASCIIString())
                    .userAgent("Mozilla/37.0").timeout(60000).get();
            extractedCFields.addAll(wrapper.extractFields(cfields));
            extractedSFields.addAll(wrapper.extractFields(sfields));
        }
        return new Pair(extractedCFields, extractedSFields);
    }

    @Override
    public Object extractTable(String tableSelector, List<Field> cfields, List<Field> sfields) throws URISyntaxException, IOException, Exception {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();
        StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL, relativeURL);
        extractedCFields.addAll(wrapper.extractTable(tableSelector, cfields));
        extractedSFields.addAll(wrapper.extractTable(tableSelector, sfields));
        while (!wrapper.document.select(nextPageSelector).attr("href").equals("")) {
            wrapper.document = Jsoup.connect(new URI(wrapper.baseURL + wrapper.document.select(nextPageSelector).attr("href").replace(wrapper.baseURL, "")).toASCIIString())
                    .userAgent("Mozilla/37.0").timeout(60000).get();
            extractedCFields.addAll(wrapper.extractTable(tableSelector, cfields));
            extractedSFields.addAll(wrapper.extractTable(tableSelector, sfields));
        }
        return new Pair(extractedCFields, extractedSFields);
    }
}
