import java.util.Comparator;

/**
 * I'm trying to write QSort implementation, too!
 */
public class QSort<T> {

    public QSort(Comparator<T> sortRule) {
        cmp = sortRule;
    }

    /**
     * Sort array inArray starting at [stIndex] and ending at [endIndex-1]
     */
    public void sort(final T[] arrayToSort) {
        if (arrayToSort == null || arrayToSort.length <= 1) return;
        a = arrayToSort;
        sortArray(0, a.length);
    }

    private T[] a;
    private final Comparator<T> cmp;

    private void sortArray(int stIndex, int endIndex) {  // endIndex points TO THE RIGHT of the last array element
        T pivot;
        int len;
        int pivotIndex = endIndex - 1;
        len = endIndex - stIndex;

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
    }
}
