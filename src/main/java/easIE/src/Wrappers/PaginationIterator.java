package easIE.src.Wrappers;

import easIE.src.Field;
import easIE.src.URLPatterns;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private int numThreads;
    private String frontPattern;
    private String rearPattern;

    /**
     * Creates a new PaginationIterator
     *
     * @param wrapper StaticHTMLWrapper object of an Instance page
     * @param nextPageSelector next Page CSS selector in the page
     * @throws URISyntaxException
     * @throws IOException
     */
    public PaginationIterator(String baseURL, String relativeURL, String nextPageSelector) throws URISyntaxException, IOException {
        this.baseURL = baseURL;
        this.relativeURL = relativeURL;
        this.nextPageSelector = nextPageSelector;
        this.numThreads = 20;
        thereisPattern();
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
        if (thereisPattern()) {
            extractedFields.addAll((ArrayList) MultiThreadPagination(
                    frontPattern,
                    rearPattern,
                    null,
                    null,
                    fields
            ));
        } else {
            extractedFields.addAll(SingleThreadPagination(new SingleStaticPageExtractor(
                    baseURL + relativeURL, null, null, fields
            )));
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
        if (thereisPattern()) {
            extractedFields.addAll((ArrayList) MultiThreadPagination(
                    frontPattern,
                    rearPattern,
                    tableSelector,
                    null,
                    fields
            ));
        } else {
            extractedFields.addAll(SingleThreadPagination(new SingleStaticPageExtractor(
                    baseURL + relativeURL, tableSelector, null, fields
            )));
        }
        return extractedFields;
    }

    @Override
    public Pair extractFields(List<Field> cfields, List<Field> sfields) throws URISyntaxException, IOException, Exception {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();
        ArrayList temp;
        if (thereisPattern()) {
            temp = (ArrayList) MultiThreadPagination(
                    frontPattern,
                    rearPattern,
                    null,
                    cfields,
                    sfields
            );
        } else {
            temp = SingleThreadPagination(new SingleStaticPageExtractor(
                    baseURL + relativeURL, null, cfields, sfields
            ));
        }
        for (int i = 0; i < temp.size(); i++) {
            extractedCFields.addAll((ArrayList) (((Pair) temp.get(i)).getKey()));
            extractedSFields.addAll((ArrayList) (((Pair) temp.get(i)).getValue()));
        }
        return new Pair(extractedCFields, extractedSFields);
    }

    @Override
    public Pair extractTable(String tableSelector, List<Field> cfields, List<Field> sfields) throws URISyntaxException, IOException, Exception {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();
        ArrayList temp;
        if (thereisPattern()) {
            temp = (ArrayList) MultiThreadPagination(
                    frontPattern,
                    rearPattern,
                    tableSelector,
                    cfields,
                    sfields
            );
        } else {
            temp = SingleThreadPagination(new SingleStaticPageExtractor(
                    baseURL + relativeURL, tableSelector, cfields, sfields
            ));
        }
        for (int i = 0; i < temp.size(); i++) {
            extractedCFields.addAll((ArrayList) (((Pair) temp.get(i)).getKey()));
            extractedSFields.addAll((ArrayList) (((Pair) temp.get(i)).getValue()));
        }
        return new Pair(extractedCFields, extractedSFields);
    }

    private boolean thereisPattern() throws IOException, URISyntaxException {
        StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL, relativeURL);

        if (!wrapper.document.select(nextPageSelector).attr("href").equals("")) {
            String url1 = baseURL + wrapper.document.select(nextPageSelector).attr("href").replace(baseURL, "");
            wrapper.document = Jsoup.connect(new URI(baseURL + wrapper.document.select(nextPageSelector).attr("href").replace(wrapper.baseURL, "")).toASCIIString())
                    .userAgent("Mozilla/37.0").timeout(60000).get();
            String url2 = baseURL + wrapper.document.select(nextPageSelector).attr("href").replace(baseURL, "");
            frontPattern = URLPatterns.frontPattern(url1, url2);
            rearPattern = URLPatterns.rearPattern(url1, url2);
            return URLPatterns.isInteger(url2.replace(frontPattern, "").replace(rearPattern, ""));
        } else {
            return false;
        }
    }

    private Object MultiThreadPagination(String frontPattern, String rearPattern, String tableSelector, List<Field> sfields, List<Field> cfields) throws InterruptedException {
        ArrayList extractedFields = new ArrayList();
        boolean existNext = true;
        int i = 0;
        while (existNext) {
            //System.out.println(i);
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            List<Future<Object>> handles = new ArrayList<Future<Object>>();
            for (int j = 1; j <= 100; j++) {
                int pagenum = i * 100 + j;
                //System.out.println(frontPattern + pagenum + rearPattern);
                //singlePageExtractor.page = frontPattern + pagenum + rearPattern;
                handles.add(executorService.submit(new SingleStaticPageExtractor(
                        frontPattern + pagenum + rearPattern,
                        tableSelector,
                        sfields,
                        cfields
                )));
            }
            executorService.shutdown();
            executorService.awaitTermination(680, TimeUnit.SECONDS);
            for (int t = 0, n = handles.size(); t < n; t++) {
                try {
                    if (handles.get(t).get() instanceof ArrayList) {
                        ArrayList list = (ArrayList) handles.get(t).get();
                        if (list.size() == 0 || list == null) {
                            existNext = false;
                        } else {
                            extractedFields.addAll(list);
                        }

                    } else {
                        Pair pair = (Pair) handles.get(t).get();
                        if (pair!=null && ((ArrayList) pair.getKey()).size() > 0) {
                            extractedFields.add(pair);
                        } else {
                            existNext = false;
                        }
                    }
                } catch (ExecutionException ex) {
                    Logger.getLogger(PaginationIterator.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PaginationIterator.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchElementException ex) {
                    Logger.getLogger(PaginationIterator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            executorService.shutdownNow();
            i++;
        }
        return extractedFields;
    }

    private ArrayList SingleThreadPagination(SingleStaticPageExtractor singlePageExtractor) throws URISyntaxException, IOException, Exception {
        ArrayList extractedFields = new ArrayList();
        Object result = singlePageExtractor.call();
        if (result instanceof ArrayList) {
            extractedFields.addAll((ArrayList) singlePageExtractor.call());
        } else {
            extractedFields.add((Pair) singlePageExtractor.call());
        }
        while (!singlePageExtractor.document.select(nextPageSelector).attr("href").equals("")) {
            singlePageExtractor.page = baseURL + singlePageExtractor.document.select(nextPageSelector).attr("href").replace(baseURL, "");
            result = singlePageExtractor.call();
            if (result instanceof ArrayList) {
                extractedFields.addAll((ArrayList) result);
            } else {
                extractedFields.add((Pair) result);
            }
        }
        return extractedFields;
    }
}
