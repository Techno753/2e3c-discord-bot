package tools;

import org.apache.commons.text.StringEscapeUtils;

public final class StringTool {
    public static StringEscapeUtils seu = new StringEscapeUtils();

    public static String escapeHTML(String s) {
        return seu.unescapeHtml4(s);
    }
}
