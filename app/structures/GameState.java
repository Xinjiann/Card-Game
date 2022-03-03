package structures;

import structures.basic.AiPlayer;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Monster;
import structures.basic.Player;
import structures.basic.abilities.AbilityToCard;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	
	public boolean something = false;

	public Board gameBoard;
	private Player humanPlayer;
	private AiPlayer aiPlayer;
	private Avatar humanAvatar;
	private Avatar aiAvatar;
	private Monster unitSelected;
	private Card cardSelected;
	private Player turnOwner;
	private int cardPos;
	private int turnCount;

	public GameState() {
		gameBoard = new Board();

		// init abilities to cards
		AbilityToCard.init();

		// human deck
		Deck humanDeck = new Deck();
		humanDeck.humanDeck();
		humanPlayer = new Player(20, 9);
		// human hand
		humanPlayer.setDeck(humanDeck);
//		humanDeck.shuffleDeck();
		Hand humanHand = new Hand();
		humanPlayer.setHand(humanHand);
		humanHand.initialHand(humanDeck);

		// ai deck
		Deck aiDeck = new Deck();
		aiDeck.aiDeck();
		aiPlayer = new AiPlayer(20, 9);
		// ai hand
		aiPlayer.setDeck(aiDeck);
//		aiDeck.shuffleDeck();
		Hand aiHand = new Hand();
		aiPlayer.setHand(aiHand);
		aiHand.initialHand(aiDeck);

		humanAvatar =  BasicObjectBuilders.loadAvatar(StaticConfFiles.humanAvatar, 0, humanPlayer, Avatar.class);
		humanAvatar.setAttack(2);
		humanAvatar.setHealth(20);
		aiAvatar = BasicObjectBuilders.loadAvatar(StaticConfFiles.aiAvatar, 1, aiPlayer, Avatar.class);
		aiAvatar.setAttack(2);
		aiAvatar.setHealth(20);
		turnOwner = humanPlayer;
	}

	public void setCardPos(int handPosition) {
		this.cardPos = handPosition;
	}

	public Board getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(Board gameBoard) {
		this.gameBoard = gameBoard;
	}

	public Player getHumanPlayer() {
		return humanPlayer;
	}

	public void setHumanPlayer(Player humanPlayer) {
		this.humanPlayer = humanPlayer;
	}

	public AiPlayer getAiPlayer() {
		return aiPlayer;
	}

	public void setAiPlayer(AiPlayer aiPlayer) {
		this.aiPlayer = aiPlayer;
	}

	public Avatar getHumanAvatar() {
		return humanAvatar;
	}

	public void setHumanAvatar(Avatar humanAvatar) {
		this.humanAvatar = humanAvatar;
	}

	public Avatar getAiAvatar() {
		return aiAvatar;
	}

	public void setAiAvatar(Avatar aiAvatar) {
		this.aiAvatar = aiAvatar;
	}

	public Monster getUnitSelected() {
		return unitSelected;
	}

	public void setUnitSelected(Monster unitSelected) {
		this.unitSelected = unitSelected;
	}

	public Card getCardSelected() {
		return cardSelected;
	}

	public void setCardSelected(Card cardSelected) {
		this.cardSelected = cardSelected;
	}

	public Player getTurnOwner() {
		return turnOwner;
	}

	public void setTurnOwner(Player turnOwner) {
		this.turnOwner = turnOwner;
	}

	public int getCardPos() {
		return cardPos;
	}

	public void turnChange() {
		this.turnOwner = (turnOwner == humanPlayer ? aiPlayer : humanPlayer);
	}

	public void giveMana() {
		if (turnOwner == humanPlayer) {
			this.turnCount +=1;
			humanPlayer.addMana(this.turnCount);
		} else {
			aiPlayer.addMana(this.turnCount);
		}
	}
}
