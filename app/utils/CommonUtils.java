package utils;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import structures.GameState;
import structures.basic.Monster;
import structures.basic.Tile;

public class CommonUtils {

  public static void rmMonsterHighlightTiles(Monster monster, GameState gameState, ActorRef out) {
    int x = monster.getPosition().getTilex();
    int y = monster.getPosition().getTiley();
    ArrayList<Tile> movableTiles = gameState.getGameBoard()
        .movableTiles(x, y, monster.getMovesLeft());
    ArrayList<Tile> attachableTiles = gameState.getGameBoard()
        .attachableTiles(x, y, monster.getMovesLeft(), monster.getAttackDistance());
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

  public static void tinySleep() {
    try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
  }

  public static void sleep() {
    try {Thread.sleep(30);} catch (InterruptedException e) {e.printStackTrace();}
  }

  public static void longSleep() {

  }

  public static void longlongSleep(int time) {
    try {Thread.sleep(time);} catch (InterruptedException e) {e.printStackTrace();}
  }

}
