package certh.iti.mklab.easie.configuration;

import certh.iti.mklab.easie.configuration.Configuration;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import certh.iti.mklab.easie.configuration.Configuration.Event;
import certh.iti.mklab.easie.configuration.Configuration.ExtractionProperties;
import certh.iti.mklab.easie.configuration.Configuration.ScrapableField;
import java.lang.reflect.Type;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * JSONConfigurationDeserializer extends JsonDeserializer and is responsible of
 * the deserialization of a JSON formated file into Configuration Object.
 * Required fields can be set through registerRequiredField method.
 *
 * @author vasgat
 */
public class JSONConfigurationDeserializer implements JsonDeserializer<Configuration> {

    @Override
    public Configuration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        Configuration configuration = new Gson().fromJson(json, Configuration.class);

        configuration.metrics = transform_metrics(configuration.metrics);

        configuration.entity_info = transform_CompanyInfo(configuration.entity_info);

        Configuration crawl = configuration.crawl;
        while (crawl != null) {
            crawl.metrics = transform_metrics(crawl.metrics);
            crawl.entity_info = transform_CompanyInfo(crawl.entity_info);
            crawl = crawl.crawl;
        }

        if (configuration.dynamic_page) {
            Object events = configuration.events;
            if (configuration.events != null) {
                if (events instanceof ArrayList) {
                    ArrayList<Event> evnts = new Gson().fromJson(
                            (new JSONArray((ArrayList<LinkedTreeMap>) events)).toString(),
                            new TypeToken<ArrayList<Event>>() {
                    }.getType());
                    configuration.setEvents(evnts);
                } else {
                    Event evnts = new Gson().fromJson(
                            (new JSONObject((LinkedTreeMap) events)).toString(), new TypeToken<Event>() {
                    }.getType());
                    configuration.setEvents(evnts);
                }
            }
        }
        return configuration;
    }

    private ArrayList<ScrapableField> transform_metrics(ArrayList<ScrapableField> metrics) {
        for (int i = 0; i < metrics.size(); i++) {
            Object label = metrics.get(i).label;
            Object value = metrics.get(i).value;
            Object citeyear = metrics.get(i).citeyear;

            if (!(label instanceof String)) {
                ExtractionProperties label_value = new Gson().fromJson(
                        (new JSONObject((LinkedTreeMap) label)).toString(),
                        new TypeToken<ExtractionProperties>() {
                }.getType());
                metrics.get(i).setLabel(label_value);
            }

            if (!(value instanceof String)) {
                ExtractionProperties value_value = new Gson().fromJson(
                        (new JSONObject((LinkedTreeMap) value)).toString(),
                        new TypeToken<ExtractionProperties>() {
                }.getType());
                metrics.get(i).setValue(value_value);
            }

            if (!(citeyear instanceof Integer) && !(citeyear instanceof Double)) {
                ExtractionProperties citeyear_value = new Gson().fromJson(
                        (new JSONObject((LinkedTreeMap) citeyear)).toString(),
                        new TypeToken<ExtractionProperties>() {
                }.getType());
                metrics.get(i).setCiteyear(citeyear_value);
            }
        }
        return metrics;
    }

    private ArrayList<ScrapableField> transform_CompanyInfo(ArrayList<ScrapableField> company_info) {
        if (company_info != null) {

            for (int i = 0; i < company_info.size(); i++) {
                Object label = company_info.get(i).label;
                Object value = company_info.get(i).value;

                if (!(label instanceof String)) {
                    ExtractionProperties label_value = new Gson().fromJson(
                            (new JSONObject((LinkedTreeMap) label)).toString(),
                            new TypeToken<ExtractionProperties>() {
                    }.getType());
                    company_info.get(i).setLabel(label_value);
                }

                if (!(value instanceof String)) {
                    ExtractionProperties value_value = new Gson().fromJson(
                            (new JSONObject((LinkedTreeMap) value)).toString(),
                            new TypeToken<ExtractionProperties>() {
                    }.getType());
                    company_info.get(i).setValue(value_value);
                }
            }
        }
        return company_info;
    }
}
