/**
 * Created by Aldor on 18.05.2016.
 */

import java.io.*;

import static IOHelp.IOHelper.readln;
import static IOHelp.IOHelper.writeln;

public class MyClass {
    private PrintStream out;

    public static void main(String[] args) {
        writeln("Hello there!");

        int a=1, b=2;
        writeln("1/2 = " + a/b);

        writeln("Enter 2 numbers:");
        String s1 = readln();
        String s2 = readln();

        System.out.println("s1 = " + s1 + ", s2 = " + s2);

        System.out.println("Hex -1 = " + Integer.toHexString(-1));

        byte _b1 = (byte)Integer.parseInt(s1);
        byte _b2 = (byte)Integer.parseInt(s2);
        Byte b1 = _b1;
        Byte b2 = _b2;

        Byte b3 = 10;
        Byte b4 = 10;

        writeln("b1=b2? " + (b1 == b2));
        writeln("b3=b4? " + (b3 == b4));    }
}
