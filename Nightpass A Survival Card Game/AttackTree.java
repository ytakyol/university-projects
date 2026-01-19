class NodeAT {

	int key; // Attack Point
	HealthTree healthTree;
	int height;

	int maxHealthInNode;

	NodeAT left, right;

	NodeAT(int key) {
		this.key = key;
		this.height = 1;
		healthTree = new HealthTree();
		left = null;
		right = null;
	}

}

//Will Store Attack Nodes That Will store Health Tree
public class AttackTree {

	NodeAT root;
	int cardCount;

	AttackTree() {
		root = null;
		cardCount = 0;
	}

	public int getCardCount() {
		return cardCount;
	}

	int height(NodeAT NodeAT) {
		if (NodeAT == null)
			return 0;
		return NodeAT.height;
	}

	NodeAT rightRotate(NodeAT NodeAT) {
		NodeAT leftChild = NodeAT.left;
		NodeAT temp = leftChild.right;

		leftChild.right = NodeAT;
		NodeAT.left = temp;

		NodeAT.height = Math.max(height(NodeAT.left), height(NodeAT.right)) + 1;
		leftChild.height = Math.max(height(leftChild.left), height(leftChild.right)) + 1;

		return leftChild;
	}

	NodeAT leftRotate(NodeAT NodeAT) {
		NodeAT rightChild = NodeAT.right;
		NodeAT temp = rightChild.left;

		rightChild.left = NodeAT;
		NodeAT.right = temp;

		NodeAT.height = Math.max(height(NodeAT.left), height(NodeAT.right)) + 1;
		rightChild.height = Math.max(height(rightChild.left), height(rightChild.right)) + 1;

		return rightChild;
	}

	int getBalance(NodeAT NodeAT) {
		if (NodeAT == null)
			return 0;
		return height(NodeAT.left) - height(NodeAT.right);
	}

	// Updates the max health stored in that specific node
	void updateMaxHealthInNode(NodeAT node) {

		if (node == null) {
			return;
		}

		if (node.healthTree.root == null) {
			node.maxHealthInNode = 0;
			return;
		}

		node.maxHealthInNode = node.healthTree.maxValueNode(node.healthTree.root).key;

	}

	NodeAT insert(NodeAT root, Card card) {

		int key = card.getAcur();
		int health = card.getHcur();

		if (root == null) {

			NodeAT node = new NodeAT(key);
			node.healthTree.root = node.healthTree.insert(node.healthTree.root, card);
			node.maxHealthInNode = health;
			cardCount++;

			return node;

		}
		if (key < root.key) {
			root.left = insert(root.left, card);
		} else if (key > root.key) {
			root.right = insert(root.right, card);
		} else {
			root.healthTree.root = root.healthTree.insert(root.healthTree.root, card);
			root.maxHealthInNode = Math.max(root.maxHealthInNode, health);
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

	// Helper: Find the node with the smallest key (used for inorder successor)
	NodeAT minValueNode(NodeAT node) {
		NodeAT current = node;
		while (current.left != null)
			current = current.left;
		return current;
	}

	NodeAT maxValueNode(NodeAT node) {
		NodeAT current = node;
		while (current.right != null)
			current = current.right;
		return current;
	}

	// Delete a node by its key (attack value)
	NodeAT deleteNodeByKey(NodeAT root, int key) {
		if (root == null)
			return null;

		// Step 1: Perform normal BST deletion
		if (key < root.key)
			root.left = deleteNodeByKey(root.left, key);
		else if (key > root.key)
			root.right = deleteNodeByKey(root.right, key);
		else {
			// Node found — handle deletion

			// If node has one or no child
			if ((root.left == null) || (root.right == null)) {
				NodeAT temp = (root.left != null) ? root.left : root.right;

				// No child case
				if (temp == null) {
					root = null;
				} else { // One child case
					root = temp;
				}
			} else {
				// Node with two children:
				// Get inorder successor (smallest in the right subtree)
				NodeAT temp = minValueNode(root.right);

				// Copy the successor's data to this node
				root.key = temp.key;
				root.healthTree = temp.healthTree; // copy its health subtree

				// Delete the inorder successor
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

	// Search for 1st priority if there exists return NodeAT if not return null
	NodeAT firstPriority(NodeAT root, int strATT, int strHP) {
		NodeAT answer = null;
		if (root == null)
			return null;
		if (root.key > strHP)
			answer = firstPriority(root.left, strATT, strHP);
		if (answer == null)
			if (root.key >= strHP && root.maxHealthInNode > strATT)
				answer = root;
			else
				answer = firstPriority(root.right, strATT, strHP);
		return answer;
	}

	// Search for 2nd priority if there exists return NodeAT if not return null
	NodeAT secondPriority(NodeAT root, int strATT, int strHP) {
		NodeAT answer = null;
		if (root == null)
			return null;
		if (root.key < strHP - 1)
			answer = secondPriority(root.right, strATT, strHP);
		if (answer == null)
			if (root.key < strHP && root.maxHealthInNode > strATT)
				answer = root;
			else
				answer = secondPriority(root.left, strATT, strHP);
		return answer;
	}

	// Search for 3rd priority if there exists return NodeAT if not return null
	NodeAT thirdPriority(NodeAT root, int strATT, int strHP) {
		NodeAT answer = null;

		if (root == null)
			return null;
		if (root.key > strHP)
			answer = thirdPriority(root.left, strATT, strHP);
		if (answer == null)
			if (root.key >= strHP)
				answer = root;
			else
				answer = thirdPriority(root.right, strATT, strHP);
		return answer;
	}

	// Search for steal card if it exists return NodeAT if not return null
	NodeAT steal(NodeAT root, int att, int hp) {
		NodeAT answer = null;

		if (root == null)
			return null;
		if (root.key > att + 1)
			answer = steal(root.left, att, hp);
		if (answer == null)
			if (root.key > att && root.maxHealthInNode > hp)
				answer = root;
			else
				answer = steal(root.right, att, hp);
		return answer;
	}

}