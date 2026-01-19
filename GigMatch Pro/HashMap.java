import java.util.ArrayList;
import java.util.LinkedList;

public class HashMap<K, V> {
	
	private static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
	
    static final double MAXIMUM_LOAD_FACTOR = 0.75;
    static final int DEFAULT_CAPACITY = 16;
	
	int capacity;
	int size;
	double loadFactor;
	LinkedList<Entry<K, V>>[] table;
	
	@SuppressWarnings("unchecked")
	HashMap(){
		this.capacity = DEFAULT_CAPACITY;
        table = new LinkedList[this.capacity];
        size = 0;
        loadFactor = 0;
	}
	
	
	private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) & (capacity - 1);
    }
	
	public  double updateSize(int added) {
		size = size + added;
		loadFactor = ((double)size)/capacity;
		return loadFactor;
	}
	
	private void rehash() {
	    int oldCapacity = capacity;
	    capacity = capacity * 2;  // or nextPrime(capacity * 2)

	    LinkedList<Entry<K, V>>[] oldTable = table;

	    // create new table
	    @SuppressWarnings("unchecked")
	    LinkedList<Entry<K, V>>[] newTable =
	            (LinkedList<Entry<K, V>>[]) new LinkedList[capacity];

	    table = newTable;
	    size = 0;  // will be recalculated (because put() increments size)

	    // reinsert all entries
	    for (int i = 0; i < oldCapacity; i++) {
	        if (oldTable[i] != null) {
	            for (Entry<K, V> e : oldTable[i]) {
	                put(e.key, e.value);   // rehash using new capacity
	            }
	        }
	    }
	}
	
	public void put(K key, V value) {
        int index = hash(key);

        if (table[index] == null)
            table[index] = new LinkedList<>();

        for (Entry<K, V> e : table[index]) {
            if (e.key.equals(key)) {
                e.value = value; // replace if key exists
                return;
            }
        }

        table[index].addFirst(new Entry<>(key, value));
        if (updateSize(1) > MAXIMUM_LOAD_FACTOR) {
        	rehash();
        }
    }
	
	public V get(K key) {
        int index = hash(key);

        // If the bucket doesn't exist, the key can't be in the map
        if (table[index] == null) {
            return null;
        }

        // Search the linked list (bucket) for the key
        for (Entry<K, V> e : table[index]) {
            if (e.key.equals(key)) {
                return e.value; // Key found
            }
        }

        // Key not found in the list
        return null;
    }
	
	public boolean remove(K key) {
        int index = hash(key);

        if (table[index] == null) {
            return false;
        }

        // Use an explicit iterator to safely remove elements
        java.util.Iterator<Entry<K, V>> iterator = table[index].iterator();
        while (iterator.hasNext()) {
            Entry<K, V> e = iterator.next();
            if (e.key.equals(key)) {
                iterator.remove(); // This is the safe way to remove
                updateSize(-1);
                return true;
            }
        }

        // Key was not found
        return false;
    }
	
	public ArrayList<V> values() {
        // Initialize the list with the map's current size for best performance.
        ArrayList<V> allValues = new ArrayList<>(this.size);

        // Iterate over the entire table array.
        for (int i = 0; i < this.capacity; i++) {
            
            // Check if the bucket at this index is not empty.
            if (table[i] != null) {
                
                // Iterate over every Entry in the bucket's LinkedList.
                for (Entry<K, V> entry : table[i]) {
                    allValues.add(entry.value);
                }
            }
        }
        
        return allValues;
    }

    public int size() {
        return size;
    }
}
