import java.util.ArrayList;

public class HashMap<K, V> {

    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] table;
    private int size;
    private int threshold;

    @SuppressWarnings("unchecked")
    public HashMap() {
        table = new Entry[DEFAULT_CAPACITY];
        threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
        size = 0;
    }

    // Optimized index calculation using bitmasking
    private int getIndex(K key) {
        return (key.hashCode() & 0x7FFFFFFF) & (table.length - 1);
    }

    public void put(K key, V value) {
        int index = getIndex(key);
        Entry<K, V> head = table[index];

        // 1. Scan chain for updates
        Entry<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value; // Update existing
                return;
            }
            current = current.next;
        }

        // 2. Insert at head (O(1)) - Faster than adding to end
        table[index] = new Entry<>(key, value, head);
        size++;

        if (size >= threshold) {
            resize();
        }
    }

    public V get(K key) {
        int index = getIndex(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            // Use .equals() for generic keys
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public boolean remove(K key) {
        int index = getIndex(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    // Removing head
                    table[index] = current.next;
                } else {
                    // Bypass the current node
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = table.length * 2;
        Entry<K, V>[] newTable = new Entry[newCapacity];
        
        // Fast Re-hashing
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> e = table[i];
            while (e != null) {
                Entry<K, V> next = e.next;
                
                // Recalculate index for new table
                int newIndex = (e.key.hashCode() & 0x7FFFFFFF) & (newCapacity - 1);
                
                // Insert at head of new bucket
                e.next = newTable[newIndex];
                newTable[newIndex] = e;
                
                e = next;
            }
        }
        table = newTable;
        threshold = (int) (newCapacity * LOAD_FACTOR);
    }

    public int size() {
        return size;
    }
    
    // Optimized values() that iterates the array directly
    public ArrayList<V> values() {
        ArrayList<V> list = new ArrayList<>(size);
        for (int i = 0; i < table.length; i++) {
             Entry<K, V> e = table[i];
             while (e != null) {
                 list.add(e.value);
                 e = e.next;
             }
        }
        return list;
    }
}