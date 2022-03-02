package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.abilities.Ability;
import structures.basic.abilities.Pyromancer;
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

		// change state
		endTurn(out, gameState);

		// ai play
		if (gameState.getTurnOwner() == gameState.getAiPlayer()) {
			AiPlayGame aiPlayGame = new AiPlayGame(gameState, out);
			aiPlayGame.paly();
		}

	}

	public void endTurn(ActorRef out, GameState gameState) {
		// clear mana
		gameState.getTurnOwner().setMana(0);
		BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
		BasicCommands.setPlayer2Mana(out, gameState.getAiPlayer());
		
		// default all units on board
		for(int i=0;i<5;i++)
			for(int j=0;j<9;j++) {
				if(!gameState.gameBoard.getGameBoard()[i][j].getAvailable()) {
					Monster monster=gameState.gameBoard.getGameBoard()[i][j].getUnitOnTile();
					monster.setFrozen(false);
					monster.setBeenProvoke(false);
					if (monster.getProvokeOwners() != null) {
						monster.getProvokeOwners().clear();
					}
					monster.setAttackCount(monster.getMaxAttackCount());
					monster.setMovesLeft(monster.getMaxMove());
				}
			}
			

		gameState.getTurnOwner().getHand().drawCard(gameState.getTurnOwner().getDeck());

		// when human player getting a new card, re-display all card in hand
		if(gameState.getTurnOwner() == gameState.getHumanPlayer()) {
			Card card = gameState.getTurnOwner().getDeck().getCardList().get(0);
			int oldCardSize = (gameState.getTurnOwner().getHand().getHandList().size()) -1;
			CommonUtils.drawCardsInHand(out, gameState, oldCardSize, gameState.getTurnOwner().getHand().getHandList());
		}

		// set new turn owner and give mana
		gameState.turnChange();
		gameState.giveMana();
		BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
		BasicCommands.setPlayer2Mana(out, gameState.getAiPlayer());
	}


}
