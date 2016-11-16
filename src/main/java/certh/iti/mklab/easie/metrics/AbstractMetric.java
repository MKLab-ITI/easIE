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
package certh.iti.mklab.easie.metrics;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * AbstractMetric Object
 * @author vasgat
 */
public abstract class AbstractMetric {
      
   protected abstract Document getMetricDBObject();
   
   public abstract void store(MongoCollection metrics_collection);
}
