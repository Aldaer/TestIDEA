/**
 * Created by Aldor on 18.05.2016.
 */

import java.io.*;

public class MyClass {
    private PrintStream out;

    public static void main(String[] args) {
        System.out.println("Hello there!");

        int a=1, b=2;
        System.out.println("1/2 = " + a/b);

        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        String s1 = null, s2=null;
        try {
            s1 = bufferRead.readLine();
            s2 = bufferRead.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("s1 = " + s1 + ", s2 = " + s2);

        System.out.println("Hex -1 = " + Integer.toHexString(-1));

        byte _b1 = (byte)Integer.parseInt(s1);
        byte _b2 = (byte)Integer.parseInt(s2);
        Byte b1 = _b1;
        Byte b2 = _b2;

        Byte b3 = 10;
        Byte b4 = 10;

        System.out.println("b1=b2? " + (b1 == b2));
        System.out.println("b3=b4? " + (b3 == b4));    }
}
