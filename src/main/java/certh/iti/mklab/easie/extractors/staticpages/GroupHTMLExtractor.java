/*
 * Copyright 2016 vasgat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package certh.iti.mklab.easie.extractors.staticpages;

import certh.iti.mklab.easie.configuration.Configuration.ScrapableField;
import certh.iti.mklab.easie.extractors.AbstractHTMLExtractor;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;

/**
 * BunchWrapper object extends AbstractWrapper and is responsible for extracting
 * data from a set of links - webpages with the same structure
 *
 * @author vasgat
 */
public class GroupHTMLExtractor extends AbstractHTMLExtractor {

    private HashSet<String> group_of_urls;
    private String base_url;
    private int numThreads;

    /**
     * Creates a new BunchWrapper
     *
     * @param group_of_urls a set of webpages
     */
    public GroupHTMLExtractor(HashSet<String> group_of_urls) {
        this.base_url = "";
        this.group_of_urls = group_of_urls;
        this.numThreads = 10;
    }

    public GroupHTMLExtractor(HashSet<String> group_of_urls, String base_url) {
        this.base_url = base_url;
        this.group_of_urls = group_of_urls;
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
    public List<Document> extractFields(List<ScrapableField> fields) throws URISyntaxException, IOException {
        ArrayList<Document> extractedFields = new ArrayList();

        Iterator links = group_of_urls.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        List<Future<ArrayList>> handles = new ArrayList<Future<ArrayList>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new SingleStaticPageExtractor(
                    base_url + links.next().toString().replace(base_url, ""),
                    null,
                    null,
                    fields
            )));
        }
        for (int t = 0, n = handles.size(); t < n; t++) {
            try {
                extractedFields.addAll(handles.get(t).get());
            } catch (ExecutionException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchElementException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
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
    public List<Document> extractTable(String tableSelector, List<ScrapableField> fields) throws URISyntaxException, IOException {
        ArrayList<Document> extractedFields = new ArrayList();

        Iterator links = group_of_urls.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        List<Future<ArrayList>> handles = new ArrayList<Future<ArrayList>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new SingleStaticPageExtractor(
                    base_url + links.next().toString().replace(base_url, ""),
                    tableSelector,
                    null,
                    fields
            )));
        }
        for (int t = 0, n = handles.size(); t < n; t++) {
            try {
                extractedFields.addAll(handles.get(t).get());
            } catch (ExecutionException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchElementException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        executorService.shutdownNow();
        return extractedFields;
    }

    @Override
    public Pair extractFields(List<ScrapableField> cfields, List<ScrapableField> sfields) throws InterruptedException {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();

        Iterator links = group_of_urls.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<Future<Pair>> handles = new ArrayList<Future<Pair>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new SingleStaticPageExtractor(
                    base_url + links.next().toString().replace(base_url, ""),
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
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchElementException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        executorService.shutdownNow();
        return Pair.of(extractedCFields, extractedSFields);
    }

    @Override
    public Pair extractTable(String tableSelector, List<ScrapableField> cfields, List<ScrapableField> sfields) throws InterruptedException {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();

        Iterator links = group_of_urls.iterator();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<Future<Pair>> handles = new ArrayList<Future<Pair>>();
        while (links.hasNext()) {
            handles.add(executorService.submit(new SingleStaticPageExtractor(
                    base_url + links.next().toString().replace(base_url, ""),
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
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchElementException ex) {
                Logger.getLogger(StaticHTMLExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        executorService.shutdownNow();
        return Pair.of(extractedCFields, extractedSFields);
    }
}
