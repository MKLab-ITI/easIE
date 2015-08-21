package abstractscrapers.src.Configure;

/**
 *
 * @author vasgat
 */
public class Event {
   private EventType type;
   private String selector;
   private int timesToRepeat;
   
   public Event(EventType type, String selector, int timesToRepeat) throws Exception{
      if (type.equals(EventType.CLICK)){
         this.type = type;
         this.selector = selector;
         this.timesToRepeat = timesToRepeat;
      }
      else{
         throw new Exception(
                 "Can not have \"selector\" field if Event Type is other than CLICK"
         );
      }
   }
   
   public Event(EventType type, int timesToRepeat) throws Exception{
      if (type.equals(EventType.SCROLL_DOWN)){
         this.type = type;
         this.timesToRepeat = timesToRepeat;
      }
      else{
         throw new Exception(
                 "False Event Type! Available Event Types: CLICK and SCROLL_DOWN"
         );
      }
   }   
}
