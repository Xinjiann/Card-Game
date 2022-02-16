package structures;

import structures.basic.Board;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.Unit;
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
	private Player aiPlayer;
	private Unit humanAvatar;
	private Unit aiAvatar;

	public GameState() {
		gameBoard = new Board();

		// human deck
		Deck humanDeck = new Deck();
		humanDeck.humanDeck();
		Player humanPlayer = new Player(20, 2);
		// human hand
		humanPlayer.setDeck(humanDeck);
		Hand humanHand = new Hand();
		humanPlayer.setHand(humanHand);
		humanHand.initialHand(humanDeck);

		// ai deck
		Deck aiDeck = new Deck();
		aiDeck.aiDeck();
		Player aiPlayer = new Player(20, 2);
		// ai hand
		aiPlayer.setDeck(aiDeck);
		Hand aiHand = new Hand();
		aiPlayer.setHand(aiHand);
		aiHand.initialHand(aiDeck);

		humanAvatar =  BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
		humanAvatar.setAttack(2);
		humanAvatar.setHealth(20);
		aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);
		aiAvatar.setAttack(2);
		aiAvatar.setHealth(20);
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

	public Player getAiPlayer() {
		return aiPlayer;
	}

	public void setAiPlayer(Player aiPlayer) {
		this.aiPlayer = aiPlayer;
	}

	public Unit getHumanAvatar() {
		return humanAvatar;
	}

	public void setHumanAvatar(Unit humanAvatar) {
		this.humanAvatar = humanAvatar;
	}

	public Unit getAiAvatar() {
		return aiAvatar;
	}

	public void setAiAvatar(Unit aiAvatar) {
		this.aiAvatar = aiAvatar;
	}
}
