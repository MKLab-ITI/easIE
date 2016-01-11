package easIE.src.examples;

import easIE.src.Configure.Configuration;
import easIE.src.Configure.ConfigurationFileReader;
import easIE.src.Configure.IllegalConfigurationException;
import easIE.src.Executors.WrapperExecutor;
import easIE.src.Executors.SnippetHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author vasgat
 */
public class RunConfigurationFile {

    public static void main(String[] args) throws FileNotFoundException, IllegalConfigurationException, Exception {
    	
            ConfigurationFileReader reader = new ConfigurationFileReader(new File("src/main/java/ConfigurationFiles/RSPO.json"));

            Configuration config = reader.getConfiguration();

            WrapperExecutor executor = new WrapperExecutor(config);

            ArrayList<HashMap> cf = executor.getCompanyFields();
            ArrayList<HashMap> sf = executor.getSnippetFields();

            SnippetHandler handler = new SnippetHandler(cf, sf);
            handler.store(config.store, config.source_name);        
    }

}
