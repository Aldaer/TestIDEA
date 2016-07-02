import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;

/**
 * I'm trying to write QSort implementation, too!
 */
@SuppressWarnings("WeakerAccess")
public class QSort<T> {

    public QSort(Comparator<T> sortRule) {
        cmp = sortRule;
    }

    private final static Logger LOG = LogManager.getLogger();

    /**
     * Sort array inArray starting at [stIndex] and ending at [endIndex-1]
     */
    public void sort(final T[] arrayToSort) {
        if (arrayToSort == null || arrayToSort.length <= 1) return;
        a = arrayToSort;

        if (LOG.isDebugEnabled()) LOG.debug("Now starting to sort array of " + arrayToSort.length + " elements");
        callNo = 0;
        sortArray(0, a.length);
    }

    private T[] a;
    private final Comparator<T> cmp;
    private int callNo;

    private void sortArray(int stIndex, int endIndex) {  // endIndex points TO THE RIGHT of the last array element
        T pivot;
        int len;
        int pivotIndex = endIndex - 1;                 // Last element of the range will be the pivot
        len = endIndex - stIndex;
        int thisCallNo = ++callNo;

        if (LOG.isTraceEnabled()) LOG.trace("Launching sort routine #" + thisCallNo + " to sort elements " + stIndex + " to " + (endIndex - 1));

        do {                                          // Main sorting loop

            pivot = a[pivotIndex];
            if (len == 2) {
                if (cmp.compare(pivot, a[stIndex]) < 0) {
                    a[pivotIndex] = a[stIndex];
                    a[stIndex] = pivot;
                }
                break;
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
                if (lenRight > 1) sortArray(pivotIndex + 1, endIndex);          // Can be launched in another thread
                endIndex = pivotIndex--;                          // Re-sort bigger part of the array in the current loop
                // New pivot is the element immediately left of the old pivot
                len = lenLeft;                                    // Continue sorting left partition
            } else {
                if (lenLeft > 1) sortArray(stIndex, pivotIndex);  // Sort right partition in the main loop
                stIndex = pivotIndex + 1;
                pivotIndex = endIndex - 1;
                len = lenRight;                                   // lenRight contains length of longest of two subarrays
            }
        } while (len > 1);

        if (LOG.isTraceEnabled()) LOG.trace("Call " + thisCallNo + " returned");
    }
}
