/**
 * My class to test various OOP concepts
 */

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Locale;

import static IOHelp.IOHelper.readln;
import static IOHelp.IOHelper.writeln;

public class MyClass {
    private PrintStream out;

    public static void main(String[] args) {
        writeln("Hello there!");

/*        int a=1, b=2;
        writeln("1/2 = " + a/b);

        writeln("Enter 2 numbers:");
        String s1 = readln();
        String s2 = readln();

        writeln("s1 = " + s1 + ", s2 = " + s2);

        writeln("Hex -1 = " + Integer.toHexString(-1));

        byte _b1 = (byte)Integer.parseInt(s1);
        byte _b2 = (byte)Integer.parseInt(s2);
        Byte b1 = _b1;
        Byte b2 = _b2;

        Byte b3 = 10;
        Byte b4 = 10;

        writeln("b1=b2? " + (b1 == b2));
        writeln("b3=b4? " + (b3 == b4));*/

        Integer[] arrI = new Integer[5];
        for (Integer x : arrI) {
            x = 1;
        }
        System.out.println(Arrays.toString(arrI));



        ZonedDateTime zdt = ZonedDateTime.now();
        DateTimeFormatter ru = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL).withLocale(Locale.forLanguageTag("ru")).withZone(ZoneId.of("Europe/Moscow"));
        DateTimeFormatter fr = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL).withLocale(Locale.forLanguageTag("fr")).withZone(ZoneId.of("Europe/Paris"));
        DateTimeFormatter enuk = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL).withLocale(new Locale("en-gb")).withZone(ZoneId.of("GMT"));
        DateTimeFormatter enus = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL).withLocale(new Locale("en-us")).withZone(ZoneId.of("EST", ZoneId.SHORT_IDS));
        System.out.println(zdt.format(ru));
        System.out.println(zdt.format(fr));
        System.out.println(zdt.format(enuk));
        System.out.println(zdt.format(enus));


    }
}
