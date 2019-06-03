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

import certh.iti.mklab.easie.extractors.Fetcher;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author vasgat
 */
public class StaticHTMLFetcher extends Fetcher {

    private Document document;
    private int responseCode;
    private Connection connection;

    public StaticHTMLFetcher(String baseURL, String relativeURL) throws URISyntaxException, IOException {
        connection = Jsoup.connect(new URI(baseURL + relativeURL).toASCIIString()).followRedirects(true).ignoreHttpErrors(true)
                .userAgent("Mozilla/37.0").timeout(60000);
        document = connection.get();
        responseCode = connection.response().statusCode();
    }

    public StaticHTMLFetcher(String fullURL) throws URISyntaxException, IOException {
        connection = Jsoup.connect(new URI(fullURL).toASCIIString()).followRedirects(true).ignoreHttpErrors(true)
                .userAgent("Mozilla/37.0").timeout(60000);
        document = connection.get();
        responseCode = connection.response().statusCode();
    }

    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public Document getHTMLDocument() {
        return document;
    }
}
