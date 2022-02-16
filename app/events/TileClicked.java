package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.Unit;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		Tile clickedTile = gameState.getGameBoard().getTile(tilex,tiley);
		Unit unit = clickedTile.getUnitOnTile();

		if (gameState.getUnitSelected() != null) {
			// TODO
			// attack logic
			removeHighlight(out, gameState);
			gameState.setUnitSelected(null);

		} else if (gameState.getCardSelected() != null) {
			System.out.println("highlight available placement and attack tile");
		} else {
			// nothing selected before
			if (unit != null && unit.getOwner() == gameState.getTurnOwner() && !unit.isAttacked()) {
				if (unit.isMoved()) {
					movedHighlight(out, gameState, tilex, tiley);
				} else {
					notMovedHighlight(out, gameState, tilex, tiley);
				}
				gameState.setUnitSelected(unit);
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

	private void movedHighlight(ActorRef out, GameState gameState, int x, int y) {
		Board board = gameState.gameBoard;

		for (int i=x-2; i<x+2; i++) {
			for (int j = y - 2; j < y + 2; j++) {
				// make sure the tile is on the board
				if (i >= 0 && j >= 0) {
					Unit otherUnit = board.getGameBoard()[j][i].getUnitOnTile();
					if (otherUnit!=null && otherUnit.getOwner()!=gameState.getTurnOwner()) {
						BasicCommands.drawTile(out, board.getGameBoard()[j][i], 2);
					}
				}
			}
		}
	}

	private void notMovedHighlight(ActorRef out, GameState gameState, int x, int y) {

		Board board = gameState.gameBoard;

		for (int i=x-3; i<x+3; i++) {
			for (int j=y-3; j<y+3; j++) {
				// make sure the tile is on the board
				if (i>=0 && j>=0) {
					Unit otherUnit = board.getGameBoard()[j][i].getUnitOnTile();
					if (otherUnit!=null && otherUnit.getOwner()!=gameState.getTurnOwner()) {
						BasicCommands.drawTile(out, board.getGameBoard()[j][i], 2);
					} else if (otherUnit!=null && otherUnit.getOwner()==gameState.getTurnOwner()) {
					} else {
						// 2 steps available move
						if ((Math.abs(i-x)+Math.abs(j-y))<3 && (Math.abs(i-x)+Math.abs(j-y))!=0) {
							BasicCommands.drawTile(out, board.getGameBoard()[j][i], 1);
						}
					}
				}
			}
		}

	}

}
