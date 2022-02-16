package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

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

		Card clickedCard = gameState.getHandList().get(handPosition);


		//firstly remove the highlight on the board
		if(gameState.getCardSelected() != null && gameState.getUnitSelected() != null){
			removeHighlight(out, gameState);
			gameState.setCardSelected(null);
		}
		//first justify the card type(attack or assist), then determine the highlight area
		else {
			//spell card type, later do
			if(clickedCard.getId() == 8 || clickedCard.getId() == 9){
				//TODO
			}else{
				//unit type card

			}

		}
		
	}

	private void removeHighlight(ActorRef out, GameState gameState) {

		Board board = gameState.gameBoard;
		int x = board.getGameBoard().length;
		int y = board.getGameBoard()[0].length;
		for (int i=0; i<x; i++) {
			for (int j=0; j<y; j++) {
				Tile tile = board.getGameBoard()[i][j];
				BasicCommands.drawTile(out, tile, 0);
			}
		}

	}

}
