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
import java.io.IOException;
import java.net.URISyntaxException;
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
    private List<ScrapableField> cfields;
    private List<ScrapableField> sfields;
    public Document document;


    public SingleStaticPageExtractor(String url, String tableSelector, List<ScrapableField> cfields, List<ScrapableField> sfields) {
        this.page = url;
        this.tableSelector = tableSelector;
        this.cfields = cfields;
        this.sfields = sfields;
    }

    @Override
    public Object call() throws URISyntaxException, IOException {
        try {
            StaticHTMLExtractor wrapper = new StaticHTMLExtractor(page);
            document = (Document) wrapper.fetcher.getHTMLDocument();
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
            System.out.println(ex.fillInStackTrace());
            return null;
        }
        return null;
    }

}
