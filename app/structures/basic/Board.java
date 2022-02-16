package structures.basic;

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
        gameBoard [i][k] = BasicObjectBuilders.loadTile(k, i);
        gameBoard [i][k].isAvalible = true;
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
}
