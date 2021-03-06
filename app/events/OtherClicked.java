package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import utils.CommonUtils;


/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = “otherClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		if (gameState.isLock()) {
			BasicCommands.addPlayer1Notification(out, "please wait...", 2);
			return;
		}

		CommonUtils.rmAllHighlight(gameState, out);
		// unselect unit
		gameState.setUnitSelected(null);
		if (gameState.getCardSelected() != null) {
			// unselect card
			BasicCommands.drawCard(out, gameState.getCardSelected(), gameState.getCardPos(), 0);
			gameState.setCardSelected(null);
		}

	}
}


