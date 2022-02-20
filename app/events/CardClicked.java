package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
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

		Card clickedCard = gameState.getHumanPlayer().getHand().getHandList().get(handPosition);
		Card preClickedCard = gameState.getCardSelected();

		//firstly remove the highlight on the board
		if(preClickedCard == clickedCard){
			removeHighlight(out, gameState);
			gameState.setCardSelected(null);
		}
		//first justify the card type(attack or assist), then determine the highlight area
		else {
			Avatar humanAvator = gameState.getHumanAvatar();
			Position pos = humanAvator.getPosition();
			if(clickedCard.getId() == 8 || clickedCard.getId() == 9){
				//spell card type, complete later
				//TODO
			}else{
				//unit type card
				nonSpellHighlight(out, gameState, pos);
				gameState.setCardSelected(clickedCard);
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

	private void nonSpellHighlight(ActorRef out, GameState gameState,Position position){
		int x_max = gameState.gameBoard.getGameBoard()[0].length;
		int y_max = gameState.gameBoard.getGameBoard().length;
		for (int i=position.getTilex()-1; i< position.getTilex()+2; i++) {
			for (int j = position.getTiley() - 1; j < position.getTiley() + 2; j++) {
				// make sure the tile is on the board
				if (i >= 0 && i<x_max && j >= 0 && j<y_max) {
					Monster otherUnit = gameState.gameBoard.getGameBoard()[j][i].getUnitOnTile();
					if (otherUnit==null) {
						BasicCommands.drawTile(out, gameState.gameBoard.getGameBoard()[j][i], 1);
					}
				}

			}
		}
	}

}
