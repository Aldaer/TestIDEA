import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class QSortTest {
    @Test
    public void sortArray() throws Exception {
        Integer[] a1 = { 8,13,2,10,6,14,4,5,7,12,11,9,3,15,1,0 };
        Integer[] a2 = { 5,2,6,6,1,7,3,3,3,0 };

        QSort<Integer> q = new QSort<>((o1, o2) -> o1-o2);

        Integer[] a1a = Arrays.copyOf(a1, a1.length);
        Integer[] a2a = Arrays.copyOf(a2, a2.length);
        Integer[] a1b = Arrays.copyOf(a1, a1.length);
        Integer[] a2b = Arrays.copyOf(a2, a2.length);

        q.sort(a1a);
        System.out.println(Arrays.toString(a1a));

        q.sort(a2a);
        System.out.println(Arrays.toString(a2a));

        QSort2<Integer> q2 = new QSort2<>((o1, o2) -> o1-o2);

        Future<Integer[]> sorter1 = q2.sort(a1b);
        Future<Integer[]> sorter2 = q2.sort(a2b);
        System.out.print("Asynchronous sorting");
        while (! (sorter1.isDone() && sorter2.isDone())) {
            System.out.print('.');
            Thread.sleep(200);
        }
        System.out.println();
        System.out.println(Arrays.toString(a1b));
        System.out.println(Arrays.toString(a2b));
    }

}