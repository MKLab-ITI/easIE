package easIE.src.Configure;

/**
 * 
 * @author vasgat
 */
public class IllegalConfigurationException extends Exception {
   public IllegalConfigurationException(){
      
   }
   
   public IllegalConfigurationException(String message){
      super(message);
   }
   
   public IllegalConfigurationException(Throwable cause){
      super(cause);
   }
   
   public IllegalConfigurationException(String message, Throwable cause){
      super(message, cause);
   }
}
