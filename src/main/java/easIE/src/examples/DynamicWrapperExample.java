package easIE.src.examples;

import easIE.src.Executors.SnippetHandler;
import easIE.src.Field;
import easIE.src.Wrappers.DynamicHTMLWrapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author vasgat
 */
public class DynamicWrapperExample {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, Exception {
        Field company_name = new Field(
                "Company Name",
                "td:nth-child(2)",
                "RAW_TEXT",
                "CSS",
                "TEXT",
                "TEXT"
        );
        Field green_score = new Field(
                "Green Score",
                "td:nth-child(9)",
                "RAW_TEXT",
                "CSS",
                "TEXT",
                "TEXT"
        );

        Field Newsweek_Rank = new Field(
                "Newsweek Rank",
                "td:nth-child(1)",
                "RAW_TEXT",
                "CSS",
                "TEXT",
                "TEXT"
        );

        Field year = new Field(
                "citeyear",
                "2012",
                "RAW_TEXT",
                "RAW_TEXT",
                "TEXT",
                "TEXT"
        );

        List<Field> company_fields = new ArrayList();
        company_fields.add(company_name);

        List<Field> snippet_fields = new ArrayList();
        snippet_fields.add(green_score);
        snippet_fields.add(Newsweek_Rank);
        snippet_fields.add(year);

        DynamicHTMLWrapper wrapper = new DynamicHTMLWrapper("http://www.newsweek.com", "/2012/10/22/newsweek-green-rankings-2012-global-500-list.html");

        wrapper.clickEvent(".pager_top > div:nth-child(1) > ul:nth-child(2) > li:nth-child(4)");

        Pair extractedData = wrapper.extractTable("#DataTables_Table_0 > tbody:nth-child(3) > tr", company_fields, snippet_fields);

        SnippetHandler handler = new SnippetHandler((ArrayList) extractedData.getKey(), (ArrayList) extractedData.getValue());

        System.out.println(handler.getJSON());
    }

}
