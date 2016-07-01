import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 2nd version, with multithreading
 */
public class QSort2<T> {

    private final Comparator<T> cmp;
    private ForkJoinPool pool;
    private int threadNo;

    final static Logger LOG = LogManager.getLogger();

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
        pool = new ForkJoinPool(20); //Executors.newSingleThreadExecutor(); // newFixedThreadPool(12); // newSingleThreadExecutor();

        LOG.debug("Now starting to sort array of " + arrayToSort.length + " elements");
        threadNo = 0;
        return pool.submit(new SortingThread(arrayToSort, 0, arrayToSort.length));
    }

    private class SortingThread extends RecursiveTask<T[]> {
        private T[] a;                                          // Array to sort

        private int stIndex;
        private int endIndex;

        private int thisThreadNo;

        private SortingThread(T[] arrayToSort, int stIndex, int endIndex) {
            a = arrayToSort;
            thisThreadNo = ++threadNo;
            this.stIndex = stIndex;
            this.endIndex = endIndex;
            LOG.debug("Creating thread #" + thisThreadNo + " to sort elements " + stIndex + " to " + (endIndex - 1));
        }

        @Override
        protected T[] compute() {
            T pivot;
            int len;
            int pivotIndex = endIndex - 1;
            len = endIndex - stIndex;

            List<ForkJoinTask<?>> childTasks = new LinkedList<>();

            MainLoop:
            do {                                          // Main sorting loop

                pivot = a[pivotIndex];
                if (len == 2) {
                    if (cmp.compare(pivot, a[stIndex]) < 0) {
                        a[pivotIndex] = a[stIndex];
                        a[stIndex] = pivot;
                    }
                    break MainLoop;
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
                    if (lenRight > 1) childTasks.add(new SortingThread(a, pivotIndex + 1, endIndex).fork());
                    endIndex = pivotIndex--;                                         // Re-sort bigger part of the array in the current loo
                    // New pivot is the element immediately left of the old pivot
                    len = lenLeft;                                                   // Continue sorting left partition
                } else {
                    if (lenLeft > 1) childTasks.add(new SortingThread(a, stIndex, pivotIndex).fork());
                    stIndex = pivotIndex + 1;
                    pivotIndex = endIndex - 1;
                    len = lenRight;                                   // lenRight contains length of longest of two subarrays
                }
            } while (len > 1);
            LOG.debug("Thread " + thisThreadNo + " is waiting for child threads");
            childTasks.parallelStream().forEach(ForkJoinTask::join);
            LOG.debug("Thread " + thisThreadNo + " completes");
            return a;
        }
    }
}
