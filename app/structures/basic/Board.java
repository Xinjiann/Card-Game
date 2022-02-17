package structures.basic;

import java.util.ArrayList;
import java.util.HashSet;
import utils.BasicObjectBuilders;

public class Board {

  private Tile [][] gameBoard;
  private final int X;
  private final int Y;

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

  public ArrayList<Tile> attachableTiles(int x, int y, int movesLeft) {
    Player player = this.getTile(x, y).getUnitOnTile().getOwner();
    ArrayList<Tile> reachableList;
    HashSet <Tile> attachableSet = new HashSet<Tile>();
    if (movesLeft == 0) {
      return new ArrayList<>(this.hasMovedAttachableTiles(x, y, player));
    }
    reachableList = this.movableTiles(x, y, movesLeft);
    for (Tile t : reachableList) {
      if (t.getUnitOnTile()!=null && t.getUnitOnTile().getOwner()!=player) {
        attachableSet.add(t);
      }
    }
    reachableList.removeIf(t -> (t.getUnitOnTile() != null));

    for(Tile t : reachableList) {
      HashSet <Tile> attRange = hasMovedAttachableTiles(t.getTilex(), t.getTiley(), player);
      attachableSet.addAll(attRange);
    }
    return new ArrayList<>(attachableSet);
  }

  public HashSet<Tile> hasMovedAttachableTiles(int x, int y, Player player) {
    HashSet<Tile> tileList = new HashSet<Tile>();
    for (int i = x - 1; i <= (x + 1); i++) {
      for (int j = y - 1; j <= (y + 1); j++) {
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
}
