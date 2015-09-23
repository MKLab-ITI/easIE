package abstractscrapers.src;

import java.util.HashMap;

/**
 * Tokenizer offers a set of functions for the tokenization of a given string
 * @author vasgat
 */
public class Tokenizer {  
     
    /**
     * 
     * @param text
     * @returns the tokens of the given text
     */
   public static String[] getTokens(String text){      
      text = text.replaceAll("[^a-zA-Z ]", "").toLowerCase();
      return text.split("\\s");
   }   
   
   /**
    * Transforms a text into a "bag of word"
    * @param text 
    * @returns a HashMap<String, Integer>
    */
   public static HashMap getTokenVectorFrequency(String text){
      String[] temp = getTokens(text);
      HashMap<String, Integer> tokens = new HashMap<String, Integer>();
      for (int i=0; i<temp.length; i++){
         if (tokens.containsKey(temp[i]))
            tokens.put(temp[i], tokens.get(temp[i])+1);
         else
            tokens.put(temp[i], 1);
      }
      return tokens;
   }  
   
   /**
    * Transforms a text into a "bag of word"
    * @param text
    * @returns a HashMap<String, Double> 
    */
   public static HashMap getTokenVectorFrequency2(String text){
      String[] temp = getTokens(text);
      HashMap<String, Double> tokens = new HashMap<String, Double>();
      for (int i=0; i<temp.length; i++){
         if (tokens.containsKey(temp[i]))
            tokens.put(temp[i], tokens.get(temp[i])+1.0);
         else
            tokens.put(temp[i], 1.0);
      }
      return tokens;
   }    
   
}
