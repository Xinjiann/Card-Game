package structures.basic.aiBasic;

import java.util.ArrayList;
import java.util.HashMap;
import structures.GameState;
import structures.basic.AiPlayer;
import structures.basic.Board;
import structures.basic.Monster;
import structures.basic.Tile;

public class AiMove {

  private AiPlayer aiPlayer;

  public AiMove(AiPlayer aiPlayer) {
    this. aiPlayer = aiPlayer;
  }

  public HashMap<Tile, Monster> getMoves(Board gameBoard, GameState gameState) {
    // 1. get all own summon
    ArrayList<Monster> allMovableUnit = this.getAllMovableUnit(gameBoard);
    if(allMovableUnit.isEmpty()) {
      return new HashMap<>();
    }
    // 2. calculate movable tiles for each summon


    // 3. choose the best tile to move
    return new HashMap<>();
  }

  private ArrayList<Monster> getAllMovableUnit(Board gameBoard) {
    ArrayList <Monster> monsters = gameBoard.friendlyUnitsWithAvatar(aiPlayer);
    monsters.removeIf(m -> (m.getMovesLeft()<=0 || m.isFrozen()));
    return monsters;
  }


}
