package easIE.src.similarities;

import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * @author vasgat
 */
public class CosineSimilarity{
    
    /** 
     * Calculates the cosine similarity between two docs, givem the collection of documents
     * @param weights
     * @param doc1_id
     * @param doc2_id
     * @returns the cosine similarity between the documents doc1 and doc2
     */
    public static double calculate(HashMap<String, HashMap<String,Double>> weights, String doc1_id, String doc2_id){
        HashMap<String, Double> doc1 = weights.get(doc1_id);
        HashMap<String, Double> doc2 = weights.get(doc2_id);
        Set<String> terms = new TreeSet<String>();
        terms.addAll(doc1.keySet());
        terms.addAll(doc2.keySet());
        Iterator it = terms.iterator();
        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;
        while(it.hasNext()){
            String term = (String) it.next();
            if(doc1.containsKey(term)&&doc2.containsKey(term)){
                sum1 += doc1.get(term)*doc2.get(term);
                sum2 += Math.pow(doc1.get(term), 2);
                sum3 += Math.pow(doc2.get(term), 2);
            }
            else if(doc1.containsKey(term)){
                sum2 += Math.pow(doc1.get(term), 2);
            }
            else if(doc2.containsKey(term)){
                sum3 += Math.pow(doc2.get(term), 2);
            }
        }
        return sum1/(Math.sqrt(sum2)*Math.sqrt(sum3));
    }
}

