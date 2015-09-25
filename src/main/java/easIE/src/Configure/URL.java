package easIE.src.Configure;

/**
 *
 * @author vasgat
 */
public class URL {
   public String baseURL;
   public String relativeURL;
   public String fullURL;
   
   public URL(String baseURL, String relativeURL){
      this.baseURL = baseURL;
      this.relativeURL = relativeURL;
   }
   
   public URL(String fullURL){
      this.fullURL = fullURL;
   }
}
