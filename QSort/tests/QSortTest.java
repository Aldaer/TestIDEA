import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class QSortTest {
    @Test
    public void sortArray() throws Exception {
        Integer[] a1 = { 8,2,6,4,1,7,9,3,5,0 };
        Integer[] a2 = { 5,2,6,6,1,7,3,3,3,0 };

        QSort<Integer> q = new QSort<>((o1, o2) -> o1-o2);

        q.sortArray(a1, 0, 10);
        System.out.println(Arrays.toString(a1));

        q.sortArray(a2, 0, 10);
        System.out.println(Arrays.toString(a2));

        QSort2<Integer> q2 = new QSort2<>((o1, o2) -> o1-o2);

        Future<Integer[]> sorter1 = q2.sort(a1);
        Future<Integer[]> sorter2 = q2.sort(a2);
        System.out.print("Asynchronous sorting");
        while (! (sorter1.isDone() && sorter2.isDone())) {
            System.out.print('.');
            Thread.sleep(200);
        }
        System.out.println();
        System.out.println(Arrays.toString(a1));
        System.out.println(Arrays.toString(a2));
    }

}