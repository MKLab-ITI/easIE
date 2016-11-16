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
package certh.iti.mklab.easie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A number of function applied on Map Objects
 *
 * @author vasgat
 */
public final class MapFunctionsUtils {

    /**
     * Sorts a Map object by value
     *
     * @returns the ordered HashMap
     */
    public static <K, V extends Comparable<? super V>> Map<K, String>
            sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list
                = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, String> result = new LinkedHashMap<K, String>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), "\"" + entry.getValue() + "\"");
        }
        return result;
    }

    /**
     * Selects the top-k (key,value) pairs of a Map based on value of the value
     */
    public static Map getTopValues(Map unsortMap, Integer selectTop) {
        List list = new LinkedList(unsortMap.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map sortedMap = new LinkedHashMap();
        int counter = 0;
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
            counter++;
            if (counter == selectTop) {
                break;
            }
        }
        return sortedMap;
    }

    public static Map getTopValues2(Map unsortMap, Integer selectTop) {
        List list = new LinkedList(unsortMap.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        Map sortedMap = new LinkedHashMap();
        int counter = 0;
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
            counter++;
            if (counter == selectTop) {
                break;
            }
        }
        return sortedMap;
    }

    /**
     * Returns a sub-TreeMap from index i to j.
     *
     * @param startIndex
     * @param endIndex
     * @param treeMap
     * @returns the sub-TreeMap
     */
    public static TreeMap subTreeMap(int startIndex, int endIndex, TreeMap treeMap) {
        Iterator it = treeMap.entrySet().iterator();
        TreeMap newMap = new TreeMap();
        ArrayList list = new ArrayList<Map.Entry>(treeMap.entrySet());
        for (int i = startIndex; i <= endIndex; i++) {
            Map.Entry entry = (Map.Entry) list.get(i);
            newMap.put(entry.getKey(), entry.getValue());
        }

        return newMap;
    }
}
