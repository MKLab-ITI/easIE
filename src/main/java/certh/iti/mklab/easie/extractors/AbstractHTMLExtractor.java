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
package certh.iti.mklab.easie.extractors;

import certh.iti.mklab.easie.configuration.Configuration.ScrapableField;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

/**
 * AbstractHTMLExtractor Object
 * @author vasgat
 */
public abstract class AbstractHTMLExtractor {
   
   public abstract List extractFields(List<ScrapableField> fields) throws URISyntaxException, IOException, InterruptedException, KeyManagementException;
   
   public abstract List extractTable(String tableSelector, List<ScrapableField> fields) throws URISyntaxException, IOException, InterruptedException, KeyManagementException;
   
   public abstract Pair extractFields(List<ScrapableField> cfields, List<ScrapableField> sfields) throws URISyntaxException, IOException, InterruptedException, KeyManagementException;
   
   public abstract Pair extractTable(String tableSelector, List<ScrapableField> cfields, List<ScrapableField> sfields) throws URISyntaxException, IOException, InterruptedException, KeyManagementException;
}
