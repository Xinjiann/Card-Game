package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import utils.CommonUtils;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{


	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int handPosition = message.get("position").asInt();

		Card clickedCard = gameState.getHumanPlayer().getHand().getHandList().get(handPosition);
		Card preClickedCard = gameState.getCardSelected();

		// set selected card
		gameState.setCardSelected(clickedCard);
		//firstly remove the highlight on the board
		if(preClickedCard == clickedCard){
			CommonUtils.rmAllHighlight(gameState, out);
			gameState.setCardSelected(null);
		}
		//first justify the card type(attack or assist), then determine the highlight area
		else {
			// check mana
			if (clickedCard.getManacost() <= gameState.getTurnOwner().getMana()) {
				Avatar humanAvatar = gameState.getHumanAvatar();
				Position pos = humanAvatar.getPosition();
				if(clickedCard.getType().equals("spell")){
					// remove all highlights
					CommonUtils.rmAllHighlight(gameState, out);
					CommonUtils.sleep();
					//spell card type, complete later
					switch (clickedCard.getCardname()) {
						case "Sundrop Elixir" -> gameState.gameBoard.setAllUnitTiles();
						case "Truestrike" -> gameState.gameBoard.setAllEnemyTiles(gameState);
						case "Staff of Y'Kir'" -> gameState.gameBoard.setAvatarArea(gameState);
						case "Entropic Decay" -> gameState.gameBoard.setNoneAvatarUnitArea();
					}
					CommonUtils.listHighlight(out, gameState.gameBoard.getSpellArea());
				}else{
					//unit type card
					// remove all highlights
					CommonUtils.rmAllHighlight(gameState, out);
					CommonUtils.sleep();
					// highlight area
					gameState.getGameBoard().setSummonArea(pos);
					CommonUtils.listHighlight(out, gameState.gameBoard.getSummonArea());
					gameState.setCardPos(handPosition);
				}
			} else {
				BasicCommands.addPlayer1Notification(out, "Mana not sufficient", 2);
			}
		}
		
	}


}
