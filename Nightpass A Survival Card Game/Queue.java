class NodeQ {
	Card card;
	NodeQ next;

	NodeQ(Card card) {
		this.card = card;
		next = null;
	}

}

//FIFO principle suitable for the project's last priority
public class Queue {
	private NodeQ front, rear;

	public Queue() {
		front = rear = null;
	}

	public void enqueue(Card item) {

		item.updateInsertionOrder();
		NodeQ NodeQ = new NodeQ(item);

		if (rear == null) {
			front = rear = NodeQ;
			return;
		}

		rear.next = NodeQ;
		rear = NodeQ;
	}

	public Card dequeue() {

		if (front == null) {

			return null;
		}

		Card item = front.card;
		front = front.next;
		if (front == null)
			rear = null;
		return item;

	}

	public boolean isEmpty() {
		return front == null;
	}
}
