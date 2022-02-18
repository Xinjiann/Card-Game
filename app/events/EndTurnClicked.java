package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.abilities.Ability;
import structures.basic.abilities.Truestrike;
import utils.CommonUtils;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		Ability ability = new Truestrike(); //开发完新ablity后修改此行以进行测试

//		Ability ability = new SundropElixir();
//		Ability ability = new StaffOfYkir();
//		Ability ability = new EntropicDecay();

		// get selected monster
		Monster selectedMonster = gameState.getUnitSelected();

		if (selectedMonster != null) {
			Tile selectedTile = gameState.getGameBoard().getTile(selectedMonster.getPosition().getTilex(), selectedMonster.getPosition().getTiley());
			// execute ability
			ability.execute(selectedMonster, gameState);
			// update front end
			BasicCommands.playEffectAnimation(out, ability.getEffectAnimation(), selectedTile);
			BasicCommands.setUnitHealth(out, selectedMonster, selectedMonster.getHealth());
			BasicCommands.setUnitAttack(out, selectedMonster, selectedMonster.getAttack());
			// if target dead after execution
			if (selectedMonster.getHealth() == 0) {
				BasicCommands.playUnitAnimation(out, selectedMonster, UnitAnimationType.death);
				CommonUtils.longlongSleep(3000); //front end sleep for 3 sec
				BasicCommands.deleteUnit(out, selectedMonster);
				selectedTile.rmUnitOnTile();
				gameState.setUnitSelected(null);
			}
		} else {
			BasicCommands.addPlayer1Notification(out, "先选择随从", 2);
		}

	}


}
