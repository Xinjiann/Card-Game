package events;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import utils.CommonUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class AiPlayGame {

  private GameState gameState;
  private ActorRef out;

  public AiPlayGame(GameState gameState, ActorRef out) {
    this.gameState = gameState;
    this.out = out;
  }

  public void paly() {

    AiPlayer ai = gameState.getAiPlayer();
    /* ai play cards **/
    HashMap<Tile, Card> cardCombo = ai.getCardAction(gameState.getGameBoard(), gameState);
    TileClicked tileClicked = new TileClicked();
    ArrayList<Card> handList = gameState.getTurnOwner().getHand().getHandList();

    if (!cardCombo.isEmpty()) {
      // set selected card
      cardCombo.entrySet().forEach(entry -> {
        CommonUtils.sleep();
        if (entry.getKey() == null || entry.getValue() == null) {
          return;
        }
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
      });
    }

    /* ai attack **/
    // get all own unit
    ArrayList<Monster> allAvailableUnit = this.getAllAvailableUnit();
    // get all movable only unit
    ArrayList<Monster> allMovableOnlyUnit = this.getAllMovableOnlyUnit(allAvailableUnit);

    ArrayList<Monster> attachableUnitRank;
    ArrayList<Tile> attachableTargetRank;

    // get all attachable target
    HashMap<Monster, ArrayList<Tile>> attachableTiles = this.getAllAttachableTiles(allAvailableUnit,
        allMovableOnlyUnit);

    while (attachableTiles.entrySet().iterator().hasNext() && !attachableTiles.entrySet().iterator()
        .next().getValue().isEmpty()) {

      // todo optimize cal speed
      // rank the enemy unit (The higher the unit ability, the higher the priority)
      attachableTargetRank = this.rankAttachableTargetUnit(attachableTiles);
      // rank attachable unit (The lower the unit ability, the higher the priority)
      attachableUnitRank = this.rankAttachableUnit(attachableTiles);

      // attack
      Monster attacker = attachableUnitRank.get(0);
      Tile defenderTile = attachableTargetRank.get(0);
      tileClicked.moveAndAttack(attacker, defenderTile.getUnitOnTile(), gameState, out,
          gameState.getGameBoard()
              .getTile(attacker.getPosition().getTilex(), attacker.getPosition().getTiley()),
          defenderTile);
      // refresh attachableTiles
      attachableTiles = this.getAllAttachableTiles(allAvailableUnit, allMovableOnlyUnit);
    }

    /* ai move unit **/
    // add the rest attachable units to movable only list
    allMovableOnlyUnit.addAll(attachableTiles.keySet());
    // get best move target
    HashMap<Monster, Tile> moveTarget = this.getUnitMoveTarget(allMovableOnlyUnit);
    // move
    moveTarget.forEach((key, value) -> {
      tileClicked.moveClick(key, gameState, out, value);
      CommonUtils.longlongSleep(2200);
    });
    // Ai's turn end
    EndTurnClicked endTurnClicked = new EndTurnClicked();
    endTurnClicked.endTurn(out, gameState);
    BasicCommands.addPlayer1Notification(out, "Your turn!", 2);
  }

  private HashMap<Monster, Tile> getUnitMoveTarget(ArrayList<Monster> allMovableOnlyUnit) {
    HashMap<Monster, Tile> map = new HashMap<>();
    for (Monster monster : allMovableOnlyUnit) {
      int x = monster.getPosition().getTilex();
      int y = monster.getPosition().getTiley();
      ArrayList<Tile> movableTiles = gameState.getGameBoard()
          .getMovableTiles(x, y, monster.getMovesLeft());
      Tile bestTile = getBestMoveTarget(movableTiles);
      map.put(monster, bestTile);
    }
    return map;
  }

  private Tile getBestMoveTarget(ArrayList<Tile> movableTiles) {
    int x = gameState.getHumanAvatar().getPosition().getTilex();
    int y = gameState.getHumanAvatar().getPosition().getTiley();
    Tile bestTile = null;
    int delta = 10;
    for (Tile tile : movableTiles) {
      int newDelTa = Math.abs(tile.getTilex() - x) + Math.abs(tile.getTiley() - y);
      bestTile = newDelTa < delta ? tile : bestTile;
      delta = Math.min(delta, newDelTa);
    }
    return bestTile;
  }

  private ArrayList<Monster> getAllAvailableUnit() {
    ArrayList<Monster> monsters = gameState.gameBoard.friendlyUnitsWithAvatar(
        gameState.getAiPlayer());
    monsters.removeIf(m -> (m.getMovesLeft() <= 0 || m.isFrozen()));
    return monsters;
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

  private HashMap<Monster, ArrayList<Tile>> getAllAttachableTiles(
      ArrayList<Monster> allAvailableUnit, ArrayList<Monster> movableOnlyUnit) {
    HashMap<Monster, ArrayList<Tile>> map = new HashMap<>();
    for (Monster monster : allAvailableUnit) {
      // unit not in movable only list
      if (!movableOnlyUnit.contains(monster)) {
        // unit not in frozen
        if (!monster.isFrozen()) {
          int x = monster.getPosition().getTilex();
          int y = monster.getPosition().getTiley();
          int movesLeft = monster.getMovesLeft();
          int attackDistance = monster.getAttackDistance();
          ArrayList<Tile> tileList = gameState.gameBoard.getAttachableTiles(x, y, movesLeft,
              attackDistance);
          map.put(monster, tileList);
        }

      }
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

  private ArrayList<Monster> getAllMovableOnlyUnit(
      ArrayList<Monster> allAvailableUnit) {
    ArrayList<Monster> list = new ArrayList<>();
    for (Monster m : allAvailableUnit) {
      if (gameState.gameBoard.getAttachableTiles(m.getPosition().getTilex(),
          m.getPosition().getTiley(), m.getMovesLeft(), m.getAttackDistance()).isEmpty()) {
        list.add(m);
      }
    }
    return list;
  }

  private ArrayList<Tile> rankAttachableTargetUnit(
      HashMap<Monster, ArrayList<Tile>> attachableTiles) {
    HashSet<Tile> set = new HashSet<>();
    HashMap<Tile, Integer> scoreMap = new HashMap<Tile, Integer>();
    for (ArrayList<Tile> list : attachableTiles.values()) {
      set.addAll(list);
    }
    for (Tile tile : set) {
      int score = 0;
      Monster monster = tile.getUnitOnTile();
      if (monster.getClass() == Avatar.class) {
        score += 10000;
      } else if (monster.getAbilities() != null && !monster.getAbilities().isEmpty()) {
        score += 1000;
      }
      score += monster.getAttack();
      score -= monster.getHealth();
      scoreMap.put(tile, score);
    }
    List<Entry<Tile, Integer>> list = new ArrayList<Entry<Tile, Integer>>(scoreMap.entrySet());
    list.sort(Comparator.comparingInt(Entry::getValue));
    List<Tile> list1 = list.stream().map(Entry::getKey).collect(Collectors.toList());

    return new ArrayList<>(list1);
  }

  private ArrayList<Monster> rankAttachableUnit(HashMap<Monster, ArrayList<Tile>> allMovableUnit) {
    HashMap<Monster, Integer> scoreMap = new HashMap<>();
    Set<Monster> allAttachableUnit = allMovableUnit.keySet();
    for (Monster monster : allAttachableUnit) {
      int score = 0;
      if (monster.getClass() == Avatar.class) {
        score += 10000;
      } else if (monster.getAbilities() != null && !monster.getAbilities().isEmpty()) {
        score += 1000;
      }
      score += monster.getAttack();
      scoreMap.put(monster, score);
    }
    List<Entry<Monster, Integer>> list = new ArrayList<Entry<Monster, Integer>>(
        scoreMap.entrySet());
    list.sort(Comparator.comparingInt(Entry::getValue));
    List<Monster> list1 = list.stream().map(Entry::getKey).collect(Collectors.toList());
    return new ArrayList<Monster>(list1);
  }

}
