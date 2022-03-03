package utils;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.abilities.Ability;
import structures.basic.abilities.WhenToCall;

public class CommonUtils {

  public static ArrayList<Tile> getAllUnits(GameState gameState) {
    Board board = gameState.getGameBoard();
    ArrayList<Tile> tiles = new ArrayList<>();
    for (int i = 0; i < board.getX(); i++) {
      for (int j = 0; j < board.getY(); j++) {
        if (!board.getTile(i, j).getAvailable()) {
          tiles.add(board.getTile(i, j));
        }
      }
    }
    return tiles;
  }

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
//      CommonUtils.tinySleep();
    }
  }

  public static void rmListHighlight(ActorRef out, ArrayList<Tile> area) {
    for (Tile tile : area) {
      BasicCommands.drawTile(out, tile, 0);
//      CommonUtils.tinySleep();
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

  public static void drawCardsInHand(ActorRef out, ArrayList<Card> handList) {
    // Delete all cards in the UI
    for (int i = 0; i < 6; i++) {
      BasicCommands.deleteCard(out, i);
    }

    // Show all the cards in new positions
    int i = 0;
    for(Card c : handList) {
      BasicCommands.drawCard(out, c, i, 0);
      i++;
    }
  }

  public static void executeMonsterAbility(ActorRef out, GameState gameState, WhenToCall whenToCall,
      Monster monster, Tile tile) {
    if (monster.getAbilities() != null && !monster.getAbilities().isEmpty()) {
      for (Ability ability : monster.getAbilities()) {
        if (ability.getWhenTOCall() == whenToCall) {
          ability.execute(monster, gameState, out);
          if (ability.getEffectAnimation() != null) {
            BasicCommands.playEffectAnimation(out, ability.getEffectAnimation(), tile);
          }
        }

      }
    }
  }
}
