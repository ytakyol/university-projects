import java.util.ArrayList;


public class OptimizedPriorityQueue<T extends Comparable<T>> {

    private final ArrayList<T> heap;
    private final HashMap<T, Integer> indexMap;

    public OptimizedPriorityQueue() {
        this.heap = new ArrayList<>(16);
        // Initialize map with adequate capacity to prevent immediate rehashing
        this.indexMap = new HashMap<>();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    public T peek() {
        if (heap.isEmpty()) return null;
        return heap.get(0);
    }

    public void add(T element) {
        if (element == null) throw new IllegalArgumentException("Null elements not allowed");
        heap.add(element);
        siftUp(heap.size() - 1, element);
    }

    public T poll() {
        if (heap.isEmpty()) return null;

        T result = heap.get(0);
        int lastIndex = heap.size() - 1;
        T last = heap.remove(lastIndex);
        indexMap.remove(result);

        if (lastIndex > 0) {
            siftDown(0, last);
        }

        return result;
    }

    public void remove(T element) {
        Integer index = indexMap.get(element);
        if (index == null) return;

        int i = index;
        int lastIndex = heap.size() - 1;
        
        // If removing the last element, just remove and exit
        if (i == lastIndex) {
            heap.remove(lastIndex);
            indexMap.remove(element);
            return;
        }

        // Swap with the last element
        T last = heap.remove(lastIndex);
        indexMap.remove(element);
        
        // We now have the 'last' element sitting at index 'i'.
        // It might need to go up or down to restore invariant.
        siftUpOrDown(i, last);
    }

    public void update(T element) {
        Integer index = indexMap.get(element);
        if (index != null) {
            // Since we don't know if the priority increased or decreased,
            // we try to move it; the logic handles checking parents/children.
            siftUpOrDown(index, element);
        }
    }

    // --- Helpers ---

    /**
     * Moves the element up to its correct position.
     * Optimization: Uses assignment (shifting) instead of full swaps.
     */
    private void siftUp(int k, T node) {
        while (k > 0) {
            int parentIdx = (k - 1) >>> 1;
            T parent = heap.get(parentIdx);

            // If node >= parent, heap property is satisfied (Min-Heap behavior? 
            // Original code used compareTo < 0 for higher priority, implying Min-Heap logic)
            if (node.compareTo(parent) >= 0) {
                break;
            }

            // Move parent down
            heap.set(k, parent);
            indexMap.put(parent, k);
            k = parentIdx;
        }
        heap.set(k, node);
        indexMap.put(node, k);
    }

    /**
     * Moves the element down to its correct position.
     * Optimization: Iterative approach + assignment (shifting).
     */
    private void siftDown(int k, T node) {
        int half = heap.size() >>> 1; // loop while a non-leaf
        while (k < half) {
            int childIdx = (k << 1) + 1; // assume left child is smaller
            T child = heap.get(childIdx);
            int rightIdx = childIdx + 1;

            // Check if right child exists and is "smaller" (higher priority) than left
            if (rightIdx < heap.size()) {
                T rightChild = heap.get(rightIdx);
                if (rightChild.compareTo(child) < 0) {
                    childIdx = rightIdx;
                    child = rightChild;
                }
            }

            // If node is smaller/equal to the smallest child, we are done
            if (node.compareTo(child) <= 0) {
                break;
            }

            // Move child up
            heap.set(k, child);
            indexMap.put(child, k);
            k = childIdx;
        }
        heap.set(k, node);
        indexMap.put(node, k);
    }

    /**
     * Decides whether to sift up or down based on neighbors.
     */
    private void siftUpOrDown(int i, T node) {
        // Check if we need to go up (compare with parent)
        if (i > 0) {
            int parentIdx = (i - 1) >>> 1;
            T parent = heap.get(parentIdx);
            if (node.compareTo(parent) < 0) {
                siftUp(i, node);
                return;
            }
        }
        // Otherwise, try to go down
        siftDown(i, node);
    }
}