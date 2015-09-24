# easyScrape
easyScrape offers a set of scrapers for obtaining content from Static and Dynamic HTML pages by pointing to the html elements using css Selectors. An additional fuctionality of easyScrape is the definition of a configuration file. Users can define a configuration file in JSON format in order to scrape content of a page by only defining this configuration file.

<h2><u>Getting started</u></h2>

Each scraper extends AbstractScraper and implements the `scrapeFields(List<Field> fields)` and `scrapeTableFields(String tableCSSselector, List<Field> fields)` methods. There are four objects that extend AbstractScraper:

1. `StaticHTMLScraper` is responsible for scraping static HTML pages:

          StaticHTMLScraper staticScraper = new StaticHTMLScraper(baseURL, relativeURL);
          staticScraper.scrapeFields(fields);

2. `DynamicHTMLScraper` is responsible for executing a number of events to a dynamic HTML page and scraping the defined contents: 

          DynamicHTMLScraper dynamicScraper = new DynamicHTMLScraper(baseURL, relativeURL);
          dymamicScraper.clickEvent(elementCSSselector);
          dynamicScraper.scrapeFields(fields);

3. `BunchScraper` is responsible for scraping a group of static HTML pages with similar structure:

          BunchScraper bunchScraper = new BunchScraper(groupOfPages);
          bunchScraper.scrapeFields(fields);
          
4. `PaginationIterator` is responsible for scraping data that are distributed in different pages:
          StaticHTMLScraper staticScraper = new StaticHTMLScraper(baseURL, relativeURL);
          PaginationIterator paginationScraper = new PaginationIterator(staticScraper, nextPageSelector);
          paginationScraper.scrapeFields(fields);
