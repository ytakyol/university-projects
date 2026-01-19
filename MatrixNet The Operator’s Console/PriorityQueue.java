import java.util.ArrayList;

public class PriorityQueue<T extends Comparable<T>> {

    
    private ArrayList<T> heap;

    public PriorityQueue() {
        heap = new ArrayList<>();
        heap.add(null); 
    }

    // Standard Min-Heap "bubble up"
    private void bubbleUp(int i) {
        while (i > 1) {
            int parentIdx = i / 2;
            if (heap.get(i).compareTo(heap.get(parentIdx)) < 0) {
                swap(i, parentIdx);
                i = parentIdx;
            } else {
                break;
            }
        }
    }

    // Standard Min-Heap "bubble down"
    private void bubbleDown(int i) {
        int n = heap.size() - 1;
        while (2 * i <= n) {
            int left = 2 * i;
            int right = 2 * i + 1;
            int smallest = i;

            if (left <= n && heap.get(left).compareTo(heap.get(smallest)) < 0) {
                smallest = left;
            }
            
            if (right <= n && heap.get(right).compareTo(heap.get(smallest)) < 0) {
                smallest = right;
            }

            if (smallest != i) {
                swap(i, smallest);
                i = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public void add(T element) {
        heap.add(element);
        bubbleUp(heap.size() - 1);
    }

    public T poll() {
        if (isEmpty()) return null;

        T root = heap.get(1);
        T last = heap.get(heap.size() - 1);
        
        // Move last element to root and reduce size
        heap.remove(heap.size() - 1);
        
        if (!isEmpty()) {
            heap.set(1, last);
            bubbleDown(1);
        }

        return root;
    }

    public T peek() {
        if (isEmpty()) return null;
        return heap.get(1);
    }

    public boolean isEmpty() {
        return heap.size() <= 1;
    }
    
    // Size helper if needed
    public int size() {
        return heap.size() - 1;
    }
}