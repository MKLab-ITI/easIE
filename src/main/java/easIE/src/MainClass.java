package easIE.src;

import easIE.src.Configure.Configuration;
import easIE.src.Configure.ConfigurationFileReader;
import easIE.src.Configure.IllegalConfigurationException;
import easIE.src.Executors.SnippetHandler;
import easIE.src.Executors.WrapperExecutor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.tika.Tika;
import org.json.JSONArray;

/**
 *
 * @author vasgat
 */
public class MainClass {

    public static void main(String[] args) throws FileNotFoundException, IllegalConfigurationException, Exception {
        if (args.length == 1) {
            FileInputStream is = null;
            try {
                Tika tika = new Tika();
                String mediaType = tika.detect(args[0]);
                if (mediaType.equals("application/json")) {
                    System.out.println("Scrape started sucessfully. Please wait...");

                    ConfigurationFileReader reader = new ConfigurationFileReader(new File(args[0]));
                    Configuration configuration = reader.getConfiguration();
                    WrapperExecutor executor = new WrapperExecutor(configuration);

                    SnippetHandler handler = new SnippetHandler(executor.getCompanyFields(), executor.getSnippetFields());
                    if (configuration.store != null && configuration.store.toHardDrive != null) {
                        handler.store(configuration.store, configuration.source_name);
                    } else {
                        JSONArray prettyJSON = new JSONArray(handler.getJSON());
                        System.out.println(prettyJSON.toString(4));
                    }
                } else {
                    throw new IOException("Illegal filetype. Try a JSON file.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
    }
}
