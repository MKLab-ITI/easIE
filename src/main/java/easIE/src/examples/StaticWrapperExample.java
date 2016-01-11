package easIE.src.examples;

import easIE.src.Executors.SnippetHandler;
import easIE.src.Field;
import easIE.src.Wrappers.StaticHTMLWrapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author vasgat
 */
public class StaticWrapperExample {

    public static void main(String[] args) throws IOException, URISyntaxException, Exception {
        Field company_name = new Field(
                "Company Name",
                "td:nth-child(1)",
                "RAW_TEXT",
                "CSS",
                "TEXT",
                "TEXT"
        );
        Field fortune_rank = new Field(
                "Fortune Rank",
                "td:nth-child(2)",
                "RAW_TEXT",
                "CSS",
                "TEXT",
                "TEXT"
        );

        Field year = new Field(
                "citeyear",
                "2011",
                "RAW_TEXT",
                "RAW_TEXT",
                "TEXT",
                "TEXT"
        );
        List<Field> company_fields = new ArrayList();
        company_fields.add(company_name);

        List<Field> snippet_fields = new ArrayList();
        snippet_fields.add(fortune_rank);
        snippet_fields.add(year);

        StaticHTMLWrapper wrapper = new StaticHTMLWrapper("http://www.goodcompanyindex.com", "/wp/wp-content/themes/hulk/company_summary.php?yr=2011");

        Pair extractedData = wrapper.extractTable("#myTable > tbody:nth-child(2) > tr", company_fields, snippet_fields);

        SnippetHandler handler = new SnippetHandler((ArrayList) extractedData.getKey(), (ArrayList) extractedData.getValue());

        System.out.println(handler.getJSON());
    }

}
