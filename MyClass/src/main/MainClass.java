package main;

/**
 * Created by Aldor on 18.05.2016.
 */
public class MainClass {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        AboutJava object = new AboutJava();
        object.printReleaseData();
        System.out.println();

        Object loadedObj = Class.forName("extra.LoadedClass").newInstance();
        System.out.println(loadedObj.toString() + " == " + loadedObj.getClass());
    }
}
