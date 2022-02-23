package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import java.util.HashSet;
import structures.GameState;
import utils.BasicObjectBuilders;

public class Board {

  private Tile [][] gameBoard;
  private final int X;
  private final int Y;
  private ArrayList<Tile> summonArea;

  public Board() {
    X = 9;
    Y = 5;
    gameBoard = new Tile[Y][X];
    for (int i = 0; i<Y; i++) {
      for (int k = 0; k<X; k++) {
        gameBoard[i][k] = BasicObjectBuilders.loadTile(k, i);
        gameBoard[i][k].isAvalible = true;
        gameBoard[i][k].unitOnTile = null;
      }
    }
  }

  public Tile[][] getGameBoard() {
    return gameBoard;
  }

  public void setGameBoard(Tile[][] gameBoard) {
    this.gameBoard = gameBoard;
  }

  public Tile getTile(int x, int y) {
    return this.gameBoard[y][x];
  }

  public ArrayList<Tile> movableTiles(int x, int y, int movesLeft) {
    ArrayList<Tile> movableTiles = new ArrayList<Tile>();
    for (int i = x - movesLeft; i <= (x + movesLeft); i++) {
      for (int j = y - movesLeft; j <= (y + movesLeft); j++) {
        // Check limits
        if ( (i <= (this.X - 1) && i >= 0) && (j <= (this.Y - 1) && j >= 0)) {
          if ( (Math.abs(i - x) + Math.abs(j - y)) <= movesLeft) {
            movableTiles.add(this.getTile(i, j));
          }
        }
      }
    }
    movableTiles.removeIf(t -> t.getUnitOnTile()!=null);
    return movableTiles;
  }

  public ArrayList<Tile> attachableTiles(int x, int y, int movesLeft, int attackDistance) {
    Player player = this.getTile(x, y).getUnitOnTile().getOwner();
    ArrayList<Tile> reachableList;
    HashSet <Tile> attachableSet = new HashSet<Tile>();
    if (movesLeft == 0) {
      return new ArrayList<>(this.hasMovedAttachableTiles(x, y, player, attackDistance));
    }
    reachableList = this.movableTiles(x, y, movesLeft);
    for (Tile t : reachableList) {
      if (t.getUnitOnTile()!=null && t.getUnitOnTile().getOwner()!=player) {
        attachableSet.add(t);
      }
    }
    reachableList.removeIf(t -> (t.getUnitOnTile() != null));

    for(Tile t : reachableList) {
      HashSet <Tile> attRange = hasMovedAttachableTiles(t.getTilex(), t.getTiley(), player, attackDistance);
      attachableSet.addAll(attRange);
    }
    return new ArrayList<>(attachableSet);
  }

  public HashSet<Tile> hasMovedAttachableTiles(int x, int y, Player player, int attackDistance) {
    HashSet<Tile> tileList = new HashSet<Tile>();
    for (int i = x - attackDistance; i <= (x + attackDistance); i++) {
      for (int j = y - attackDistance; j <= (y + attackDistance); j++) {
        // Check limit
        if ( (i <= (this.X - 1) && i >= 0) && (j <= (this.Y - 1) && j >= 0)) {
          if(this.getTile(i, j).getUnitOnTile() != null && this.getTile(i, j).getUnitOnTile().getOwner() != player) {
            tileList.add(this.getTile(i, j));
          }
        }
      }
    }
    return tileList;
  }

  public void setSummonArea(ActorRef out, GameState gameState,Position position) {
    ArrayList<Tile> summonArea = new ArrayList<>();
    int x_max = this.Y;
    int y_max = this.X;
    for (int i=position.getTilex()-1; i< position.getTilex()+2; i++) {
      for (int j = position.getTiley() - 1; j < position.getTiley() + 2; j++) {
        // make sure the tile is on the board
        if (i >= 0 && i<x_max && j >= 0 && j<y_max) {
          Monster otherUnit = this.gameBoard[j][i].getUnitOnTile();
          if (otherUnit==null) {
            summonArea.add(this.gameBoard[j][i]);
          }
        }
      }
    }
    this.summonArea = summonArea;
  }

  public ArrayList<Tile> getSummonArea() {
    return summonArea;
  }

  public void setSummonArea(ArrayList<Tile> summonArea) {
    this.summonArea = summonArea;
  }
}
