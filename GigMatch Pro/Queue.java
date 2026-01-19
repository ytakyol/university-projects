

public class Queue<T> {
	
	private static class NodeQ<T> {
        T item;
        NodeQ<T> next;

        NodeQ(T item) {
            this.item = item;
            this.next = null;
        }
    }
	
	private NodeQ<T> front, rear;

	public Queue() {
		front = rear = null;
	}

	public void enqueue(T item) {

		NodeQ<T> newNode = new NodeQ<>(item);

		if (rear == null) {
			front = rear = newNode;
			return;
		}

		rear.next = newNode;
		rear = newNode;
	}

	public T dequeue() {

		if (front == null) {
			return null;
		}

		T item = front.item;
		front = front.next;
		if (front == null)
			rear = null;
		return item;

	}

	public boolean isEmpty() {
		return front == null;
	}
	
	public T peek() {
	    return (front == null) ? null : front.item;
	}

}

