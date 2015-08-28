/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package abstractscrapers.src;

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
