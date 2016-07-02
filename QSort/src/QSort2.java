import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 2nd version, with multithreading
 */
@SuppressWarnings("WeakerAccess")
public class QSort2<T> {
    private final static int MIN_ARRAY_TO_CREATE_THREAD = 2_000;    // Determined experimentally

    private final Comparator<T> cmp;
    private ForkJoinPool pool;
    private int threadNo;

    private final static Logger LOG = LogManager.getLogger();

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
        pool = new ForkJoinPool();

        if (LOG.isDebugEnabled()) LOG.debug("Now starting to sort array of " + arrayToSort.length + " elements");
        threadNo = 0;
        return pool.submit(new SortingTask(arrayToSort, 0, arrayToSort.length));
    }

    @SuppressWarnings("UnnecessaryLabelOnBreakStatement")
    private class SortingTask extends RecursiveTask<T[]> {
        private T[] a;                                          // Array to sort

        private int stIndex;
        private int endIndex;

        private int thisTaskNo;

        private SortingTask(T[] arrayToSort, int stIndex, int endIndex) {
            a = arrayToSort;
            thisTaskNo = ++threadNo;
            this.stIndex = stIndex;
            this.endIndex = endIndex;
            if (LOG.isDebugEnabled()) LOG.debug("Creating task #" + thisTaskNo + " to sort elements " + stIndex + " to " + (endIndex - 1));
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
                    if (lenRight > 1) {
                        if (lenRight > MIN_ARRAY_TO_CREATE_THREAD) childTasks.add(new SortingTask(a, pivotIndex + 1, endIndex).fork());
                        else new SortingTask(a, pivotIndex + 1, endIndex).compute();          // Do not create threads for short subarrays
                    }
                    endIndex = pivotIndex--;                                         // Re-sort bigger part of the array in the current loo
                                                                                     // New pivot is the element immediately left of the old pivot
                    len = lenLeft;                                                   // Continue sorting left partition
                } else {
                    if (lenLeft > 1) {
                        if (lenLeft > MIN_ARRAY_TO_CREATE_THREAD) childTasks.add(new SortingTask(a, stIndex, pivotIndex).fork());
                        else new SortingTask(a, stIndex, pivotIndex).compute();
                    }
                    stIndex = pivotIndex + 1;
                    pivotIndex = endIndex - 1;
                    len = lenRight;                                   // lenRight contains length of longest of two subarrays
                }
            } while (len > 1);
            if (LOG.isDebugEnabled()) LOG.debug("Task " + thisTaskNo + " is waiting for subtasks to complete");
            childTasks.parallelStream().forEach(ForkJoinTask::join);
            if (LOG.isDebugEnabled()) LOG.debug("Task " + thisTaskNo + " completes");
            return a;
        }
    }
}
