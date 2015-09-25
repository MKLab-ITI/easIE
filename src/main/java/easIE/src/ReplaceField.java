package easIE.src;

import java.util.List;

/**
 * 
 * @author vasgat
 */
public class ReplaceField {
   public List<String> regex;
   public List<String> with;
   
   public ReplaceField(List<String> regex, List<String> with){
      this.regex = regex;
      this.with = with;
   }
}
