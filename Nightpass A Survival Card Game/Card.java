
public class Card {

	String name;

	private int Abase, Hbase;
	private int Acur, Hcur;
	private int Hmissing;
	static int insertionOrder = 0; // did not use much since using Queue
	private int cardInsertionOrder;

	Card(String name, int att, int hp) {
		this.name = name;
		this.Abase = this.Acur = att;
		this.Hbase = this.Hcur = hp;
		this.Hmissing = 0;
		cardInsertionOrder = 0;
	}

	public void updateInsertionOrder() {
		cardInsertionOrder = insertionOrder++;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAbase() {
		return Abase;
	}

	public void setAbase(int abase) {
		Abase = abase;
	}

	public int getAcur() {
		return Acur;
	}

	public void setAcur(int acur) {
		Acur = acur;
	}

	public int getHcur() {
		return Hcur;
	}

	public void setHcur(int hcur) {
		Hcur = hcur;
	}

	public int getCardInsertionOrder() {
		return cardInsertionOrder;
	}

	public void setCardInsertionOrder(int cardInsertionOrder) {
		this.cardInsertionOrder = cardInsertionOrder;
	}

	public int getHbase() {
		return Hbase;
	}

	public int getHmissing() {
		return Hmissing;
	}

	public void setHmissing(int hmissing) {
		Hmissing = hmissing;
	}

}
