/*
 * Copyright 2016 vasgat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package certh.iti.mklab.easie.configuration;

import certh.iti.mklab.easie.configuration.validator.ConfigurationValidator;
import certh.iti.mklab.easie.exception.IllegalSchemaException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

/**
 * ConfigurationFileReader Object reads a JSON file or a String and transforms
 * it to Configuration Object and validates it
 *
 * @author vasgat
 */
public class ConfigurationReader {

    private String content;

    public Configuration config;

    private File ConfigurationFile;

    /**
     * ConfigurationFileReader Object constructor that created Configuration
     * Object from a JSON file and validates it.
     *
     * @param configurationFile: Configuration
     * @throws FileNotFoundException in case of not valid file path
     * @throws IllegalConfigurationException in case of invalid Configuration
     * schema
     */
    public ConfigurationReader(String FilePath, String SchemaPath) throws FileNotFoundException, IOException, ProcessingException, IllegalSchemaException {
        content = IOUtils.toString(new FileInputStream(new File(FilePath)), "UTF-8");
        this.ConfigurationFile = new File(FilePath);        
        deserialize(SchemaPath);
    }
    
    public ConfigurationReader(String config_content) throws IOException, ProcessingException, IllegalSchemaException{
        content = config_content;
        deserialize();
    }

    private void deserialize(String SchemaPath) throws IOException, ProcessingException, IllegalSchemaException {
        ConfigurationValidator validator = new ConfigurationValidator(SchemaPath);
        validator.validate(ConfigurationFile);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Validation of configuration file:"+ConfigurationFile+" is been completed...");

        GsonBuilder gsonBuilder = new GsonBuilder();
        JSONConfigurationDeserializer deserializer = new JSONConfigurationDeserializer();
        gsonBuilder.registerTypeAdapter(Configuration.class, deserializer);
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        config = gson.fromJson(content, Configuration.class);

        System.out.println("Successful deserialization of configuration file...");
        System.out.println("------------------------------------------------------------------------");
    }
    
    private void deserialize() throws IOException, ProcessingException, IllegalSchemaException{
        ConfigurationValidator validator = new ConfigurationValidator("C:\\Users\\vasgat\\Desktop\\Scrapers");
        validator.validate(content);
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Validation of configuration file:"+ConfigurationFile+" is been completed...");
        
        GsonBuilder gsonBuilder = new GsonBuilder();
        JSONConfigurationDeserializer deserializer = new JSONConfigurationDeserializer();
        gsonBuilder.registerTypeAdapter(Configuration.class, deserializer);
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        config = gson.fromJson(content, Configuration.class);

        System.out.println("Successful deserialization of configuration file...");
        System.out.println("------------------------------------------------------------------------");
    }

    public Configuration getConfiguration() {
        return config;
    }
}
