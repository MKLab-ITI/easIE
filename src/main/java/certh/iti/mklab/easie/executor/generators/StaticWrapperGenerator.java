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
package certh.iti.mklab.easie.executor.generators;

import certh.iti.mklab.easie.configuration.Configuration;
import certh.iti.mklab.easie.exception.PaginationException;
import certh.iti.mklab.easie.extractors.staticpages.GroupHTMLExtractor;
import certh.iti.mklab.easie.extractors.staticpages.PaginationIterator;
import certh.iti.mklab.easie.extractors.staticpages.StaticHTMLExtractor;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 * @author vasgat
 */
public class StaticWrapperGenerator extends WrapperGenerator {

    public StaticWrapperGenerator(Configuration configuration) {
        super(configuration);
    }

    @Override
    public void execute() throws InterruptedException, PaginationException, URISyntaxException, IOException {
        if (configuration.url.relative_url != null) {
            StaticHTMLExtractor wrapper = new StaticHTMLExtractor(
                    configuration.url.base_url,
                    configuration.url.relative_url
            );

            if (configuration.next_page_selector != null) {
                PaginationIterator pagination = new PaginationIterator(
                        configuration.url.base_url,
                        configuration.url.relative_url,
                        configuration.next_page_selector
                );
                extraction_handler.execute(pagination, configuration);
            } else {
                extraction_handler.execute(wrapper, configuration);
            }
        } else if (configuration.group_of_urls != null && configuration.url.base_url != null && configuration.url.relative_url == null) {
            if (configuration.next_page_selector != null) {
                throw new PaginationException("Pagination can not be conducted in Group Wrapper mode");
            }
            GroupHTMLExtractor wrapper = new GroupHTMLExtractor(
                    configuration.group_of_urls,
                    configuration.url.base_url
            );
            
            extraction_handler.execute(wrapper, configuration);
        }
    }
}
