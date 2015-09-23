package abstractscrapers.src.Scrapers;

import abstractscrapers.src.Field;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * AbstractScraper Object
 * @author vasgat
 */
public abstract class AbstractScraper {
   
   public abstract Object scrapeFields(List<Field> fields) throws URISyntaxException, IOException, Exception;
   
   public abstract Object scrapeTable(String tableSelector, List<Field> fields) throws URISyntaxException, IOException, Exception;
}
