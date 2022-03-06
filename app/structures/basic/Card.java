package structures.basic;

import java.util.ArrayList;
import structures.basic.abilities.Ability;

/**
 * This is the base representation of a Card which is rendered in the player's
 * hand. A card has an id, a name (cardname) and a manacost. A card then has a
 * large and mini version. The mini version is what is rendered at the bottom of
 * the screen. The big version is what is rendered when the player clicks on a
 * card in their hand.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Card {
	// variables
	int id;

	String cardname;
	int manacost;
	MiniCard miniCard;
	BigCard bigCard;
	String unitConfigFiles;
	String type;

	private ArrayList<Ability> abilityList;

	// Constructor 1
	public Card() {
	};

	// Constructor 2
	public Card(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super();
		this.id = id;
		this.cardname = cardname;
		this.manacost = manacost;
		this.miniCard = miniCard;
		this.bigCard = bigCard;
		this.abilityList = new ArrayList<Ability>();
	}

	// getters and setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCardname() {
		return cardname;
	}

	public void setCardname(String cardname) {
		this.cardname = cardname;
	}

	public int getManacost() {
		return manacost;
	}

	public void setManacost(int manacost) {
		this.manacost = manacost;
	}

	public MiniCard getMiniCard() {
		return miniCard;
	}

	public void setMiniCard(MiniCard miniCard) {
		this.miniCard = miniCard;
	}

	public BigCard getBigCard() {
		return bigCard;
	}

	public void setBigCard(BigCard bigCard) {
		this.bigCard = bigCard;
	}

	public ArrayList<Ability> getAbilityList() {
		return abilityList;
	}

	public void setAbilityList(ArrayList<Ability> abilityList) {
		this.abilityList = abilityList;
	}

	public String getUnitConfigFiles() {
		return unitConfigFiles;
	}

	public void setUnitConfigFiles(String unitConfigFiles) {
		this.unitConfigFiles = unitConfigFiles;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
