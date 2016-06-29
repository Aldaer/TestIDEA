import java.util.Comparator;
import java.util.concurrent.*;
import java.util.concurrent.Future;

/**
 * 2nd version, with multithreading
 */
public class QSort2<T> {

    /**
     * Creates a sorter and sets the comparator to use on array elements
     * @param sortRule Indicates how array elements should be compared
     */
    public QSort2(Comparator<T> sortRule) {
        cmp = sortRule;
    }

    /**
     * Performs sorting of the array {@code a}. Sorting is asynchronous and in-place, so do not modify the array while the result isn't complete.
     *
     * @param arrayToSort Array to sort
     * @return Future containing sorted array
     */
    public Future<T[]> sort(final T[] arrayToSort) {
        a = arrayToSort;
        if (a == null || a.length <= 1) return CompletableFuture.completedFuture(a);
        executorService = Executors.newCachedThreadPool();
  //      return executorService.submit(new SortingThread(0, a.length), a);
        new SortingThread(0, a.length).run();
        return CompletableFuture.completedFuture(a);
    }

    private T[] a;                                          // Array to sort
    private final Comparator<T> cmp;
    private ExecutorService executorService;


    private int threadNo = 0;

    private class SortingThread implements Runnable {

        private int stIndex;
        private int endIndexP1;

        private int thisThreadNo;

        private SortingThread(int stIndex, int endIndexPlus1) {
            thisThreadNo = ++threadNo;
            this.stIndex = stIndex;
            this.endIndexP1 = endIndexPlus1;
        }

        @Override
        public void run() {
            T side;
            int len;
            int pivotIndex = endIndexP1 - 1;
            len = endIndexP1 - stIndex;
            System.out.printf("\nThread %d is sorting array between %d and %d", thisThreadNo, stIndex, pivotIndex);
  //          if (len <= 0) return;                   // Length 0 and 1 arrays don't need sorting

            do {                                        // Main sorting loop

                if (len == 2) {
                    if (cmp.compare(a[pivotIndex], a[stIndex]) < 0) {
                        side = a[pivotIndex];
                        a[pivotIndex] = a[stIndex];
                        a[stIndex] = side;
                        return;
                    }
                }

                for (int i = pivotIndex - 1; i >= stIndex; i--) {
                    if (cmp.compare(a[pivotIndex], a[i]) < 0) {                // Increase right partition, move element there
                        side = a[pivotIndex];                                       // Pivot element
                        a[pivotIndex--] = a[i];
                        a[i] = a[pivotIndex];
                        a[pivotIndex] = side;                                       // Everything > pivot is located from [pivotIndex+1] to [endIndexP1-1]
                    }                                                               // Everything in [stIndex - pivotIndex] is <= pivot
                }
                int lenRight = endIndexP1 - (pivotIndex + 1);
                int lenLeft = pivotIndex - stIndex + 1;
                if (lenRight < lenLeft) {
                    if (lenRight > 1) new SortingThread(pivotIndex + 1, endIndexP1).run();
                    endIndexP1 = pivotIndex--;                          // Re-sort bigger part of the array in the current loop
                    len = lenLeft;                                      // Continue sorting left partition
                } else {
                    if (lenLeft > 1) new SortingThread(stIndex, pivotIndex).run();
                    stIndex = pivotIndex + 1;
                    pivotIndex = endIndexP1 - 1;
                    len = lenRight;                                             // lenLeft contains length of longest of two subarrays
                }
            } while (len > 1);
        }
    }
}