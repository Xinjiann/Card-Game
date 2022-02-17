package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;

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
		Monster monster = clickedTile.getUnitOnTile();

		if (gameState.getUnitSelected() != null) {
			// TODO
			// attack logic
			afterUnitSelectedClick(monster, gameState, out, clickedTile);
			removeHighlight(out, gameState);
			gameState.setUnitSelected(null);
		} else if (gameState.getCardSelected() != null) {
			afterCardSelectedClick(monster, gameState, out);
			System.out.println("highlight available placement and attack tile");
		} else {
			// nothing selected before
			afterNothingClick(monster, gameState, out);
		}


	}

	private void afterNothingClick(Monster monster, GameState gameState, ActorRef out) {

		if (monster != null) {
			if (monster.getOwner() == gameState.getTurnOwner()) {
//				if (!monster.hasAttacked()) {
//					if (monster.hasMoved()) {
//						movedHighlight(out, gameState, monster.getPosition().getTilex(), monster.getPosition().getTiley());
//					} else {
//						notMovedHighlight(out, gameState, monster.getPosition().getTilex(), monster.getPosition().getTiley());
//					}
//					gameState.setUnitSelected(monster);
//				}
				displayAvailableTiles(gameState, out, monster);
				gameState.setUnitSelected(monster);
			} else {
				BasicCommands.addPlayer1Notification(out, "This unit does not belong to you", 1);
			}
		} else {
			// no unit on the tile
			BasicCommands.addPlayer1Notification(out, "No unit chosen", 1);
		}


	}

	private void displayAvailableTiles(GameState gameState, ActorRef out, Monster monster) {
		int x = monster.getPosition().getTilex();
		int y = monster.getPosition().getTiley();
		ArrayList<Tile> movableTiles = gameState.getGameBoard().movableTiles(x,y,monster.getMovesLeft());
		ArrayList <Tile> attachableTiles = gameState.getGameBoard().attachableTiles(x, y, monster.getMovesLeft());
		movableTiles.addAll(attachableTiles);

		for(Tile t : movableTiles) {
			if(attachableTiles.contains(t)) {
				BasicCommands.drawTile(out, t, 2);
			}
			else {
				BasicCommands.drawTile(out, t, 1);
			}
		}
	}

	private void afterCardSelectedClick(Monster clickedTile, GameState gameState, ActorRef out) {
	}

	private void afterUnitSelectedClick(Monster monster, GameState gameState, ActorRef out, Tile clickedTile) {
		if (clickedTile.getUnitOnTile() != null && clickedTile.getUnitOnTile().getOwner() == gameState.getTurnOwner()) {
			friendClick(monster, gameState, out, clickedTile);
		} else if (clickedTile.getUnitOnTile() != null && clickedTile.getUnitOnTile().getOwner() != gameState.getTurnOwner()) {
			enemyClick(monster, gameState, out, clickedTile);
		} else {
			moveClick(gameState, out, clickedTile);
		}
	}

	private void moveClick(GameState gameState, ActorRef out, Tile clickedTile) {

		Monster previousMonster = gameState.getUnitSelected();
		int previous_x = previousMonster.getPosition().getTilex();
		int previous_y = previousMonster.getPosition().getTiley();
		Tile previousTile = gameState.gameBoard.getTile(previous_x, previous_y);

		ArrayList <Tile> attachableTiles;
		ArrayList <Tile> movableTiles = new ArrayList<Tile>();
		movableTiles = gameState.getGameBoard().movableTiles(previous_x, previous_y, previousMonster.getMovesLeft());
		attachableTiles = gameState.getGameBoard().attachableTiles(previous_x, previous_y, previousMonster.getMovesLeft());
		movableTiles.addAll(attachableTiles);

		if((!movableTiles.isEmpty()) && movableTiles.contains(clickedTile)) {
			int deltaX = Math.abs(previous_x - clickedTile.getTilex());
			int deltaY = Math.abs(previous_y - clickedTile.getTiley());
			if ((deltaX+deltaY)>previousMonster.getMovesLeft()) {
				BasicCommands.addPlayer1Notification(out, "You can not move that far", 1);
			} else {
				previousMonster.setMovesLeft(previousMonster.getMovesLeft()-(deltaX+deltaY));
				previousMonster.setPositionByTile(clickedTile);
				for (Tile t : movableTiles) {
					BasicCommands.drawTile(out, t, 0);
				}
				previousTile.rmUnitOnTile();
				clickedTile.setUnitOnTile(previousMonster);

				BasicCommands.moveUnitToTile(out, previousMonster, clickedTile);

				BasicCommands.playUnitAnimation(out, previousMonster, UnitAnimationType.move);
			}
		}
	}

	private void enemyClick(Monster monster, GameState gameState, ActorRef out, Tile clickedTile) {
	}

	private void friendClick(Monster monster, GameState gameState, ActorRef out, Tile clickedTile) {
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

//	private void movedHighlight(ActorRef out, GameState gameState, int x, int y) {
//		Board board = gameState.gameBoard;
//		for (int i=x-2; i<x+2; i++) {
//			for (int j = y - 2; j < y + 2; j++) {
//				// make sure the tile is on the board
//				if (i >= 0 && j >= 0) {
//					Monster otherUnit = board.getGameBoard()[j][i].getUnitOnTile();
//					if (otherUnit!=null && otherUnit.getOwner()!=gameState.getTurnOwner()) {
//						BasicCommands.drawTile(out, board.getGameBoard()[j][i], 2);
//					}
//				}
//			}
//		}
//	}
//
//	private void notMovedHighlight(ActorRef out, GameState gameState, int x, int y) {
//
//		Board board = gameState.gameBoard;
//		for (int i=x-3; i<x+3; i++) {
//			for (int j=y-3; j<y+3; j++) {
//				// make sure the tile is on the board
//				if (i>=0 && j>=0) {
//					Monster otherUnit = board.getGameBoard()[j][i].getUnitOnTile();
//					if (otherUnit!=null && otherUnit.getOwner()!=gameState.getTurnOwner()) {
//						BasicCommands.drawTile(out, board.getGameBoard()[j][i], 2);
//					} else if (otherUnit!=null && otherUnit.getOwner()==gameState.getTurnOwner()) {
//					} else {
//						// 2 steps available move
//						if ((Math.abs(i-x)+Math.abs(j-y))<3 && (Math.abs(i-x)+Math.abs(j-y))!=0) {
//							BasicCommands.drawTile(out, board.getGameBoard()[j][i], 1);
//						}
//					}
//				}
//			}
//		}
//
//	}

}
