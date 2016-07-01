import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class QSortTest {
    @Test
    public void sortArray() throws InterruptedException, ExecutionException {
        Integer[] a1 = { 8,13,2,10,6,14,4,5,7,12,11,9,3,15,1,0 };
        Integer[] a2 = { 5,2,6,6,1,7,3,3,3,0 };

        QSort<Integer> q = new QSort<>((o1, o2) -> o1-o2);

        Integer[] a1a = Arrays.copyOf(a1, a1.length);
        Integer[] a2a = Arrays.copyOf(a2, a2.length);
        Integer[] a1b = Arrays.copyOf(a1, a1.length);
        Integer[] a2b = Arrays.copyOf(a2, a2.length);

        System.out.println("Synchronous sorting");
        q.sort(a1a);
        System.out.println(Arrays.toString(a1a));

        q.sort(a2a);
        System.out.println(Arrays.toString(a2a));

        QSort2<Integer> q2 = new QSort2<>((o1, o2) -> o1-o2);

        Future<Integer[]> sorter1 = q2.sort(a1b);
        Future<Integer[]> sorter2 = q2.sort(a2b);
        System.out.print("Asynchronous sorting");
        while (! (sorter1.isDone() && sorter2.isDone())) {
            try {
                sorter1.get(200, TimeUnit.MILLISECONDS);
                sorter2.get(200, TimeUnit.MILLISECONDS);
            } catch (TimeoutException to) {
            System.out.print('.');
            }
        }
        System.out.println();
        System.out.println(Arrays.toString(a1b));
        System.out.println(Arrays.toString(a2b));
    }

    @Test
    public void LargeQSortTest() throws Exception {
        Integer[] MilInt1;
        Integer[] MilInt2;

        System.out.println("Current default path is " + Paths.get("").toAbsolutePath().toString());
        System.out.print("Reading input file");
        Future<String> ints = TextFileNonblockingIO.readFileIntoString("million_ints.txt");
        String s;
        while(true) try {
            s = ints.get(200, TimeUnit.MILLISECONDS);
            break;
        } catch (TimeoutException to) {
            System.out.print('.');
        }
        System.out.println();
        MilInt1 = Arrays.stream(s.split(",")).map(Integer::valueOf).toArray(Integer[]::new);
        int million = MilInt1.length;
        MilInt2 = new Integer[million];

        QSort<Integer> qS = new QSort<>((o1, o2) -> o1-o2);
        QSort2<Integer> qA = new QSort2<>((o1, o2) -> o1-o2);

        System.out.println("Synchronous sorting");

        long d1 = new Date().getTime();
        for (int i = 0; ++i < 50; ) {
            System.arraycopy(MilInt1, 0, MilInt2, 0, million);
            qS.sort(MilInt2);
        }
        d1 = new Date().getTime() - d1;
        System.out.printf("Sorting of a %d-element array took %d ms\n", MilInt1.length, d1);
        TextFileNonblockingIO.writeStringIntoFile("d:\\out_sync.txt", Arrays.toString(MilInt2));

        System.out.print("Asynchronous sorting");

        d1 = new Date().getTime();
        for (int i = 0; ++i < 50; ) {
            System.arraycopy(MilInt1, 0, MilInt2, 0, million);

            qA.sort(MilInt2).get();
        }
        d1 = new Date().getTime() - d1;
        System.out.printf("\nSorting of the same array took %d ms\n", d1);
        TextFileNonblockingIO.writeStringIntoFile("d:\\out_async.txt", Arrays.toString(MilInt2)).get();
    }
}