package easIE.src.Executors;

import easIE.src.Configure.Configuration;
import easIE.src.Configure.EventType;
import easIE.src.Configure.RepetitionType;
import easIE.src.Wrappers.AbstractWrapper;
import easIE.src.Wrappers.BunchWrapper;
import easIE.src.Wrappers.DynamicHTMLWrapper;
import easIE.src.Wrappers.PaginationIterator;
import easIE.src.Wrappers.StaticHTMLWrapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;

/**
 * WrapperExecutor extracts contents as defined in Configuration object
 *
 * @author vasgat
 */
public class WrapperExecutor {

    private Configuration config;
    private ArrayList<HashMap> snippetFields;
    private ArrayList<HashMap> companyFields;

    /**
     * Creates WrapperExecutor Object that generates a wrapper for an HTML page
     * or a set of HTML pages based the Configuration
     *
     * @param config
     * @throws URISyntaxException
     * @throws IOException
     * @throws Exception
     */
    public WrapperExecutor(Configuration config) throws URISyntaxException, IOException, Exception {
        this.config = config;
        this.companyFields = new ArrayList<HashMap>();
        this.snippetFields = new ArrayList<HashMap>();
        WrapperGenerator();
    }

    /**
     * Stores the extracted data in database or in drive based on the
     * Configuration
     *
     * @throws Exception
     */
    public void StoreScrapedInfo() throws Exception {
        SnippetHandler handler = new SnippetHandler(companyFields, snippetFields);
        handler.store(config.store, config.source_name);
    }

    /**
     * Generates and executes a Wrapper
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws Exception
     */
    private void WrapperGenerator() throws URISyntaxException, IOException, Exception {
        if (config.dynamicHTML) {
            DynamicHTMLWrapper wrapper = null;
            if (config.url.baseURL != null && config.url.relativeURL != null) {
                wrapper = new DynamicHTMLWrapper(config.url.baseURL, config.url.relativeURL);
            } else if (config.url.fullURL != null) {
                wrapper = new DynamicHTMLWrapper(config.url.fullURL);
            }
            if (config.event.sequence_of_events != null) {
                for (int i = 0; i < config.event.sequence_of_events.size(); i++) {
                    if (config.event.sequence_of_events.get(i).equals(EventType.CLICK)) {
                        wrapper.clickEvent(config.event.sequence_of_selectors.get(i));
                    } else if (config.event.sequence_of_events.get(i).equals(EventType.SCROLL_DOWN)) {
                        wrapper.scrollDownEvent();
                    }
                    if (config.table_selector != null) {
                        execExtractTable(wrapper);
                    } else {
                        execExtractFields(wrapper);
                    }
                }
            } else if (config.event.type.equals(EventType.CLICK)) {
                if (config.event.repetition_type.equals(RepetitionType.AFTER_ALL_EVENTS)) {
                    for (int i = 0; i < config.event.timesToRepeat; i++) {
                        wrapper.clickEvent(config.event.selector);
                    }
                    if (config.table_selector != null) {
                        execExtractTable(wrapper);
                    } else {
                        execExtractFields(wrapper);
                    }

                } else if (config.event.repetition_type.equals(RepetitionType.AFTER_EACH_EVENT)) {
                    for (int i = 0; i < config.event.timesToRepeat; i++) {
                        wrapper.clickEvent(config.event.selector);
                        if (config.table_selector != null) {
                            execExtractTable(wrapper);
                        } else {
                            execExtractFields(wrapper);
                        }
                    }
                }
                wrapper.quit();
            } else if (config.event.type.equals(EventType.SCROLL_DOWN)) {
                if (config.event.timesToRepeat == null) {
                    wrapper.scrollDownEvent();
                    if (config.table_selector != null) {
                        execExtractTable(wrapper);
                    } else {
                        execExtractFields(wrapper);
                    }
                } else {
                    wrapper.scrollDownEvent(config.event.timesToRepeat);
                    if (config.table_selector != null) {
                        execExtractTable(wrapper);
                    } else {
                        execExtractFields(wrapper);
                    }
                }
            }
        } else if (config.url.baseURL == null && config.url.relativeURL == null && config.url.fullURL != null) {
            if (config.bunch_urls != null) {
                throw new Exception("Scraping a banch of urls you need to define in the Configuration \"baseURL\" field.");
            }
            if (config.nextPageSelector != null) {
                throw new Exception("For extracting data from a set of similar Pages (next Pagination) corresponding to the same site you need to define \"baseURL\" field");
            }
            StaticHTMLWrapper wrapper = new StaticHTMLWrapper(config.url.fullURL);
            if (config.table_selector != null) {
                execExtractTable(wrapper);
            } else {
                execExtractFields(wrapper);
            }

        } else if (config.url.baseURL != null && config.url.relativeURL != null) {
            StaticHTMLWrapper wrapper = new StaticHTMLWrapper(config.url.baseURL, config.url.relativeURL);
            if (config.nextPageSelector != null) {
                PaginationIterator pagination = new PaginationIterator(config.url.baseURL, config.url.relativeURL, config.nextPageSelector);
                if (config.table_selector != null) {
                    execExtractTable(pagination);
                } else {
                    execExtractFields(pagination);
                }
            } else if (config.table_selector != null) {
                execExtractTable(wrapper);
            } else {
                execExtractFields(wrapper);
            }
        } else if (config.bunch_urls != null && config.url.baseURL != null && config.url.relativeURL == null && config.url.fullURL == null) {
            if (config.nextPageSelector != null) {
                throw new Exception("Pagination can not be conducted in Bunch Wrapper mode");
            }
            BunchWrapper wrapper = new BunchWrapper(config.bunch_urls, config.url.baseURL);
            if (config.table_selector != null) {
                execExtractTable(wrapper);
            } else {
                execExtractFields(wrapper);
            }
        } else {
            throw new Exception("Configuration error: In defining url field you need to determine either (\"baseURL\" and \"relativeURL\") or (\"fullURL\") or (\"baseURL\" alongside with \"bunch_urls\" field)");
        }
    }

    public ArrayList<HashMap> getSnippetFields() {
        return this.snippetFields;
    }

    ;
   
   public ArrayList<HashMap> getCompanyFields() {
        return this.companyFields;
    }

    ;
   
   private void execExtractTable(AbstractWrapper wrapper) throws Exception {
        if (config.company_fields != null && config.snippet_fields != null) {
            Pair temp = (Pair) wrapper.extractTable(
                    config.table_selector,
                    config.company_fields,
                    config.snippet_fields
            );
            companyFields.addAll(
                    (ArrayList<HashMap>) temp.getKey()
            );
            snippetFields.addAll(
                    (ArrayList<HashMap>) temp.getValue()
            );
        } else if (config.snippet_fields != null) {
            snippetFields.addAll(
                    (ArrayList<HashMap>) wrapper.extractTable(
                            config.table_selector,
                            config.snippet_fields
                    ));
        } else {
            throw new Exception("In the Configuration you need to define either company_fields or snippet_fields to be extracted (or both)");
        }
    }

    private void execExtractFields(AbstractWrapper wrapper) throws Exception {
        Pair temp = (Pair) wrapper.extractFields(
                config.company_fields,
                config.snippet_fields
        );
        if (config.company_fields != null) {
            companyFields.addAll(
                    (ArrayList<HashMap>) temp.getKey()
            );
            snippetFields.addAll(
                    (ArrayList<HashMap>) temp.getValue());
        } else if (config.snippet_fields != null) {
            snippetFields.addAll(
                    (ArrayList<HashMap>) wrapper.extractFields(
                            config.snippet_fields
                    ));
        } else {
            throw new Exception("In the Configuration you need to define either company_fields or snippet_fields to be extracted (or both)");
        }

    }
}
