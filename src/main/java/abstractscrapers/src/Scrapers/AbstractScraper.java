/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package abstractscrapers.src.Scrapers;

import abstractscrapers.src.Field;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 *
 * @author vasgat
 */
public abstract class AbstractScraper {
   
   public abstract Object scrapeFields(List<Field> fields) throws URISyntaxException, IOException, Exception;
   
   public abstract Object scrapeTable(String tableSelector, List<Field> fields) throws URISyntaxException, IOException, Exception;
}
