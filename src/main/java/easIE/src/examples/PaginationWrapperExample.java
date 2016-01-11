package easIE.src.examples;

import easIE.src.Executors.SnippetHandler;
import easIE.src.Field;
import easIE.src.Wrappers.PaginationIterator;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author vasgat
 */
public class PaginationWrapperExample {
    
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
                "Membership RSPO Type",
                "td:nth-child(5)",
                "RAW_TEXT",
                "CSS",
                "TEXT",
                "TEXT"
        );

        Field year = new Field(
                "RSPO Member since",
                "td:nth-child(4)",
                "RAW_TEXT",
                "CSS",
                "TEXT",
                "TEXT"
        );
        List<Field> company_fields = new ArrayList();
        company_fields.add(company_name);

        List<Field> snippet_fields = new ArrayList();
        snippet_fields.add(fortune_rank);
        snippet_fields.add(year);

        PaginationIterator wrapper = new PaginationIterator("http://www.rspo.org", "/members/all", ".next > a:nth-child(1)");

        Pair extractedData = wrapper.extractTable(".table > tbody:nth-child(2) > tr", company_fields, snippet_fields);

        SnippetHandler handler = new SnippetHandler((ArrayList) extractedData.getKey(), (ArrayList) extractedData.getValue());

        System.out.println(handler.getJSON());
    }
    
}
