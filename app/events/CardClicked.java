package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import structures.GameState;
import structures.basic.*;
import structures.basic.abilities.Ability;
import structures.basic.abilities.WhenToCall;
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

		if (gameState.isLock()) {
			BasicCommands.addPlayer1Notification(out, "please wait...", 2);
			return;
		}

		int handPosition = message.get("position").asInt();

		Card clickedCard = gameState.getHumanPlayer().getHand().getHandList().get(handPosition);
		Card preClickedCard = gameState.getCardSelected();
		// unselect unit
		gameState.setUnitSelected(null);
		//firstly remove the highlight on the board
		if(preClickedCard == clickedCard){
			CommonUtils.rmAllHighlight(gameState, out);
			gameState.setCardSelected(null);
		}
		//first justify the card type(attack or assist), then determine the highlight area
		else {
			// check mana
			if (clickedCard.getManacost() <= gameState.getTurnOwner().getMana()) {
				// set selected card
				gameState.setCardSelected(clickedCard);
				if(clickedCard.getType().equals("spell")){
					// remove all highlights
					CommonUtils.rmAllHighlight(gameState, out);
					//spell card type
					switch (clickedCard.getCardname()) {
						case "Sundrop Elixir" :
							gameState.gameBoard.setAllUnitTiles();
							break;
						case "Truestrike" :
							gameState.gameBoard.setAllEnemyTiles(gameState);
							break;
						case "Staff of Y'Kir'" :
							gameState.gameBoard.setAvatarArea(gameState);
							break;
						case "Entropic Decay" :
							gameState.gameBoard.setNoneAvatarUnitArea(gameState);
							break;
					}
					CommonUtils.sleep();
					CommonUtils.drawTilesInBatch(out, gameState.gameBoard.getSpellArea(), 1);
				}else{
					//unit type card
					// remove all highlights
					CommonUtils.rmAllHighlight(gameState, out);
					CommonUtils.sleep();
					// check ability
					if (clickedCard.getAbilityList() != null && !clickedCard.getAbilityList().isEmpty()) {
						for (Ability ability : clickedCard.getAbilityList()) {
							if (ability.getWhenTOCall() == WhenToCall.cardClick) {
								ability.execute(null, gameState, out);
							}
						}

					}
					// highlight area
					ArrayList<Monster> friendlyUnits = gameState.getGameBoard().friendlyUnitsWithAvatar(gameState.getTurnOwner());
					gameState.getGameBoard().setSummonArea(friendlyUnits);
					CommonUtils.drawTilesInBatch(out, gameState.gameBoard.getSummonArea(), 1);
					gameState.setCardPos(handPosition);
				}
			} else {
				BasicCommands.addPlayer1Notification(out, "Mana not sufficient", 2);
			}
		}
		
	}


}
