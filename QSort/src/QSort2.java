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
        if (a == null || a.length == 1) return CompletableFuture.completedFuture(a);
        int maxThreads = (int) (Math.log(a.length) / Math.log(2));
        executorService = Executors.newFixedThreadPool(maxThreads);
        return executorService.submit(new SortingThread(0, a.length - 1), a);
    }

    private T[] a;                                          // Array to sort
    private final Comparator<T> cmp;
    private ExecutorService executorService;


    private int threadNo = 0;

    private class SortingThread implements Runnable {

        private int stIndex;
        private int endIndex;

        private SortingThread(int stIndex, int endIndex) {
            threadNo++;
            this.stIndex = stIndex;
            this.endIndex = endIndex;
        }

        @Override
        public void run() {
            T side;
            int len;
            int pivotIndex = endIndex - 1;
            len = endIndex - stIndex;                   // 1 less than actual length of the array
            System.out.printf("Thread %d is sorting elements %d to %d\n", threadNo, stIndex, endIndex);

            do {                                        // Main sorting loop

                if (len <= 0) return;                   // Length 0 and 1 arrays don't need sorting
                if (len == 1) {
                    if (cmp.compare(a[pivotIndex], a[stIndex]) < 0) {
                        side = a[pivotIndex];
                        a[pivotIndex] = a[stIndex];
                        a[stIndex] = side;
                        return;
                    }
                }

                for (int i = pivotIndex - 1; i >= stIndex; i--) {
                    if (cmp.compare(a[pivotIndex], a[i]) < 0) {                     // Increase right partition, move element there
                        side = a[pivotIndex];                                       // Pivot element
                        a[pivotIndex--] = a[i];
                        a[i] = a[pivotIndex];
                        a[pivotIndex] = side;                                       // Everything > pivot is located from [pivotIndex+1] to [endIndex-1]
                    }                                                               // Everything in [stIndex - pivotIndex] is <= pivot
                }
                int lenRight = endIndex - (pivotIndex + 1);
                int lenLeft = pivotIndex - stIndex + 1;
                if (lenRight < lenLeft) {
                    executorService.submit(new SortingThread(pivotIndex + 1, endIndex));
                    endIndex = pivotIndex--;                        // Re-sort bigger part of the array in the current loop
                    len = lenLeft;                                  // Continue sorting left partition
                } else {
                    executorService.submit(new SortingThread(stIndex, pivotIndex));
                    stIndex = pivotIndex + 1;
                    pivotIndex = endIndex - 1;                      // Sort right partition in the main loop
                    len = lenRight;                                 // lenLeft contains length of longest of two subarrays
                }
            }

            while (len > 1);
        }
    }

}