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
package certh.iti.mklab.easie.companymatching;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author vasgat
 */
public class CountryAbreviationsLoader {

    public HashMap<String, String> TwoLetterABR;
    public HashMap<String, String> ThreeLetterABR;
    public String source;

    public CountryAbreviationsLoader() throws IOException {
        this.TwoLetterABR = getCountry2ABR();
        this.ThreeLetterABR = getCountry3ABR();
    }

    public CountryAbreviationsLoader(String source) throws IOException {
        this.TwoLetterABR = getCountry2ABR();
        this.ThreeLetterABR = getCountry3ABR();
    }

    private HashMap<String, String> getCountry2ABR() throws FileNotFoundException, IOException {
        HashMap<String, String> ABR2Letter = new HashMap();
        BufferedReader in = new BufferedReader(new FileReader("D://NetBeans//easIE.v2//Countries_ABR.txt"));
        String line;
        while ((line = in.readLine()) != null) {
            String[] array = line.split(";");
            ABR2Letter.put(array[1], array[0].toLowerCase());
        }
        in.close();
        return ABR2Letter;
    }

    private HashMap<String, String> getCountry3ABR() throws FileNotFoundException, IOException {
        HashMap<String, String> ABR3Letter = new HashMap();
        BufferedReader in = new BufferedReader(new FileReader("D://NetBeans//easIE.v2//Countries_ABR.txt"));
        String line;
        while ((line = in.readLine()) != null) {
            String[] array = line.split(";");
            ABR3Letter.put(array[2], array[0].toLowerCase());
        }
        in.close();
        return ABR3Letter;
    }

}
