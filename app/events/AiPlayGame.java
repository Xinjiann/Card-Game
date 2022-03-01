package events;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import structures.GameState;
import structures.basic.AiPlayer;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Tile;
import utils.CommonUtils;

public class AiPlayGame {

  private GameState gameState;
  private ActorRef out;

  public AiPlayGame(GameState gameState, ActorRef out) {
    this.gameState = gameState;
    this.out = out;
  }

  public void paly() {

    AiPlayer ai = gameState.getAiPlayer();
    // ai play cards
    HashMap<Tile, Card> cardCombo = ai.getCardAction(gameState.getGameBoard(), gameState);
    TileClicked tileClicked = new TileClicked();
    ArrayList<Card> handList = gameState.getTurnOwner().getHand().getHandList();

    if (!cardCombo.isEmpty()) {
      Iterator<Entry<Tile, Card>> iterator = cardCombo.entrySet().iterator();
      while(iterator.hasNext()){
        CommonUtils.sleep();
        Entry<Tile, Card> entry = iterator.next();
        if (entry.getKey() == null || entry.getValue() == null) {
          continue;
        }
        // set selected card
        for (Card card : handList) {
          if (card.getId() == entry.getValue().getId()) {
            gameState.setCardSelected(card);
            gameState.setCardPos(handList.indexOf(card));
          }
        }

        if (entry.getValue().getType().equals("spell")) {
          // play spell card
          tileClicked.playSpell(gameState, out, entry.getKey(), entry.getValue());
        } else if (entry.getValue().getType().equals("unit")) {
          // summon unit
          tileClicked.summonUnit(gameState, out, entry.getKey(), entry.getValue());
        }
      }
    }

    // get all own unit
    ArrayList<Monster> allMovableUnit = this.getAllMovableUnit();
    HashMap<Monster, ArrayList<Tile>> attachableTiles = this.getAllAttachableTiles(allMovableUnit);
//    HashMap<Monster, ArrayList<Tile>> movableTiles = this.getAllMovableTiles(allMovableUnit);


    if (!attachableTiles.isEmpty()) {
      Iterator<Entry<Monster, ArrayList<Tile>>> iterator = attachableTiles.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<Monster, ArrayList<Tile>> entry = iterator.next();
        if (this.hasUnitTarget(entry.getValue())) {
          // attack or move and attack

        } else {
          // move
        }

      }
    }
  }

  private boolean hasUnitTarget(ArrayList<Tile> list) {
    for (Tile tile : list) {
      Monster monster = tile.getUnitOnTile();
      if (monster != null && monster.getOwner() != gameState.getTurnOwner()) {
        return true;
      }
    }
    return false;
  }

  private HashMap<Monster, ArrayList<Tile>> getAllAttachableTiles(ArrayList<Monster> allMovableUnit) {
    HashMap<Monster, ArrayList<Tile>> map = new HashMap<>();
    for (Monster monster : allMovableUnit) {
      int x = monster.getPosition().getTilex();
      int y = monster.getPosition().getTiley();
      int movesLeft = monster.getMovesLeft();
      int attackDistance = monster.getAttackDistance();
      ArrayList<Tile> tileList = gameState.gameBoard.getAttachableTiles(x, y, movesLeft, attackDistance);
      map.put(monster, tileList);
    }
    return map;
  }

  private HashMap<Monster, ArrayList<Tile>> getAllMovableTiles(
      ArrayList<Monster> allMovableUnit) {
    HashMap<Monster, ArrayList<Tile>> map = new HashMap<>();
    for (Monster monster : allMovableUnit) {
      int x = monster.getPosition().getTilex();
      int y = monster.getPosition().getTiley();
      int movesLeft = monster.getMovesLeft();
      ArrayList<Tile> tileList = gameState.gameBoard.getMovableTiles(x, y, movesLeft);
      map.put(monster, tileList);
    }
    return map;
  }

  private ArrayList<Monster> getAllMovableUnit() {
    ArrayList <Monster> monsters = gameState.gameBoard.friendlyUnitsWithAvatar(gameState.getAiPlayer());
    monsters.removeIf(m -> (m.getMovesLeft()<=0 || m.isFrozen()));
    return monsters;
  }
}
