package easIE.src.Wrappers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author vasgat
 */
public class StaticHTMLFetcher extends Fetcher{
    private Document document;
    
    public StaticHTMLFetcher(String baseURL, String relativeURL) throws URISyntaxException, IOException{
        document = Jsoup.connect(new URI(baseURL+relativeURL).toASCIIString())
                                        .userAgent("Mozilla/37.0").timeout(60000).get();
    }
    
    public StaticHTMLFetcher(String fullURL) throws URISyntaxException, IOException{
        document = Jsoup.connect(new URI(fullURL).toASCIIString())
                                        .userAgent("Mozilla/37.0").timeout(60000).get();
    }

    @Override
    public Object getHTMLDocument() {
        return document;
    }
}
