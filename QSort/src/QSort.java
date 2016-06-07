import java.util.Comparator;

/**
 * I'm trying to write QSort implementation, too!
 */
public class QSort<T> {

    public QSort(Comparator<T> sortRule) {
        cmp = sortRule;
    }

    private final Comparator<T> cmp;

    /**
     * Sort array inArray starting at [stIndex] and ending at [endIndex-1]
     */
    public void sortArray(final T[] a, int stIndex, int endIndex) {
        T side;
        int len;
        int pivotIndex = endIndex - 1;
        len = endIndex - stIndex;

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
                if (cmp.compare(a[pivotIndex], a[i]) < 0) {                // Increase right partition, move element there
                    side = a[pivotIndex];                                       // Pivot element
                    a[pivotIndex--] = a[i];
                    a[i] = a[pivotIndex];
                    a[pivotIndex] = side;                                       // Everything > pivot is located from [pivotIndex+1] to [endIndex-1]
                }                                                               // Everything in [stIndex - pivotIndex] is <= pivot
            }
            int lenRight = endIndex - (pivotIndex + 1);
            int lenLeft = pivotIndex - stIndex + 1;
            if (lenRight < lenLeft) {
                sortArray(a, pivotIndex + 1, endIndex);           // Can be launched in another thread
                endIndex = pivotIndex--;                          // Re-sort bigger part of the array in the current loop
                len = lenLeft;                                    // Continue sorting left partition
            } else {
                sortArray(a, stIndex, pivotIndex);                // Sort right partition in the main loop
                stIndex = pivotIndex + 1;
                pivotIndex = endIndex - 1;
                len = lenRight;                                             // lenLeft contains length of longest of two subarrays
            }
        } while (len > 1);
    }
}
