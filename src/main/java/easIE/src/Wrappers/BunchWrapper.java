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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
        this.numThreads = 20;
    }

    public BunchWrapper(HashSet<String> BunchOfLinks, String baseURL) {
        this.baseURL = baseURL;
        this.BunchOfLinks = BunchOfLinks;
        this.numThreads = 20;
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
    public ArrayList<HashMap> extractFields(final List<Field> fields) throws URISyntaxException, IOException, Exception {
        ArrayList<HashMap> extractedFields = new ArrayList();
        System.out.println(BunchOfLinks);
        final Iterator links = BunchOfLinks.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<ArrayList>> handles = new ArrayList<Future<ArrayList>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new Callable<ArrayList>() {
                public ArrayList call() throws Exception {
                    StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL + ((String) links.next()).replace(baseURL, ""));
                    return wrapper.extractFields(fields);
                }
            }));
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
    public ArrayList<HashMap> extractTable(final String tableSelector, final List<Field> fields) throws URISyntaxException, IOException, Exception {
        ArrayList<HashMap> extractedFields = new ArrayList();
        final Iterator links = BunchOfLinks.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<ArrayList>> handles = new ArrayList<Future<ArrayList>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new Callable<ArrayList>() {
                public ArrayList call() throws Exception {
                    StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL + ((String) links.next()).replace(baseURL, ""));
                    return wrapper.extractTable(tableSelector, fields);
                }

            }));
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
    public Object extractFields(final List<Field> cfields, final List<Field> sfields) {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();
        System.out.println(BunchOfLinks);
        final Iterator links = BunchOfLinks.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Future<Pair>> handles = new ArrayList<Future<Pair>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new Callable<Pair>() {
                public Pair call() throws Exception {
                    StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL + ((String) links.next()).replace(baseURL, ""));
                    return wrapper.extractFields(cfields, sfields);
                }
            }));
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

    @Override
    public Object extractTable(final String tableSelector, final List<Field> cfields, final List<Field> sfields) {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();
        final Iterator links = BunchOfLinks.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Future<Pair>> handles = new ArrayList<Future<Pair>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new Callable<Pair>() {
                public Pair call() throws Exception {
                    StaticHTMLWrapper wrapper = new StaticHTMLWrapper(baseURL + ((String) links.next()).replace(baseURL, ""));
                    return wrapper.extractTable(tableSelector, cfields, sfields);
                }
            }));
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
