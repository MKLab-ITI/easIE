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
import certh.iti.mklab.easie.exception.RelativeURLException;
import certh.iti.mklab.easie.extractors.dynamicpages.DynamicHTMLExtractor;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author vasgat
 */
public class DynamicWrapperGenerator extends WrapperGenerator {

    private String ChromeDriverPath;

    public DynamicWrapperGenerator(Configuration configuration, String ChromeDriverPath) {
        super(configuration);
        this.ChromeDriverPath = ChromeDriverPath;
    }

    @Override
    public void execute() throws InterruptedException, URISyntaxException, IOException, RelativeURLException, KeyManagementException {

        if (configuration.url.relative_url != null) {
            DynamicHTMLExtractor wrapper = new DynamicHTMLExtractor(
                    configuration.url.base_url,
                    configuration.url.relative_url,
                    ChromeDriverPath
            );
            if (configuration.events != null) {
                this.execute_events(wrapper);
            } else {
                extraction_handler.execute(wrapper, configuration);
            }

            wrapper.browser_emulator.close();
        } else if (configuration.group_of_urls != null && configuration.url.base_url != null && configuration.url.relative_url == null) {
            HashSet<String> group_of_urls = configuration.group_of_urls;

            Iterator<String> it = group_of_urls.iterator();
            while (it.hasNext()) {
                String current_url = it.next().replace(configuration.url.base_url, "");

                DynamicHTMLExtractor wrapper = new DynamicHTMLExtractor(
                        configuration.url.base_url,
                        current_url,
                        ChromeDriverPath
                );

                if (configuration.events != null) {
                    this.execute_events(wrapper);
                } else {
                    extraction_handler.execute(wrapper, configuration);
                }

                wrapper.browser_emulator.close();
            }

        } else {
            throw new RelativeURLException("relative_url is not defined");
        }
    }

    private void execute_events(DynamicHTMLExtractor wrapper) throws InterruptedException, URISyntaxException, IOException, KeyManagementException {
        if (configuration.events instanceof ArrayList) {
            ArrayList<Configuration.Event> list_of_events = (ArrayList<Configuration.Event>) configuration.events;
            for (int i = 0; i < list_of_events.size(); i++) {
                if (list_of_events.get(i).type.equals("CLICK")) {
                    int times_to_repeat = list_of_events.get(i).times_to_repeat;
                    for (int j = 0; j < times_to_repeat; j++) {
                        wrapper.browser_emulator.clickEvent(list_of_events.get(i).selector);
                    }
                    extraction_handler.execute(wrapper, configuration);
                } else if (list_of_events.get(i).type.equals("SCROLL_DOWN")) {
                    int times_to_repeat = list_of_events.get(i).times_to_repeat;
                    for (int j = 0; j < times_to_repeat; j++) {
                        wrapper.browser_emulator.scrollDownEvent();
                    }
                    extraction_handler.execute(wrapper, configuration);
                }
            }
        } else {
            Configuration.Event event = (Configuration.Event) configuration.events;
            System.out.println(event.extraction_type);
            if (event.type.equals("CLICK")) {
                if (event.extraction_type.equals("AFTER_ALL_EVENTS")) {
                    for (int i = 0; i < event.times_to_repeat; i++) {
                        wrapper.browser_emulator.clickEvent(event.selector);
                    }
                    extraction_handler.execute(wrapper, configuration);
                } else {
                    extraction_handler.execute(wrapper, configuration);
                    for (int i = 0; i < event.times_to_repeat; i++) {
                        wrapper.browser_emulator.clickEvent(event.selector);
                        extraction_handler.execute(wrapper, configuration);
                    }

                }
            } else {

                if (event.times_to_repeat != null) {
                    wrapper.browser_emulator.scrollDownEvent(event.times_to_repeat);
                } else {
                    wrapper.browser_emulator.scrollDownEvent();
                }
                extraction_handler.execute(wrapper, configuration);
            }
        }
    }
}
