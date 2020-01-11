package tools;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexTool {

    /**
     * Extracts and returns the regex groups as an ArrayList
     * @param regex Regex with grouping
     * @param in String to match regex
     * @return ArrayList of matched groups
     */
    public static ArrayList<String> getGroups(String regex, String in) {
        ArrayList<String> out = new ArrayList<>();
        Pattern pat = Pattern.compile(regex);
        Matcher m = pat.matcher(in);

        // Get groups
        if (m.matches()) {
            for (int i = 0; i < m.groupCount(); i++) {
                out.add(m.group(i+1));
            }
        }
        return out;
    }
}
