package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.UnitAnimationType;
import structures.basic.Monster;
import structures.basic.Tile;
import java.util.ArrayList;
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
//		Tile[][] allTile =  gameState.getGameBoard().getGameBoard();
//		for (int i = 0; i < allTile.length; i++) {
//			Tile[] tiles = allTile[i];
//			for (int j = 0; j < tiles.length; j++) {
//				Tile tile = tiles[j];
//				BasicCommands.drawTile(out, tile, 0);
//			}
//		}
		Monster monster = gameState.getUnitSelected();
		Card card = gameState.getCardSelected();
		if (monster != null) {
			CommonUtils.rmMonsterHighlightTiles(monster, gameState, out);
			gameState.setUnitSelected(null);
		}
		if (card != null) {
			CommonUtils.rmAdjTiles(gameState.getHumanAvatar(), gameState, out);
			gameState.setCardSelected(null);
		}
	}
}


