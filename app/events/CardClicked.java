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

		//firstly remove the highlight on the board
		if(preClickedCard == clickedCard){
			CommonUtils.rmSummonHighlight(out, gameState.gameBoard.getSummonArea());
			gameState.setCardSelected(null);
		}
		//first justify the card type(attack or assist), then determine the highlight area
		else {
			// check mana
			if (clickedCard.getManacost() <= gameState.getTurnOwner().getMana()) {
				Avatar humanAvatar = gameState.getHumanAvatar();
				Position pos = humanAvatar.getPosition();
				if(clickedCard.getId() == 8 || clickedCard.getId() == 9){
					//spell card type, complete later
					//TODO
				}else{
					//unit type card
					gameState.getGameBoard().setSummonArea(out, gameState, pos);
					CommonUtils.summonHighlight(out, gameState.gameBoard.getSummonArea());
					gameState.setCardSelected(clickedCard);
					gameState.setCardPos(handPosition);
				}
			} else {
				BasicCommands.addPlayer1Notification(out, "Mana not sufficient", 2);
			}
		}
		
	}


}
