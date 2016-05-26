package IOHelp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Aldor on 26.05.2016.
 */
public class IOHelper {
    public static void writeln(String arg) {
        System.out.println(arg);
    }

    public static String readln() {
        String s="";
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    private IOHelper() {}
    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
}
