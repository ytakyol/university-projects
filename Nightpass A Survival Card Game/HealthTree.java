class NodeHT {

	int key; // Health Point
	Queue cardQueue;
	int height;

	int minHealth, maxHealth;

	NodeHT left, right;

	NodeHT(int key) {
		this.key = key;
		this.height = 1;
		cardQueue = new Queue();

		left = null;
		right = null;
	}

}

//Will Store Health Nodes which will store Queue of cards
public class HealthTree {

	NodeHT root;
	int cardCount;

	HealthTree() {
		root = null;
		cardCount = 0;
	}

	public int getCardCount() {
		return cardCount;
	}

	int height(NodeHT NodeHT) {
		if (NodeHT == null)
			return 0;
		return NodeHT.height;
	}

	NodeHT rightRotate(NodeHT NodeHT) {
		NodeHT leftChild = NodeHT.left;
		NodeHT temp = leftChild.right;

		leftChild.right = NodeHT;
		NodeHT.left = temp;

		NodeHT.height = Math.max(height(NodeHT.left), height(NodeHT.right)) + 1;
		leftChild.height = Math.max(height(leftChild.left), height(leftChild.right)) + 1;

		return leftChild;
	}

	NodeHT leftRotate(NodeHT NodeHT) {
		NodeHT rightChild = NodeHT.right;
		NodeHT temp = rightChild.left;

		rightChild.left = NodeHT;
		NodeHT.right = temp;

		NodeHT.height = Math.max(height(NodeHT.left), height(NodeHT.right)) + 1;
		rightChild.height = Math.max(height(rightChild.left), height(rightChild.right)) + 1;

		return rightChild;
	}

	int getBalance(NodeHT NodeHT) {
		if (NodeHT == null)
			return 0;
		return height(NodeHT.left) - height(NodeHT.right);
	}

	NodeHT insert(NodeHT root, Card card) {

		int key = card.getHcur();

		if (root == null) {
			NodeHT node = new NodeHT(key);
			node.cardQueue.enqueue(card);
			cardCount++;

			return node;
		}
		if (key < root.key)
			root.left = insert(root.left, card);
		else if (key > root.key)
			root.right = insert(root.right, card);
		else {
			root.cardQueue.enqueue(card);
			cardCount++;

			return root;
		}

		root.height = 1 + Math.max(height(root.left), height(root.right));

		int balance = getBalance(root);

		if (balance > 1 && key < root.left.key)
			return rightRotate(root);

		if (balance < -1 && key > root.right.key)
			return leftRotate(root);

		if (balance > 1 && key > root.left.key) {
			root.left = leftRotate(root.left);
			return rightRotate(root);
		}

		if (balance < -1 && key < root.right.key) {
			root.right = rightRotate(root.right);
			return leftRotate(root);
		}

		return root;
	}

	NodeHT insertToDiscardPile(NodeHT root, Card card) {

		int key = card.getHmissing();

		if (root == null) {
			NodeHT node = new NodeHT(key);
			node.cardQueue.enqueue(card);
			cardCount++;

			return node;
		}
		if (key < root.key)
			root.left = insertToDiscardPile(root.left, card);
		else if (key > root.key)
			root.right = insertToDiscardPile(root.right, card);
		else {
			root.cardQueue.enqueue(card);
			cardCount++;

			return root;
		}

		root.height = 1 + Math.max(height(root.left), height(root.right));

		int balance = getBalance(root);

		if (balance > 1 && key < root.left.key)
			return rightRotate(root);

		if (balance < -1 && key > root.right.key)
			return leftRotate(root);

		if (balance > 1 && key > root.left.key) {
			root.left = leftRotate(root.left);
			return rightRotate(root);
		}

		if (balance < -1 && key < root.right.key) {
			root.right = rightRotate(root.right);
			return leftRotate(root);
		}

		return root;
	}

	NodeHT minValueNode(NodeHT node) {
		NodeHT current = node;
		while (current.left != null)
			current = current.left;
		return current;
	}

	NodeHT maxValueNode(NodeHT node) {
		NodeHT current = node;
		while (current.right != null)
			current = current.right;
		return current;
	}

	NodeHT deleteNodeByKey(NodeHT root, int key) {
		if (root == null)
			return root;

		if (key < root.key)
			root.left = deleteNodeByKey(root.left, key);
		else if (key > root.key)
			root.right = deleteNodeByKey(root.right, key);
		else {
			if ((root.left == null) || (root.right == null)) {
				NodeHT temp = (root.left != null) ? root.left : root.right;
				if (temp == null)
					root = null;
				else
					root = temp;
			} else {
				NodeHT temp = minValueNode(root.right);
				root.key = temp.key;
				root.cardQueue = temp.cardQueue;
				root.right = deleteNodeByKey(root.right, temp.key);

			}

		}

		if (root == null)
			return root;

		root.height = 1 + Math.max(height(root.left), height(root.right));

		int balance = getBalance(root);

		if (balance > 1 && getBalance(root.left) >= 0)
			return rightRotate(root);

		if (balance > 1 && getBalance(root.left) < 0) {
			root.left = leftRotate(root.left);
			return rightRotate(root);
		}

		if (balance < -1 && getBalance(root.right) <= 0)
			return leftRotate(root);

		if (balance < -1 && getBalance(root.right) > 0) {
			root.right = rightRotate(root.right);
			return leftRotate(root);
		}

		return root;
	}

	NodeHT searchLowestGreaterOrEqualThan(NodeHT root, int key) {
		NodeHT result = null;

		while (root != null) {
			if (root.key >= key) {
				// This node is a potential answer; try to find a smaller one on the left
				result = root;
				root = root.left;
			} else {
				// Go right to find a larger key
				root = root.right;
			}
		}

		return result;
	}

	NodeHT searchLowestGreaterThan(NodeHT root, int key) {
		NodeHT result = null;

		while (root != null) {
			if (root.key > key) {
				// This node is a potential answer; try to find a smaller one on the left
				result = root;
				root = root.left;
			} else {
				// Go right to find a larger key
				root = root.right;
			}
		}

		return result;
	}

	NodeHT searchHighestLowerOrEqualThan(NodeHT root, int key) {
		NodeHT result = null;

		while (root != null) {
			if (root.key <= key) {
				// This node could be the answer; try to find a larger one on the right
				result = root;
				root = root.right;
			} else {
				// Go left to find a smaller key
				root = root.left;
			}
		}

		return result;
	}

	NodeHT searchHighestLowerThan(NodeHT root, int key) {
		NodeHT result = null;

		while (root != null) {
			if (root.key < key) {
				// This node could be the answer; try to find a larger one on the right
				result = root;
				root = root.right;
			} else {
				// Go left to find a smaller key
				root = root.left;
			}
		}

		return result;
	}

}
