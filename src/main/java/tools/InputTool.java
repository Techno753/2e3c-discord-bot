package tools;

import java.util.Scanner;

/**
 * Class for providing input methods
 */
public final class InputTool {
    public static String inputFromTerminal() {
        String in;
        Scanner sc = new Scanner(System.in);
        return sc.next();
    }
}
