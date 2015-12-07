package easIE.src.Wrappers;

import easIE.src.Field;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

/**
 * BunchWrapper object extends AbstractWrapper and is responsible for extracting
 * data from a set of links - webpages with the same structure
 *
 * @author vasgat
 */
public class BunchWrapper extends AbstractWrapper {

    private HashSet<String> BunchOfLinks;
    private String baseURL;
    private int numThreads;

    /**
     * Creates a new BunchWrapper
     *
     * @param BunchOfLinks a set of webpages
     */
    public BunchWrapper(HashSet<String> BunchOfLinks) {
        this.baseURL = "";
        this.BunchOfLinks = BunchOfLinks;
        this.numThreads = 10;
    }

    public BunchWrapper(HashSet<String> BunchOfLinks, String baseURL) {
        this.baseURL = baseURL;
        this.BunchOfLinks = BunchOfLinks;
        this.numThreads = 10;
    }

    /**
     * Extracts specified field for each webpage
     *
     * @param fields set of fields
     * @return the extracted Fields
     * @throws URISyntaxException
     * @throws IOException
     * @throws Exception
     */
    @Override
    public ArrayList<HashMap> extractFields(List<Field> fields) throws URISyntaxException, IOException, Exception {
        ArrayList<HashMap> extractedFields = new ArrayList();
        System.out.println(BunchOfLinks);
        Iterator links = BunchOfLinks.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<ArrayList>> handles = new ArrayList<Future<ArrayList>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new SingleStaticPageExtractor(
                    baseURL + links.next(),
                    null,
                    null,
                    fields
            )));
        }
        for (int t = 0, n = handles.size(); t < n; t++) {
            try {
                extractedFields.addAll(handles.get(t).get());
            } catch (ExecutionException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchElementException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        executorService.shutdownNow();
        return extractedFields;
    }

    /**
     * Extracts data from a table
     *
     * @param tableSelector
     * @param fields
     * @return the extracted table fields for each page
     * @throws URISyntaxException
     * @throws IOException
     * @throws Exception
     */
    @Override
    public ArrayList<HashMap> extractTable(String tableSelector, List<Field> fields) throws URISyntaxException, IOException, Exception {
        ArrayList<HashMap> extractedFields = new ArrayList();
        Iterator links = BunchOfLinks.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<ArrayList>> handles = new ArrayList<Future<ArrayList>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new SingleStaticPageExtractor(
                    baseURL + links.next(),
                    tableSelector,
                    null,
                    fields
            )));
        }
        for (int t = 0, n = handles.size(); t < n; t++) {
            try {
                extractedFields.addAll(handles.get(t).get());
            } catch (ExecutionException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchElementException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        executorService.shutdownNow();
        return extractedFields;
    }

    @Override
    public Object extractFields(List<Field> cfields, List<Field> sfields) throws InterruptedException {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();
        System.out.println(BunchOfLinks);
        Iterator links = BunchOfLinks.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<Pair>> handles = new ArrayList<Future<Pair>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new SingleStaticPageExtractor(
                    baseURL + links.next(),
                    null,
                    cfields,
                    sfields
            )));
        }
        for (int t = 0, n = handles.size(); t < n; t++) {
            try {
                if (handles.get(t).get() != null) {
                    extractedCFields.addAll((ArrayList) handles.get(t).get().getKey());
                    extractedSFields.addAll((ArrayList) handles.get(t).get().getValue());
                }
            } catch (ExecutionException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchElementException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        executorService.shutdownNow();
        return new Pair(extractedCFields, extractedSFields);
    }

    @Override
    public Object extractTable(String tableSelector, List<Field> cfields, List<Field> sfields) throws InterruptedException {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();
        Iterator links = BunchOfLinks.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<Pair>> handles = new ArrayList<Future<Pair>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new SingleStaticPageExtractor(
                    baseURL + links.next(),
                    tableSelector,
                    cfields,
                    sfields
            )));
        }
        for (int t = 0, n = handles.size(); t < n; t++) {
            try {
                extractedCFields.addAll((ArrayList) handles.get(t).get().getKey());
                extractedSFields.addAll((ArrayList) handles.get(t).get().getValue());
            } catch (ExecutionException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchElementException ex) {
                Logger.getLogger(StaticHTMLWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        executorService.shutdownNow();
        return new Pair(extractedCFields, extractedSFields);
    }
}
