package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import structures.GameState;
import structures.basic.Monster;
import structures.basic.Tile;
import utils.CommonUtils;

/**
 * In the user’s browser, the game is running in an infinite loop, where there is around a 1 second delay 
 * between each loop. Its during each loop that the UI acts on the commands that have been sent to it. A 
 * heartbeat event is fired at the end of each loop iteration. As with all events this is received by the Game 
 * Actor, which you can use to trigger game logic.
 * 
 * { 
 *   String messageType = “heartbeat”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Heartbeat implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// refresh player 1 stats
		BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
		CommonUtils.sleep();
		BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
		CommonUtils.sleep();


		// refresh player 2 stats
		BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());
		CommonUtils.sleep();
		BasicCommands.setPlayer2Mana(out, gameState.getAiPlayer());
		CommonUtils.sleep();

		//refresh all unit stats
		ArrayList<Tile> list = CommonUtils.getAllUnits(gameState);
		for (Tile tile : list) {
			Monster monster = tile.getUnitOnTile();
			BasicCommands.setUnitAttack(out, monster, monster.getAttack());
			CommonUtils.sleep();
			BasicCommands.setUnitHealth(out, monster, monster.getHealth());
			CommonUtils.sleep();
		}
	}

}
