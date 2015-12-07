# easIE
easy Information Extraction: a framework for quickly and simply generating Web Information Extractors and Wrappers.
easIE offers a set of wrappers for obtaining content from Static and Dynamic HTML pages by pointing to the html elements using css Selectors. An additional fuctionality is the definition of a configuration file. Users can define a configuration file in JSON format in order to extract content of a page by only defining this configuration file.
**Note: [Here](https://youtu.be/R5glu7lgSmo) you can find a link that leads to a tech demo**
<h2><u>Getting started</u></h2>

Each wrapper extends AbstractWrapper and implements the `extractFields(List<Field> fields)` and `extractTableFields(String tableCSSselector, List<Field> fields)` methods. There are four objects that extend AbstractWrapper:

1. `StaticHTMLWrapper` is responsible for extracting content from static HTML pages:

          StaticHTMLWrapper staticWrapper = new StaticHTMLWrapper(baseURL, relativeURL);
          staticWrapper.extractFields(fields);

2. `DynamicHTMLWrapper` is responsible for executing a number of events to a dynamic HTML page and extracting the defined contents: 

          DynamicHTMLWrapper dynamicWrapper = new DynamicHTMLWrapper(baseURL, relativeURL);
          dymamicWrapper.clickEvent(elementCSSselector);
          dynamicWrapper.extractFields(fields);

3. `BunchWrapper` is responsible for extracting content from a group of static HTML pages with similar structure:

          BunchWrapper bunchWrapper = new BunchWrapper(groupOfPages);
          bunchWrapper.extractFields(fields);
          
4. `PaginationIterator` is responsible for extracting data that are distributed in different pages:

          StaticHTMLWrapper staticWrapper = new StaticHTMLWrapper(baseURL, relativeURL);
          PaginationIterator paginationWrapper = new PaginationIterator(staticWrapper, nextPageSelector);
          paginationWrapper.extractFields(fields);
