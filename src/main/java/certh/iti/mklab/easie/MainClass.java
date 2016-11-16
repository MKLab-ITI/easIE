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
package certh.iti.mklab.easie;

import certh.iti.mklab.easie.companymatching.CompanySearcher;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

/**
 *
 * @author vasgat
 */
public class MainClass {

    public static void main(String[] args) {
        MongoClient client = MongoUtils.newClient(
                "localhost",
                "admin",
                "w00d@dm1nP@ssword1t1",
                "admin"
        );

        MongoCollection companies = client.getDatabase("WikiRateDB_NEW").getCollection("Companies");
        CompanySearcher searcher = new CompanySearcher(companies);

        System.out.println(searcher.search("Adidas AG", "germany"));
        //System.out.println(searcher.search("Adidas AG", "germany", "http://www.adidas-group.com"));

    }
}
