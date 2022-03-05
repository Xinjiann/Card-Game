package structures.basic;

import java.util.ArrayList;
import java.util.HashSet;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.CommonUtils;

public class Board {

  private Tile [][] gameBoard;
  private final int X;
  private final int Y;
  private ArrayList<Tile> summonArea;
  private int summonDistance;
  private ArrayList<Tile> spellArea;
  private ArrayList<Tile> movableTiles;
  private ArrayList<Tile> attachableTiles;


  public Board() {
    X = 9;
    Y = 5;
    summonDistance = 1;
    gameBoard = new Tile[Y][X];
    for (int i = 0; i<Y; i++) {
      for (int k = 0; k<X; k++) {
        gameBoard[i][k] = BasicObjectBuilders.loadTile(k, i);
        gameBoard[i][k].isAvalible = true;
        gameBoard[i][k].unitOnTile = null;
      }
    }
    attachableTiles = new ArrayList<>();
    movableTiles = new ArrayList<>();
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

  public ArrayList<Tile> getMovableTiles(int x, int y, int movesLeft, GameState gameState) {
    ArrayList<Tile> movableTiles = new ArrayList<>();
    for (int i = x - movesLeft; i <= (x + movesLeft); i++) {
      for (int j = y - movesLeft; j <= (y + movesLeft); j++) {
        // Check limits
        if ( (i <= (this.X - 1) && i >= 0) && (j <= (this.Y - 1) && j >= 0)) {
          Tile tile = this.getTile(i, j);
          Monster monster = tile.getUnitOnTile();
          if (monster != null) {
            if (monster.isProvoke() && monster.getOwner() != gameState.getTurnOwner() && Math.abs(i-x)<2 && Math.abs(j-y)<2) {
              this.movableTiles = new ArrayList<>();
              return this.movableTiles;
            }
          } else {
            if ( (Math.abs(i - x) + Math.abs(j - y)) <= movesLeft) {
              movableTiles.add(tile);
            }
          }
        }
      }
    }
    this.movableTiles = movableTiles;
    return this.movableTiles;
  }

  public void setMovableTiles(ArrayList<Tile> movableTiles) {
    this.movableTiles = movableTiles;
  }

  public ArrayList<Tile> getMovableTiles() {
    return this.movableTiles;
  }

  public ArrayList<Tile> getAttachableTiles(int x, int y, int movesLeft, GameState gameState, int attackDistance) {
    Player player = this.getTile(x, y).getUnitOnTile().getOwner();
    ArrayList<Tile> reachableList;
    HashSet <Tile> attachableSet = new HashSet<Tile>();
    if (movesLeft == 0) {
      return new ArrayList<>(this.hasMovedAttachableTiles(x, y, player, attackDistance));
    }
    reachableList = this.getMovableTiles(x, y, movesLeft, gameState);
    // if provoke exist
    if (reachableList.isEmpty()) {
      ArrayList<Tile> adjTiles = CommonUtils.getAdjTiles(this.getTile(x, y).getUnitOnTile(), gameState);
      for (Tile tile : adjTiles) {
        Monster monster = tile.getUnitOnTile();
        if (monster != null && monster.isProvoke() && monster.getOwner() != gameState.getTurnOwner()) {
          this.attachableTiles = new ArrayList<>();
          this.attachableTiles.add(tile);
          return this.attachableTiles;
        }
      }
    }
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
    attachableTiles = new ArrayList<>(attachableSet);
    return attachableTiles;
  }

  public void setAttachableTiles(ArrayList<Tile> attachableTiles) {
    this.attachableTiles = attachableTiles;
  }

  public ArrayList<Tile> getAttachableTiles() {
    return this.attachableTiles;
  }

  public ArrayList<Tile> getAllAttachableAndMovableTiles() {
    movableTiles.addAll(attachableTiles);
    return movableTiles;
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

  public void setSummonArea(ArrayList<Monster> friendlyList) {
    HashSet<Tile> summonArea = new HashSet<>();
    for (Monster monster : friendlyList) {
      Position position = monster.getPosition();
      for (int i = position.getTilex()-summonDistance; i <= position.getTilex() + summonDistance; i++) {
        for (int j = position.getTiley() - summonDistance; j <= position.getTiley() + summonDistance; j++) {
          // make sure the tile is on the board
          if (i >= 0 && i< this.X && j >= 0 && j< this.Y) {
            Monster otherUnit = this.gameBoard[j][i].getUnitOnTile();
            if (otherUnit==null) {
              summonArea.add(this.gameBoard[j][i]);
            }
          }
        }
      }
    }
    this.summonArea = new ArrayList<>(summonArea);
    summonDistance = 1;
  }

  public void setSummonAreaSimple(ArrayList<Tile> area) {
    this.summonArea = area;
  }

  public ArrayList<Tile> getSummonArea() {
    return summonArea;
  }

  public ArrayList<Tile> getSpellArea() {
    return spellArea;
  }


  public void setAllUnitTiles() {
    ArrayList<Tile> allUnitTiles = new ArrayList<>();
    int x = gameBoard.length;
    int y = gameBoard[0].length;
    for (int i=0; i<x; i++) {
      for (int j = 0; j < y; j++) {
        Tile tile = gameBoard[i][j];
        Monster monster = tile.getUnitOnTile();
        if (monster != null) {
          allUnitTiles.add(tile);
        }
      }
    }
    spellArea = allUnitTiles;
  }

  public void setAllEnemyTiles(GameState gameState) {
    ArrayList<Tile> allEnemyTiles = new ArrayList<>();
    int x = gameBoard.length;
    int y = gameBoard[0].length;
    for (int i=0; i<x; i++) {
      for (int j = 0; j < y; j++) {
        Tile tile = gameBoard[i][j];
        Monster monster = tile.getUnitOnTile();
        if (monster != null && monster.getOwner() != gameState.getTurnOwner()) {
          allEnemyTiles.add(tile);
        }
      }
    }
    this.spellArea = allEnemyTiles;
  }

  public void setAvatarArea(GameState gameState) {
    ArrayList<Tile> avatarTiles = new ArrayList<>();
    int x = gameBoard.length;
    int y = gameBoard[0].length;
    for (int i=0; i<x; i++) {
      for (int j = 0; j < y; j++) {
        Tile tile = gameBoard[i][j];
        Monster monster = tile.getUnitOnTile();
        if (monster != null && monster.getClass() == Avatar.class && monster.getOwner() == gameState.getTurnOwner()) {
          avatarTiles.add(tile);
        }
      }
    }
    this.spellArea = avatarTiles;
  }

  public void setNoneAvatarUnitArea() {
    ArrayList<Tile> allUnitTiles = new ArrayList<>();
    int x = gameBoard.length;
    int y = gameBoard[0].length;
    for (int i=0; i<x; i++) {
      for (int j = 0; j < y; j++) {
        Tile tile = gameBoard[i][j];
        Monster monster = tile.getUnitOnTile();
        if (monster != null && monster.getClass() != Avatar.class) {
          allUnitTiles.add(tile);
        }
      }
    }
    spellArea = allUnitTiles;
  }

  public ArrayList<Tile> getSummonableTiles(AiPlayer aiPlayer) {
    ArrayList<Tile> summonableTiles = new ArrayList<>();
    int x = gameBoard.length;
    int y = gameBoard[0].length;
    for (int i=0; i<x; i++) {
      for (int j = 0; j < y; j++) {
        Tile tile = gameBoard[i][j];
        Monster monster = tile.getUnitOnTile();
        if (monster != null && monster.getOwner() == aiPlayer) {
          summonableTiles.addAll(this.getAdjTiles(tile));
        }
      }
    }
    return summonableTiles;
  }

  private ArrayList<Tile> getAdjTiles(Tile tile) {


    ArrayList<Tile> list = new ArrayList<Tile>();
    int xPos = tile.getTilex();
    int yPos = tile.getTiley();


    int x_max = this.X;
    int y_max = this.Y;
    for (int i=xPos-1; i< xPos+2; i++) {
      for (int j = yPos - 1; j < yPos + 2; j++) {
        // make sure the tile is on the board
        if (i >= 0 && i<x_max && j >= 0 && j<y_max) {
          Monster otherUnit = this.gameBoard[j][i].getUnitOnTile();
          if (otherUnit==null) {
            list.add(this.gameBoard[j][i]);
          }
        }
      }
    }
    return list;
  }

  public ArrayList<Monster> friendlyUnitsWithAvatar(Player player) {
    ArrayList<Monster> tiles = new ArrayList<Monster>();
    for (int i = 0; i <gameBoard.length; i++) {
      for (int k =0; k<gameBoard[0].length; k++) {
        if (gameBoard[i][k].getUnitOnTile() != null && gameBoard[i][k].getUnitOnTile().getOwner()==player) {
          tiles.add(gameBoard[i][k].getUnitOnTile());
        }
      }
    }
    return tiles;
  }

  public int getSummonDistance() {
    return summonDistance;
  }

  public void setSummonDistance(int summonDistance) {
    this.summonDistance = summonDistance;
  }

  public int getX() {
    return X;
  }

  public int getY() {
    return Y;
  }
}
