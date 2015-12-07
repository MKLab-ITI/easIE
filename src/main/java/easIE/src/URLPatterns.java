package easIE.src;

/**
 *
 * @author vasgat
 */
public class URLPatterns {

    // return offset of first match or N if no match
    private static int searchPattern(String pat, String txt) {
        int M = pat.length();
        int N = txt.length();

        for (int i = 0; i <= N - M; i++) {
            int j;
            for (j = 0; j < M; j++) {
                if (txt.charAt(i + j) != pat.charAt(j)) {
                    break;
                }
            }
            if (j == M) {
                return i;// found at offset i
            }
        }
        return N; // not found
    }

    public static String frontPattern(String url1, String url2) {
        String frontPattern = url1;
        boolean isPattern = false;
        int n = url1.length();
        for (int i = 0; i < n; i++) {
            int offset = searchPattern(frontPattern, url2);
            if (offset != url2.length()) {
                isPattern = true;
                break;
            }
            frontPattern = (String) frontPattern.substring(0, frontPattern.length() - 1);
        }
        if (isPattern) {
            return frontPattern;
        } else {
            return "";
        }
    }

    public static String rearPattern(String url1, String url2) {
        String rearPattern = url1;
        System.out.println(url1);
        boolean isPattern = false;
        int n = url1.length();
        for (int i = n; i > 0; i--) {
            int offset = searchPattern(rearPattern, url2);
            if (offset != url2.length()) {
                isPattern = true;
                break;
            }
            rearPattern = (String) rearPattern.substring(1, rearPattern.length());
        }
        if (isPattern) {
            return rearPattern;
        } else {
            return "";
        }
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c <= '/' || c >= ':') {
                return false;
            }
        }
        return true;
    }
    
}
