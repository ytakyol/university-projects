import java.util.ArrayList;

public class PriorityQueue {

	private ArrayList<Freelancer> heap;
	private HashMap<Freelancer, Integer> indexMap;

	public PriorityQueue() {
		heap = new ArrayList<>();
		heap.add(null);
		indexMap = new HashMap<>();
	}

	private int parent(int i) {
		return i/2;
	}

	private int left(int i) {
		return 2 * i;
	}

	private int right(int i) {
		return 2 * i + 1;
	}
	
	private void swap(int i, int j) {
		Freelancer temp = heap.get(i);
		heap.set(i, heap.get(j));
		heap.set(j, temp);
		
		indexMap.put(heap.get(i), i);
	    indexMap.put(heap.get(j), j);
	}
	
	private void bubbleUp(int i) {
	    while (i > 1 && heap.get(i).isPriorTo(heap.get(parent(i)))) {
	        swap(i, parent(i));
	        i = parent(i);
	    }
	}
	
	private void bubbleDown(int i) {
	    int largest = i;
	    int l = left(i);
	    int r = right(i);

	    if (l < heap.size() && heap.get(l).isPriorTo(heap.get(largest))) largest = l;
	    if (r < heap.size() && heap.get(r).isPriorTo(heap.get(largest))) largest = r;

	    if (largest != i) {
	        swap(i, largest);
	        bubbleDown(largest);
	    }
	}

	private void bubbleUpDown(int i) {
	    if (i > 1 && heap.get(i).isPriorTo(heap.get(i / 2))) {
	        bubbleUp(i);
	    } else {
	        bubbleDown(i);
	    }
	}

	public void add(Freelancer f) {
        heap.add(f);
        int i = heap.size() - 1;
        indexMap.put(f, i);
        bubbleUp(i);
    }
	
	public boolean isEmpty() {
        return heap.size() == 1; // because index 0 is unused
    }
	
	public Freelancer peek() {
		if (isEmpty()) return null;
        return heap.get(1);
    }
	
	public Freelancer poll() {
        if (isEmpty()) return null;

        Freelancer root = heap.get(1);
        Freelancer last = heap.remove(heap.size() - 1);

        if (!isEmpty()) {
            heap.set(1, last);
            indexMap.put(last, 1);
            bubbleDown(1);
        }
        
        indexMap.remove(root);
        return root;
    }
	
	public void updateFreelancer(Freelancer f) {
	    Integer i = indexMap.get(f);
	    if (i == null) return; // not in heap

	    bubbleUpDown(i);
	}
	
	public void remove(Freelancer f) {
	    Integer i = indexMap.get(f);
	    if (i == null) return;

	    swap(i,heap.size()-1);
	    heap.remove(heap.size()-1);
	    indexMap.remove(f);
	    
	    if(i<heap.size()) {
	    	bubbleUpDown(i);
	    }
	    	
	}
	
	public boolean inQueue(Freelancer f) {
		if(indexMap.get(f) == null)
			return false;
		return true;
	}
}
