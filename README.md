# easIE
easy Information Extraction: is an easy-to-use information extraction framework that extracts data about companies from heterogeneous Web sources in a semi-automatic manner. It allows admin users to extract data about companies from heterogeneous Web sources in a semi-automatic manner by only defining a configuration file. The framework is quickly and simply generating Web Information Extractors and Wrappers. easIE offers a set of wrappers for obtaining content from Static and Dynamic HTML pages by pointing to the html elements using css Selectors.

<h2><u>Getting started</u></h2>

Each extractor extends AbstractHTMLExtractor and implements the `extractFields(List<ScrapableField> fields)` and `extractTable(String table_selector, List<ScrapableField> fields)` methods. There are four objects that extend AbstractHTMLExtractor:

1. `StaticHTMLExtractor` is responsible for extracting content from static HTML pages:

          StaticHTMLExtractor extractor = new StaticHTMLExtractor(base_url, relative_url);
          extractor.extractFields(fields);

2. `DynamicHTMLExtractor` is responsible for executing a number of events to a dynamic HTML page and extracting the defined contents: 

          DynamicHTMLExtractor extractor = new DynamicHTMLWrapper(base_url, relative_url, chrome_driver_path);
          extractor.browser_emulator.clickEvent(css_selector);
          extractor.extractFields(fields);

3. `GroupHTMLExtractor` is responsible for extracting content from a group of static HTML pages with similar structure:

          GroupHTMLExtractor extractor = new GroupHTMLExtractor(group_of_pages);
          extractor.extractFields(fields);
          
4. `PaginationIterator` is responsible for extracting data that are distributed in different pages:

          PaginationIterator extractor = new PaginationIterator(base_url, relative_url, next_page_selector);
          extractor.extractFields(fields);
          
