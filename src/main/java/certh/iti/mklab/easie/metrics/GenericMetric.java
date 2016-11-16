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

import certh.iti.mklab.easie.MongoUtils;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
/**
 * GenericMetric extends AbstractMetric 
 * @author vasgat
 */
public class GenericMetric extends AbstractMetric{
   private String name; 
   private Object value;
   private String source; 
   private Integer citeyear; 
   public Document json;
    
   /**
    * Creates a Snippet Object
    * @param Info: a Set of key,values that constitute the snippet
    */
   public GenericMetric(String name, Object value, String source, Integer citeyear){
       this.name = name;
       this.value = value;
       this.source = source;
       this.citeyear = citeyear;
       json = getMetricDBObject();
   }
   
   public void setAdditionalField(String name, Object value){
       json.append(name, value);
   }

   /**
    * Transforms Info that snippet contains into JSON format
    * @returns DBObject
    */
   @Override
   protected Document getMetricDBObject() {
      Document metric = new Document();
      metric.append("name", name);
      metric.append("value", value);
      metric.append("source", source);
      metric.append("citeyear", citeyear);
      return metric;
   }
   
   /**
    * Store GenericMetric on mongodb database in JSON format
    * @param dbname
    * @param collection
    * @param mongo 
    */
   @Override
   public void store(MongoCollection metrics_collection) {
      MongoUtils.insertDoc(metrics_collection, getMetricDBObject());
   }
   
}
