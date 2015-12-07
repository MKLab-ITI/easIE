/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easIE.src;

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
    
    public CountryAbreviationsLoader() throws IOException{
        this.TwoLetterABR = getCountry2ABR();
        this.ThreeLetterABR = getCountry3ABR();
        System.out.println("Abreviations were loaded...");
    }
    
    public CountryAbreviationsLoader(String source) throws IOException{
        this.TwoLetterABR = getCountry2ABR();
        this.ThreeLetterABR = getCountry3ABR();
        System.out.println("Abreviations were loaded...");
    }
    
    private HashMap<String, String> getCountry2ABR() throws FileNotFoundException, IOException {
        HashMap<String, String> ABR2Letter = new HashMap();
        BufferedReader in = new BufferedReader(new FileReader("D://NetBeans//AbstractScrapers//src//main//java//easIE//src//data//Countries_ABR.txt"));
        String line;
        while ((line = in.readLine()) != null) {
            String[] array = line.split(";");
            ABR2Letter.put(array[1], array[0]);
        }
        in.close();
        return ABR2Letter;
    }
    
    private HashMap<String, String> getCountry3ABR() throws FileNotFoundException, IOException {
        HashMap<String, String> ABR3Letter = new HashMap();
        BufferedReader in = new BufferedReader(new FileReader("D://NetBeans//AbstractScrapers//src//main//java//easIE//src//data//Countries_ABR.txt"));
        String line;
        while ((line = in.readLine()) != null) {
            String[] array = line.split(";");
            ABR3Letter.put(array[2], array[0]);
        }
        in.close();
        return ABR3Letter;
    }
    
}
