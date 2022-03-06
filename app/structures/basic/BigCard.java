package structures.basic;

/**
 * BigCard represents the expanded card version that that appears on mouse-over
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class BigCard {
	// variables
	int attack;
	int health;
	String[] rulesTextRows;
	String[] cardTextures;

	// Constructor 1
	public BigCard() {
	}

	// Constructor 2
	public BigCard(int attack, int health, String[] rulesTextRows, String[] cardTextures) {
		super();
		this.attack = attack;
		this.health = health;
		this.rulesTextRows = rulesTextRows;
		this.cardTextures = cardTextures;
	}

	// getters and setters
	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public String[] getRulesTextRows() {
		return rulesTextRows;
	}

	public void setRulesTextRows(String[] rulesTextRows) {
		this.rulesTextRows = rulesTextRows;
	}

	public String[] getCardTextures() {
		return cardTextures;
	}

	public void setCardTextures(String[] cardTextures) {
		this.cardTextures = cardTextures;
	}

}
