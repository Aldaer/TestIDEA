import java.util.Comparator;
import java.util.concurrent.*;
import java.util.concurrent.Future;

/**
 * 2nd version, with multithreading
 */
public class QSort2<T> {

    /**
     * Creates a sorter and sets the comparator to use on array elements
     *
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
        if (arrayToSort == null || arrayToSort.length <= 1) return CompletableFuture.completedFuture(arrayToSort);
        executorService = Executors.newCachedThreadPool();
        return executorService.submit(new SortingThread(arrayToSort, 0, arrayToSort.length), arrayToSort);
/*        new SortingThread(0, a.length).run();
        return CompletableFuture.completedFuture(a);*/
    }

    private final Comparator<T> cmp;
    private ExecutorService executorService;


    private int threadNo = 0;

    private class SortingThread implements Runnable {
        private T[] a;                                          // Array to sort

        private int stIndex;
        private int endIndex;

        private int thisThreadNo;

        private SortingThread(T[] arrayToSort, int stIndex, int endIndex) {
            a = arrayToSort;
            thisThreadNo = ++threadNo;
            this.stIndex = stIndex;
            this.endIndex = endIndex;
        }

        @Override
        public void run() {
            T pivot;
            int len;
            int pivotIndex = endIndex - 1;
            len = endIndex - stIndex;
            System.out.printf("\nThread %d sorts elements in the range of [%d-%d]", thisThreadNo, stIndex, pivotIndex);

            do {                                          // Main sorting loop

                pivot = a[pivotIndex];
                if (len == 2) {
                    if (cmp.compare(pivot, a[stIndex]) < 0) {
                        a[pivotIndex] = a[stIndex];
                        a[stIndex] = pivot;
                        return;
                    }
                }

                for (int i = pivotIndex - 1; i >= stIndex; i--) {
                    if (cmp.compare(pivot, a[i]) < 0) {                // Increase right partition, move element there
                        a[pivotIndex--] = a[i];
                        a[i] = a[pivotIndex];
                        a[pivotIndex] = pivot;                                      // Everything > pivot is located from [pivotIndex+1] to [endIndex-1]
                    }                                                               // Everything in [stIndex - pivotIndex] is <= pivot
                }
                int lenRight = endIndex - (pivotIndex + 1);                         // Subarray of big elements, length >=0
                int lenLeft = pivotIndex - stIndex;                                 // Subarray of small elements, length >=0
                if (lenRight < lenLeft) {
                    if (lenRight > 1) executorService.submit(new SortingThread(a, pivotIndex + 1, endIndex));
                    endIndex = pivotIndex--;                                         // Re-sort bigger part of the array in the current loo
                                                                                     // New pivot is the element immediately left of the old pivot
                    len = lenLeft;                                                   // Continue sorting left partition
                } else {
                    if (lenLeft > 1) executorService.submit(new SortingThread(a, stIndex, pivotIndex));
                    stIndex = pivotIndex + 1;
                    pivotIndex = endIndex - 1;
                    len = lenRight;                                   // lenRight contains length of longest of two subarrays
                }
            } while (len > 1);
        }
    }
}
