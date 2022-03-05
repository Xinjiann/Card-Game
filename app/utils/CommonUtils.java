package utils;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Position;
import structures.basic.Tile;
import java.util.HashSet;

public class CommonUtils {

  public static void rmMonsterHighlightTiles(Monster monster, GameState gameState, ActorRef out) {
    int x = monster.getPosition().getTilex();
    int y = monster.getPosition().getTiley();
    ArrayList<Tile> movableTiles = gameState.getGameBoard()
        .getMovableTiles(x, y, monster.getMovesLeft());
    ArrayList<Tile> attachableTiles = gameState.getGameBoard()
        .getAttachableTiles(x, y, monster.getMovesLeft(), monster.getAttackDistance());
    movableTiles.addAll(attachableTiles);
    for (Tile t : movableTiles) {
      BasicCommands.drawTile(out, t, 0);
    }
  }

  public static void rmAdjTiles(Monster monster, GameState gameState, ActorRef out) {
    int x = monster.getPosition().getTilex();
    int y = monster.getPosition().getTiley();
    int x_max = gameState.gameBoard.getGameBoard().length;
    int y_max = gameState.gameBoard.getGameBoard()[0].length;
    for (int i=x-1; i<=x+1; i++) {
      for (int j=y-1; j<=y+1; j++) {
        if (i >= 0 && i< x_max && j >= 0 && j<y_max) {
          Tile tile = gameState.getGameBoard().getGameBoard()[j][i];
          BasicCommands.drawTile(out, tile, 0);
          CommonUtils.tinySleep();
        }
      }
    }
  }

  public static void rmAllHighlight(GameState gameState, ActorRef out) {
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

  public static void listHighlight(ActorRef out, ArrayList<Tile> area) {
    for (Tile tile : area) {
      BasicCommands.drawTile(out, tile, 1);
    }
  }

  public static void rmListHighlight(ActorRef out, ArrayList<Tile> area) {
    for (Tile tile : area) {
      BasicCommands.drawTile(out, tile, 0);
    }
  }

  public static void tinySleep() {
    try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
  }

  public static void sleep() {
    try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
  }

  public static void longSleep() {
    try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
  }

  public static void longlongSleep(int time) {
    try {Thread.sleep(time);} catch (InterruptedException e) {e.printStackTrace();}
  }

  public static void drawCardsInHand(ActorRef out, GameState gameState, int oldSize, ArrayList<Card> handList) {
    // Delete all cards in the UI
    for (int i = 0; i < oldSize; i++) {
      BasicCommands.deleteCard(out, i);
      sleep();
    }

    // Show all the cards in new positions
    int i = 0;
    for(Card c : handList) {
      BasicCommands.drawCard(out, c, i, 0);
      i++;
      sleep();
    }
  }
  
	public static HashSet<Monster> getMonsterAround(Monster currentMonster, GameState gameState) {
		Position position = currentMonster.getPosition();
		HashSet<Monster> monsterAround=new HashSet<Monster>();
		int i = -1;
		int j = -1;
		int a = 2;
		int b = 2;
		if (position.getTilex() == 0)
			j = 0;
		if (position.getTilex() == 8)
			b = 1;
		if (position.getTiley() == 0)
			i = 0;
		if (position.getTiley() == 4)
			a = 1;
		for (; i < a; i++)
			for (; j < b; j++) {
				if (j == 0 && i == 0)
					continue;
				else {
					if (!(gameState.gameBoard.getTile(position.getTilex() + j, position.getTiley() + i)
							.getAvailable())) {
						Monster monsterOnTile = gameState.gameBoard
								.getTile(position.getTilex() + j, position.getTiley() + i).getUnitOnTile();
						if (currentMonster.getOwner() != monsterOnTile.getOwner())
							monsterAround.add(monsterOnTile);
					}
				}
			}
		return monsterAround;
	}
}
