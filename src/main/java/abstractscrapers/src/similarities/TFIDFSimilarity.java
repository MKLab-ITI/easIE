package abstractscrapers.src.similarities;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author vasgat
 */
public class TFIDFSimilarity {
    
    public static double calculate(HashMap<String, HashMap<String,Double>> tfidf, String doc1_id, String doc2_id){
        return CosineSimilarity.calculate(tfidf, doc1_id, doc2_id);
    }
    
    public static HashMap<String, HashMap<String, Double>> TF_IDF(HashMap<String, HashMap<String,Integer>> docs){
        HashMap<String, HashMap<String, Double>> tf_idf = new HashMap<String, HashMap<String, Double>>();
        HashMap<String, HashMap<String, Double>> tf = tf(docs);
        HashMap<String, Double> idf = idf(df(docs), docs.size());
        
        Iterator it = tf.entrySet().iterator();
        while(it.hasNext()){
            HashMap<String, Double> termScore = new HashMap<String, Double>();
            Map.Entry<String, HashMap<String, Double>> entry = (Map.Entry<String, HashMap<String, Double>>) it.next();
            Iterator it2 = entry.getValue().entrySet().iterator();
            while(it2.hasNext()){
                Map.Entry<String, Double> docTerms = (Map.Entry<String, Double>) it2.next();
                termScore.put(docTerms.getKey(), docTerms.getValue()*idf.get(docTerms.getKey()));
            }
            tf_idf.put(entry.getKey(), termScore);
        }
        return tf_idf;
    }
    
    public static HashMap<String, HashMap<String, Double>> tf(HashMap<String, HashMap<String, Integer>> docs){
        HashMap<String, HashMap<String, Double>> docstf = new HashMap<String, HashMap<String, Double>>();
        Iterator it = docs.entrySet().iterator();
        while(it.hasNext()){
            HashMap<String, Double> tf = new HashMap<String, Double>();
            Map.Entry<String, HashMap<String, Integer>> entry = (Map.Entry<String, HashMap<String, Integer>>) it.next();
            Iterator it2 = entry.getValue().entrySet().iterator();
            while(it2.hasNext()){
                Map.Entry<String, Integer> docTerms = (Map.Entry<String, Integer>) it2.next();
                tf.put(docTerms.getKey(), 1+Math.log(docTerms.getValue()));
            }
            docstf.put(entry.getKey(), tf);
        }
        return docstf;
    }
    
    public static HashMap<String, Double> idf(HashMap<String, Integer> df, int nufOfDocs){
        HashMap<String, Double> idf = new HashMap<String, Double>();
        Iterator it = df.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, Integer> term = (Map.Entry<String, Integer>) it.next();
            idf.put(term.getKey(), Math.log10((1.0*nufOfDocs)/(1.0*df.get(term.getKey()))));
        }
        return idf;
    }
    
    public static HashMap<String, Integer> df(HashMap<String, HashMap<String, Integer>> docs){
        HashMap<String, Integer> df = new HashMap<String, Integer>();        
        Iterator it = docs.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String, HashMap<String, Integer>> entry = (Map.Entry<String, HashMap<String, Integer>>) it.next();
            Iterator it2 = entry.getValue().entrySet().iterator();
            while(it2.hasNext()){
                Map.Entry<String, Integer> terms = (Map.Entry<String, Integer>) it2.next();
                if(df.containsKey(terms.getKey())){
                    df.put(terms.getKey(), df.get(terms.getKey())+1);
                }
                else{
                    df.put(terms.getKey(), 1);
                }
            }
        }
        return df;
    }    
}
