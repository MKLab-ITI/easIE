package abstractscrapers.src;

import java.util.HashMap;

/**
 *
 * @author vasgat
 */
public class Tokenizer {  
     
   public static String[] getTokens(String text){      
      text = text.replaceAll("[^a-zA-Z ]", "").toLowerCase();
      return text.split("\\s");
   }   
   
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
   
}
