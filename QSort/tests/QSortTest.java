import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

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
    }

}