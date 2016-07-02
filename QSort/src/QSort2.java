import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 3rd version sorter class, with multithreading and simultaneous asynchronous sorting of several arrays
 */
@SuppressWarnings("WeakerAccess")
public class QSort2<T> {
    private final static int MIN_ARRAY_TO_FORK = 2_000;    // Determined experimentally for max performance with single-array sorting

    private final Comparator<T> cmp;
    private final ForkJoinPool pool;                                // Single common pool for all sorting tasks performed with this sorter instance

    private final static Logger LOG = LogManager.getLogger();

    /**
     * Creates a sorter and sets the comparator to use on array elements
     *
     * @param sortRule Indicates how array elements should be compared
     */
    public QSort2(Comparator<T> sortRule) {
        cmp = sortRule;
        pool = new ForkJoinPool();
    }

    /**
     * Performs sorting of the array {@code a}. Sorting is asynchronous and in-place, so do not modify the array while the result isn't complete.
     *
     * @param arrayToSort Array to sort
     * @return Future containing sorted array
     */
    public Future<T[]> sort(final T[] arrayToSort) {
        return sort(arrayToSort, "");
    }

    public Future<T[]> sort(final T[] arrayToSort, String tag) {
        if (arrayToSort == null || arrayToSort.length <= 1) return CompletableFuture.completedFuture(arrayToSort);

        if (LOG.isDebugEnabled()) LOG.debug("Now starting to sort array " + (tag == null || tag.equals("")? "" : "[" + tag + "] ") + "of " + arrayToSort.length + " elements");

        return new SortingEnvironment(arrayToSort, tag).invoke();
    }


    /**
     * Class to hold all info pertaining to a single sorting operation
     */
    private class SortingEnvironment {
        private final T[] a;
        private AtomicInteger activeTaskNo;
        private AtomicInteger taskNo;
        private CompletableFuture<T[]> sortedArray;

        private String tag = "";         // To mark simultaneously sorted arrays for debugging purposes

        private SortingEnvironment(T[] arrayToSort) {
            this.a = arrayToSort;
            taskNo = new AtomicInteger(0);
            activeTaskNo = new AtomicInteger(0);
        }

        private SortingEnvironment(T[] arrayToSort, String tag) {
            this(arrayToSort);
            if (tag != null) this.tag = tag;
        }

        /**
         * Launches anynchronous sorting and returns a future to the expected result
         * @return Future sorting result
         */
        public Future<T[]> invoke() {
            sortedArray = new CompletableFuture<>();
            pool.execute(new SortingTask(0, a.length));
            return sortedArray;
        }

        @SuppressWarnings("UnnecessaryLabelOnBreakStatement")
        private class SortingTask extends RecursiveAction {
            private int stIndex;
            private int endIndex;
            private final int thisTaskNo;

            private SortingTask(int stIndex, int endIndex) {
                thisTaskNo = taskNo.incrementAndGet();
                activeTaskNo.incrementAndGet();
                this.stIndex = stIndex;
                this.endIndex = endIndex;
                if (LOG.isTraceEnabled())
                    LOG.trace("Creating task " + (tag.equals("")? "" : tag + "-") + thisTaskNo + " to sort elements " + stIndex + " to " + (endIndex - 1));
            }

            /**
             * Main sorting method. Do NOT call with 0- or 1-item-long arrays: this condition isn't checked.
             * Last subtask to finish completes the sortedArray future and sets the result
             */
            @Override
            protected void compute() {
                T pivot;
                int len = endIndex - stIndex;
                int pivotIndex = endIndex - 1;

                MainSortingLoop:
                do {

                    pivot = a[pivotIndex];
                    if (len == 2) {
                        if (cmp.compare(pivot, a[stIndex]) < 0) {
                            a[pivotIndex] = a[stIndex];
                            a[stIndex] = pivot;
                        }
                        break MainSortingLoop;
                    }

                    for (int i = pivotIndex - 1; i >= stIndex; i--) {
                        if (cmp.compare(pivot, a[i]) < 0) {                // Increase right partition, move element there
                            a[pivotIndex--] = a[i];
                            a[i] = a[pivotIndex];
                            a[pivotIndex] = pivot;                                       // Everything > pivot is located from [pivotIndex+1] to [endIndex-1]
                        }                                                                // Everything in [stIndex - pivotIndex] is <= pivot
                    }
                    int lenRight = endIndex - (pivotIndex + 1);                          // Subarray of big elements, length >=0
                    int lenLeft = pivotIndex - stIndex;                                  // Subarray of small elements, length >=0
                    if (lenRight < lenLeft) {
                        if (lenRight > 1) {                                              // Does the partition need sorting?
                            if (lenRight >= MIN_ARRAY_TO_FORK)
                                new SortingTask(pivotIndex + 1, endIndex).fork();        // Create subtasks for LONG subarrays
                            else
                                new SortingTask(pivotIndex + 1, endIndex).compute();     // Do not create subtasks for SHORT subarrays
                        }
                        endIndex = pivotIndex--;                                         // Re-sort bigger part of the array in the current loop
                                                                                         // New pivot will be the element immediately left of the old pivot
                        len = lenLeft;                                                   // Continue sorting left partition
                    } else {
                        if (lenLeft > 1) {
                            if (lenLeft >= MIN_ARRAY_TO_FORK)
                                new SortingTask(stIndex, pivotIndex).fork();
                            else new SortingTask(stIndex, pivotIndex).compute();
                        }
                        stIndex = pivotIndex + 1;
                        pivotIndex = endIndex - 1;
                        len = lenRight;                                                  // Continue sorting right partition
                    }
                } while (len > 1);

                int remaining = activeTaskNo.decrementAndGet();
                if (LOG.isTraceEnabled()) LOG.trace("Task " + (tag.equals("")? "" : tag + "-") + thisTaskNo + " completes, " + remaining + " remains");
                if (remaining == 0) {                                             // Last (sub)task to complete finalizes the result
                    if (LOG.isTraceEnabled()) LOG.trace("Completing the future...");
                    sortedArray.complete(a);
                    if (LOG.isDebugEnabled()) LOG.debug("Calculation " + (tag.equals("")? "" : "[" + tag + "] ") + "complete");
                }
            } // compute()
        } // SortingTask
    } // SortingEnvironment
}
