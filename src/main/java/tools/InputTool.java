package tools;

import java.util.Scanner;

/**
 * Class for providing input methods
 */
public final class InputTool {
    public static String inputFromTerminal(String msg) {
        String in;
        System.out.print(msg);
        Scanner sc = new Scanner(System.in);
        return sc.next();
    }
}
