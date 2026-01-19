
public class Game {

	int strangerPoints;
	int playerPoints;
	AttackTree attackTree; // Also can be called "Deck"
	HealthTree discardPile;

	Game() {

		attackTree = new AttackTree();
		discardPile = new HealthTree();

	}

	public String draw_card(String name, int att, int hp) {

		Card card = new Card(name, att, hp);

		attackTree.root = attackTree.insert(attackTree.root, card);

		return "Added " + name + " to the deck";

	}

	public int phase2(int heal) {
		NodeHT nodeHT = null;
		Card card;
		int revived = 0;

		if (heal == 0) {
			return 0;
		}

		// First priority

		nodeHT = discardPile.searchHighestLowerOrEqualThan(discardPile.root, heal);

		// Revive as much as you can
		while (nodeHT != null && heal > 0) {

			card = nodeHT.cardQueue.dequeue();
			if (nodeHT.cardQueue.isEmpty()) {
				discardPile.root = discardPile.deleteNodeByKey(discardPile.root, card.getHmissing());
			}
			discardPile.cardCount--;

			heal -= card.getHmissing();

			card.setHmissing(0);
			card.setAbase((int) Math.floor(card.getAbase() * 0.9));
			card.setAcur(card.getAbase());
			card.setHcur(card.getHbase());

			attackTree.root = attackTree.insert(attackTree.root, card);
			revived++;

			nodeHT = discardPile.searchHighestLowerOrEqualThan(discardPile.root, heal);

		}

		// Third priority
		if (heal > 0 && discardPile.root != null) {

			nodeHT = discardPile.minValueNode(discardPile.root);

			if (nodeHT != null) {
				card = nodeHT.cardQueue.dequeue();
				if (nodeHT.cardQueue.isEmpty()) {
					discardPile.root = discardPile.deleteNodeByKey(discardPile.root, card.getHmissing());
				}
				discardPile.cardCount--;

				card.setHmissing(card.getHmissing() - heal);
				heal = 0;
				card.setAbase((int) Math.floor(card.getAbase() * 0.95));

				discardPile.root = discardPile.insertToDiscardPile(discardPile.root, card);

			}
		}

		return revived;
	}

	public String battle(int att, int hp, int heal) {

		NodeAT nodeAT = null;
		NodeHT nodeHT = null;
		Card card = null;
		int priority = 0;
		String out = "";
		int revived = 0;

		int cardAtt, cardHp;
		int strangerAtt = att, strangerHp = hp;

		// Search for first Priority
		if (card == null) {

			nodeAT = attackTree.firstPriority(attackTree.root, att, hp);

			if (nodeAT != null) {
				nodeHT = nodeAT.healthTree.searchLowestGreaterThan(nodeAT.healthTree.root, att);

				card = nodeHT.cardQueue.dequeue();
				priority = 1;

			}

		}

		// Search for Second Priority
		if (card == null) {

			nodeAT = attackTree.secondPriority(attackTree.root, att, hp);

			if (nodeAT != null) {
				nodeHT = nodeAT.healthTree.searchLowestGreaterThan(nodeAT.healthTree.root, att);

				card = nodeHT.cardQueue.dequeue();
				priority = 2;

			}
		}

		// 3th priority
		if (card == null) {

			nodeAT = attackTree.thirdPriority(attackTree.root, att, hp);

			if (nodeAT != null) {
				nodeHT = nodeAT.healthTree.minValueNode(nodeAT.healthTree.root);
				card = nodeHT.cardQueue.dequeue();
				priority = 3;
			}

		}
		// 4th priority
		if (card == null) {

			nodeAT = attackTree.maxValueNode(attackTree.root);
			if (nodeAT != null) {

				nodeHT = nodeAT.healthTree.minValueNode(nodeAT.healthTree.root);
				card = nodeHT.cardQueue.dequeue();
				priority = 4;

			}
		}

		// Do the things
		if (card != null) {

			// Update the Deck
			if (nodeHT.cardQueue.isEmpty()) {
				nodeAT.healthTree.root = nodeAT.healthTree.deleteNodeByKey(nodeAT.healthTree.root, card.getHcur());
			}
			nodeAT.healthTree.cardCount--;
			if (nodeAT.healthTree.root == null) {
				attackTree.root = attackTree.deleteNodeByKey(attackTree.root, card.getAcur());
			}
			attackTree.cardCount--;
			attackTree.updateMaxHealthInNode(nodeAT);

			/*
			 * decrease the card count, delete nodes if necessary calculate new hp and
			 * att's, calculate points and add them if hp is lower than 1 add it to discard
			 * (or delete) if not insert it to deck (attackTree), create out string
			 */

			cardAtt = card.getAcur();
			cardHp = card.getHcur();

			cardHp = cardHp - strangerAtt;
			card.setHcur(Math.max(0, cardHp));
			strangerHp = strangerHp - cardAtt;
			cardAtt = Math.max(1, (int) Math.floor(card.getAbase() * card.getHcur() / card.getHbase()));
			card.setAcur(cardAtt);
			card.setHmissing(card.getHbase() - card.getHcur());

			if (strangerHp <= 0)
				playerPoints += 2;
			else if (strangerHp <= hp)
				playerPoints += 1;

			if (cardHp <= 0)
				strangerPoints += 2;
			else if (cardHp <= card.getHbase())
				strangerPoints += 1;

			out += "Found with priority " + priority + ", Survivor plays " + card.getName();

			if (card.getHcur() == 0) {

				discardPile.root = discardPile.insertToDiscardPile(discardPile.root, card);

				out += ", the played card is discarded";
			} else {
				attackTree.root = attackTree.insert(attackTree.root, card);

				out += ", the played card returned to deck";
			}

		} else {
			strangerPoints += 2;
			out += "No card to play";
		}

		// PHASE 2

		revived = phase2(heal);

		// PHASE 2

		out += ", " + revived + " cards revived";

		return out;
	}

	public String findWinning() {
		if (playerPoints >= strangerPoints) {
			return "The Survivor, Score: " + playerPoints;
		} else {
			return "The Stranger, Score: " + strangerPoints;
		}
	}

	public String deckCount() {

		return "Number of cards in the deck: " + attackTree.getCardCount();

	}

	public String discardPileCount() {
		return "Number of cards in the discard pile: " + discardPile.getCardCount();
	}

	public String steal_card(int att, int hp) {

		NodeAT nodeAT = null;
		NodeHT nodeHT = null;
		Card card = null;
		String out = "";

		// Search
		nodeAT = attackTree.steal(attackTree.root, att, hp);

		if (nodeAT != null) {

			nodeHT = nodeAT.healthTree.searchLowestGreaterThan(nodeAT.healthTree.root, hp);
			card = nodeHT.cardQueue.dequeue();

		}

		// Check
		if (card == null) {
			out += "No card to steal";
		} else {
			out += "The Stranger stole the card: " + card.getName();

			// Update the Deck
			if (nodeHT.cardQueue.isEmpty()) {
				nodeAT.healthTree.root = nodeAT.healthTree.deleteNodeByKey(nodeAT.healthTree.root, card.getHcur());
			}
			nodeAT.healthTree.cardCount--;

			if (nodeAT.healthTree.root == null) {
				attackTree.root = attackTree.deleteNodeByKey(attackTree.root, card.getAcur());
			}
			attackTree.cardCount--;
			attackTree.updateMaxHealthInNode(nodeAT);
		}

		return out;
	}

}