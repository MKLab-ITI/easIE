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

import certh.iti.mklab.easie.URLPatterns;
import certh.iti.mklab.easie.configuration.Configuration.ScrapableField;
import certh.iti.mklab.easie.extractors.AbstractHTMLExtractor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * PaginationItearator object extends AbstractHTMLExtractor and is responsible
 * for extracting content that is distributed to different pages
 *
 * @author vasgat
 */
public class PaginationIterator extends AbstractHTMLExtractor {

    private String next_page_selector;
    private String base_url;
    private String relative_url;
    private int numThreads;
    private String frontPattern;
    private String rearPattern;
    private int step;
    private int startPage;
    private boolean thereisPattern;

    /**
     * Creates a new PaginationIterator
     *
     * @param next_page_selector next Page CSS selector in the page
     * @throws URISyntaxException
     * @throws IOException
     */
    public PaginationIterator(String base_url, String relative_url, String next_page_selector) throws URISyntaxException, IOException, NoSuchAlgorithmException, KeyManagementException {
        this.base_url = base_url;
        this.relative_url = relative_url;
        this.next_page_selector = next_page_selector;
        this.numThreads = 20;
        thereisPattern = thereisPattern();
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
    public List<HashMap> extractFields(List<ScrapableField> fields) throws URISyntaxException, IOException, InterruptedException, KeyManagementException {
        ArrayList<HashMap> extractedFields = new ArrayList();
        if (thereisPattern) {
            extractedFields.addAll((ArrayList) MultiThreadPagination(
                    frontPattern,
                    rearPattern,
                    null,
                    null,
                    fields
            ));
        } else {
            extractedFields.addAll(SingleThreadPagination(new SingleStaticPageExtractor(
                    base_url + relative_url, null, null, fields
            )));
        }
        return extractedFields;
    }

    /**
     * Extracts data from a table from each page until no next page exists
     *
     * @param tableSelector CSS table selector
     * @param fields        List of table fields we want to extract
     * @return the extracted table fields as a List of HashMaps
     * @throws URISyntaxException
     * @throws IOException
     * @throws Exception
     */
    @Override
    public List<HashMap> extractTable(String tableSelector, List<ScrapableField> fields) throws URISyntaxException, IOException, InterruptedException, KeyManagementException {
        ArrayList<HashMap> extractedFields = new ArrayList();

        if (thereisPattern) {
            extractedFields.addAll((ArrayList) MultiThreadPagination(
                    frontPattern,
                    rearPattern,
                    tableSelector,
                    null,
                    fields
            ));
        } else {
            extractedFields.addAll(SingleThreadPagination(new SingleStaticPageExtractor(
                    base_url + relative_url, tableSelector, null, fields
            )));
        }
        return extractedFields;
    }

    @Override
    public Pair extractFields(List<ScrapableField> cfields, List<ScrapableField> sfields) throws URISyntaxException, IOException, InterruptedException, KeyManagementException {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();
        ArrayList temp;
        if (thereisPattern) {
            temp = (ArrayList) MultiThreadPagination(
                    frontPattern,
                    rearPattern,
                    null,
                    cfields,
                    sfields
            );
        } else {
            temp = SingleThreadPagination(new SingleStaticPageExtractor(
                    base_url + relative_url, null, cfields, sfields
            ));
        }
        for (int i = 0; i < temp.size(); i++) {
            extractedCFields.addAll((ArrayList) (((Pair) temp.get(i)).getKey()));
            extractedSFields.addAll((ArrayList) (((Pair) temp.get(i)).getValue()));
        }
        return Pair.of(extractedCFields, extractedSFields);
    }

    @Override
    public Pair extractTable(String tableSelector, List<ScrapableField> cfields, List<ScrapableField> sfields) throws URISyntaxException, IOException, InterruptedException, KeyManagementException {
        ArrayList<HashMap> extractedCFields = new ArrayList();
        ArrayList<HashMap> extractedSFields = new ArrayList();
        ArrayList temp;

        if (thereisPattern) {
            temp = (ArrayList) MultiThreadPagination(
                    frontPattern,
                    rearPattern,
                    tableSelector,
                    cfields,
                    sfields
            );
        } else {
            temp = SingleThreadPagination(new SingleStaticPageExtractor(
                    base_url + relative_url, tableSelector, cfields, sfields
            ));
        }
        for (int i = 0; i < temp.size(); i++) {
            extractedCFields.addAll((ArrayList) (((Pair) temp.get(i)).getKey()));
            extractedSFields.addAll((ArrayList) (((Pair) temp.get(i)).getValue()));
        }
        return Pair.of(extractedCFields, extractedSFields);
    }

    private boolean thereisPattern() throws IOException, URISyntaxException, KeyManagementException, NoSuchAlgorithmException {
        try {
            StaticHTMLExtractor wrapper = new StaticHTMLExtractor(base_url, relative_url);
            Document document = (Document) wrapper.fetcher.getHTMLDocument();
            if (!document.select(next_page_selector).attr("href").equals("")) {
                String url1 = base_url + document.select(next_page_selector).attr("href").replace(base_url, "");
                document = Jsoup.connect(new URI(base_url + document.select(next_page_selector).attr("href").replace(wrapper.base_url, "")).toASCIIString())
                        .userAgent("Mozilla/37.0").timeout(60000).get();
                String url2 = base_url + document.select(next_page_selector).attr("href").replace(base_url, "");
                frontPattern = URLPatterns.frontPattern(url1, url2);
                rearPattern = URLPatterns.rearPattern(url1, url2);
                if (URLPatterns.isInteger(url2.replace(frontPattern, "").replace(rearPattern, ""))) {
                    int temp1 = Integer.parseInt(url1.replace(frontPattern, "").replace(rearPattern, ""));
                    int temp2 = Integer.parseInt(url2.replace(frontPattern, "").replace(rearPattern, ""));
                    step = temp2 - temp1;
                    startPage = temp2 - 2 * step;
                }
                return URLPatterns.isInteger(url2.replace(frontPattern, "").replace(rearPattern, ""));
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean thereisPattern(String url1, String url2) throws IOException, URISyntaxException {
        frontPattern = URLPatterns.frontPattern(url1, url2);
        rearPattern = URLPatterns.rearPattern(url1, url2);

        if (URLPatterns.isInteger(url2.replace(frontPattern, "").replace(rearPattern, ""))) {
            int temp1 = Integer.parseInt(url1.replace(frontPattern, "").replace(rearPattern, ""));
            int temp2 = Integer.parseInt(url2.replace(frontPattern, "").replace(rearPattern, ""));
            step = temp2 - temp1;
            startPage = temp2 - 2 * step;
            thereisPattern = true;
        }

        return URLPatterns.isInteger(url2.replace(frontPattern, "").replace(rearPattern, ""));
    }

    private Object MultiThreadPagination(String frontPattern, String rearPattern, String tableSelector, List<ScrapableField> cfields, List<ScrapableField> sfields) throws InterruptedException {
        ArrayList extractedFields = new ArrayList();
        boolean existNext = true;
        int i = 0;
        while (existNext) {
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            List<Future<Object>> handles = new ArrayList<Future<Object>>();
            for (int j = startPage; j <= 100 * step; j = j + step) {
                int pagenum = i * 100 * step + j;

                handles.add(executorService.submit(new SingleStaticPageExtractor(
                        frontPattern + pagenum + rearPattern,
                        tableSelector,
                        cfields,
                        sfields
                )));
            }
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.DAYS);
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
                        if (pair != null && ((ArrayList) pair.getKey()).size() > 0) {
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
                } catch (NullPointerException wx) {

                }
            }
            executorService.shutdownNow();
            i++;

        }
        return extractedFields;
    }

    private ArrayList SingleThreadPagination(SingleStaticPageExtractor singlePageExtractor) throws URISyntaxException, IOException, KeyManagementException {
        ArrayList extractedFields = new ArrayList();
        Object result = singlePageExtractor.call();
        if (result instanceof ArrayList) {
            extractedFields.addAll((ArrayList) singlePageExtractor.call());
        } else {
            extractedFields.add((Pair) singlePageExtractor.call());
        }
        while (!singlePageExtractor.document.select(next_page_selector).attr("href").equals("")) {
            singlePageExtractor.page = base_url + singlePageExtractor.document.select(next_page_selector).attr("href").replace(base_url, "");
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
