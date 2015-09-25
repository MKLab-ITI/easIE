package easIE.src.Configure;

import java.util.ArrayList;

/**
 *
 * @author vasgat
 */
public class Event {
   public String type;
   public String selector;
   public Integer timesToRepeat;
   public ArrayList<String> sequence_of_events;
   public ArrayList<String> sequence_of_selectors;
   public String repetition_type;
   
   public Event(String type, String selector, Integer timesToRepeat, String repetition_type) throws Exception{
      if (type.equals(EventType.CLICK)){
         this.type = type;
         this.selector = selector;
         if (timesToRepeat==null){
            throw new NullPointerException("In the Configuration, you need to define timeToRepeat field in case of CLICK event");
         }
         else{
            this.timesToRepeat = timesToRepeat;
            if(repetition_type.equals(RepetitionType.AFTER_ALL_EVENTS)||repetition_type.equals(RepetitionType.AFTER_EACH_EVENT)){
               this.repetition_type = repetition_type;
            }
            else{
               throw new Exception("\"repetition_type\" should be set as AFTER_ALL_EVENTS or AFTER_EACH_EVENT");
            }
         }         
      }
      else if(type.equals(EventType.SCROLL_DOWN)){
          throw new Exception(
                 "In case of SCROLL_DOWN event there is no need to define a selector"
         );
      }
      else{
         throw new Exception(
                 "Can not have \"selector\" field if Event Type is other than CLICK"
         );
      }
   }
   
   public Event(String type, Integer timesToRepeat) throws Exception{
      if (type.equals(EventType.SCROLL_DOWN)){
         this.type = type;
         this.timesToRepeat = timesToRepeat;
      }
      else if(type.equals(EventType.CLICK)){
          throw new Exception(
                 "In case of CLICK event you need to define the location of next button with a selector"
         );
      }
      else{
         throw new Exception(
                 "False Event Type! Available Event Types: CLICK and SCROLL_DOWN"
         );
      }
   }
   
   public Event(ArrayList<String> sequence_of_events, ArrayList<String> sequence_of_selectors){
      if(sequence_of_events==null||sequence_of_selectors==null){
         throw new NullPointerException("You have to define both \"sequence_of_events\" field and \"sequence_of_selectors\" field");
      }
      else{
         this.sequence_of_events = sequence_of_events;
         this.sequence_of_selectors = sequence_of_selectors;
      }
   }
}
