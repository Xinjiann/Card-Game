package structures.basic;

/**
 * A basic representation of of the Player. A player has health and mana.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Player {
	// variables
	int health;
	int mana;
	Hand hand;
	Deck deck;

	// constructor1
	public Player() {
		this.health = 20;
		this.mana = 0;
	}

	// constructor2
	public Player(int health, int mana) {
		this.health = health;
		this.mana = mana;
	}

	// getters and setters
	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public Deck getDeck() {
		return deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	// set mana, have a limit of 9
	public void addMana(int turnCount) {
		mana += turnCount;
		mana = Math.min(mana, 9);
	}
}
