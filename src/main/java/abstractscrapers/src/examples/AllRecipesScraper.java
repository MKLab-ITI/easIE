package abstractscrapers.src.examples;

import abstractscrapers.src.Field;
import abstractscrapers.src.FieldType;
import abstractscrapers.src.OutputFormatter.JSONFormatter;
import abstractscrapers.src.SelectorType;
import abstractscrapers.src.StaticWebPagesScrapers.BunchScraper;
import abstractscrapers.src.StaticWebPagesScrapers.PaginationIterator;
import abstractscrapers.src.StaticWebPagesScrapers.Scraper;
import com.mongodb.BasicDBList;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author vasgat
 */
public class AllRecipesScraper {
   private List<Field> RecipeFields;
   private List<Field> linkFields;
   
   public AllRecipesScraper(){      
      RecipeFields = new ArrayList<Field>();
      Field directions = new Field(
              "Directions",
              ".directions",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      Field rating = new Field(
              "Rating",
              ".detail-right > div:nth-child(4) > div:nth-child(1) > div:nth-child(2) > meta:nth-child(1)",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.attr = "content"
      );
      Field Ingredients = new Field(
              "Ingredients",
              ".ingred-left",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      Field Title = new Field(
              "Recipe Title",
              "#itemTitle",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.text
      );
      RecipeFields.add(directions);
      RecipeFields.add(rating);
      RecipeFields.add(Ingredients);
      RecipeFields.add(Title);
      
      linkFields = new ArrayList<Field>();
      Field RecipeLinkField = new Field(
              "RecipeLink",
              "p > a",
              SelectorType.rawtext,
              SelectorType.CSS,
              FieldType.text,
              FieldType.link
      );
      linkFields.add(RecipeLinkField);
   }
   
   public BasicDBList scrapeRecipesByIngedient(List<String> ingredients) throws URISyntaxException, IOException, Exception{
      HashSet<String> RecipeLinks = new HashSet();
      for (int i=0; i<ingredients.size(); i++){
         RecipeLinks.addAll(getRecipeLinksByIngredient(ingredients.get(i)));
      }
      
      BunchScraper bunchScraper = new BunchScraper(RecipeLinks,"http://allrecipes.com");

      return JSONFormatter.toJSONArray(bunchScraper.scrapeFields(RecipeFields));      
   }
   
   public HashSet getRecipeLinksByIngredient(String ingredient) throws URISyntaxException, IOException, Exception{
      HashSet<String> RecipeLinks = new HashSet();
      PaginationIterator PageIterator = new PaginationIterator(
              new Scraper(
                      "http://allrecipes.com",
                      "/search/default.aspx?ms=0&origin=Recipe+Search+Results&rt=r&qt=i&pqt=i&fo=0&w0="+ingredient
              ),
              "#ctl00_CenterColumnPlaceHolder_ucPager_corePager_pageNumbers > a:nth-child(9)"
      );
      ArrayList<HashMap<String, String>> recipeLinks 
                  = PageIterator.scrapeTable(".recipe-info", linkFields);
      for (int j=0; j<recipeLinks.size(); j++){
         RecipeLinks.addAll(recipeLinks.get(j).values());
      }
      return RecipeLinks;
   }
   
   public List<String> readIngredientsFile(String filePath) throws IOException{
      List<String> ingredients = new ArrayList<String>();
      BufferedReader in = new BufferedReader(new FileReader(filePath));
      String line;
      while((line = in.readLine()) != null)
      {
         String[] lineFields = line.split("\\t");
         ingredients.add(lineFields[1]);
      }
      in.close();
      ingredients.remove("ingredient name");
      return ingredients;
     }
   
   public static void main(String[] args) throws IOException, Exception{ 
      AllRecipesScraper allrecipesScraper = new AllRecipesScraper();
      List<String> ingredients = allrecipesScraper.readIngredientsFile("src//main//java//abstractscrapers//src//data//ingredients.tsv");
      PrintWriter writer = new PrintWriter("recipes.json");
      writer.println(allrecipesScraper.scrapeRecipesByIngedient(ingredients));
      writer.close();
   }
}
