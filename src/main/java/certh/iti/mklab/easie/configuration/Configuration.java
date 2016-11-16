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
package certh.iti.mklab.easie.configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author vasgat
 */
public final class Configuration {

    public URL url;

    public HashSet<String> group_of_urls;

    public String source_name;

    public String table_selector;

    public ArrayList<ScrapableField> metrics;

    public ArrayList<ScrapableField> company_info;

    public String next_page_selector;

    public boolean dynamic_page;

    public Object events;

    public Store store;

    public Configuration crawl;

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void setEvents(Event events) {
        this.events = events;
    }

    public static class ScrapableField {

        public Object label;

        public Object value;

        public Object citeyear;

        public void setLabel(ExtractionProperties ep) {
            label = ep;
        }

        public void setValue(ExtractionProperties ep) {
            value = ep;
        }

        public void setCiteyear(ExtractionProperties ep) {
            citeyear = ep;
        }
    }

    public static class ExtractionProperties {

        public String selector;

        public String type;

        public Object citeyear;

        public ReplaceField replace;

    }

    public static class ReplaceField {

        public List<String> regex;

        public List<String> with;
    }

    public static class Event {

        public String type;

        public String selector;

        public Integer times_to_repeat;

        public String extraction_type;
    }

    public static class URL {

        public String base_url;

        public String relative_url;
    }

    public class Store {

        public String format;

        public String database;

        public String hd_path;

        public String companies_collection;

        public String metrics_collection;

        public DBCreadentials db_credentials;
    }

    public class DBCreadentials {

        public String username;

        public String password;

        public String server_address;

        public String db;
    }
}
