package events;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import structures.GameState;
import structures.basic.AiPlayer;
import structures.basic.Avatar;
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
    /* ai play cards **/
    HashMap<Tile, Card> cardCombo = ai.getCardAction(gameState.getGameBoard(), gameState);
    TileClicked tileClicked = new TileClicked();
    ArrayList<Card> handList = gameState.getTurnOwner().getHand().getHandList();

    if (!cardCombo.isEmpty()) {
      for (Entry<Tile, Card> tileCardEntry : cardCombo.entrySet()) {
        CommonUtils.sleep();
        if (tileCardEntry.getKey() == null || tileCardEntry.getValue() == null) {
          continue;
        }
        // set selected card
        for (Card card : handList) {
          if (card.getId() == tileCardEntry.getValue().getId()) {
            gameState.setCardSelected(card);
            gameState.setCardPos(handList.indexOf(card));
          }
        }

        if (tileCardEntry.getValue().getType().equals("spell")) {
          // play spell card
          tileClicked.playSpell(gameState, out, tileCardEntry.getKey(), tileCardEntry.getValue());
        } else if (tileCardEntry.getValue().getType().equals("unit")) {
          // summon unit
          tileClicked.summonUnit(gameState, out, tileCardEntry.getKey(), tileCardEntry.getValue());
        }
      }
    }

    /* ai attack **/
    // get all own unit
    ArrayList<Monster> allAvailableUnit = this.getAllAvailableUnit();
    // get all movable only unit
    ArrayList<Monster> allMovableOnlyUnit = this.getAllMovableOnlyUnit(allAvailableUnit);

    for (Monster monster : allAvailableUnit) {
      if (!allMovableOnlyUnit.contains(monster)) {
        if (!monster.isFrozen()) {
          int x = monster.getPosition().getTilex();
          int y = monster.getPosition().getTiley();
          int movesLeft = monster.getMovesLeft();
          int attackDistance = monster.getAttackDistance();
          ArrayList<Tile> tileList = gameState.gameBoard.getAttachableTiles(x, y, movesLeft, gameState,
              attackDistance);
          // attachable area is empty, change to move only
          if (tileList.isEmpty()) {
            allMovableOnlyUnit.add(monster);
            continue;
          }
          Tile bestTarget = this.getBestAttackTarget(tileList);
          if (Math.abs(monster.getPosition().getTilex()-bestTarget.getTilex())<2 && Math.abs(monster.getPosition().getTiley()-bestTarget.getTiley())<2) {
            // attack
            tileClicked.attack(monster, bestTarget.getUnitOnTile(), gameState, out, gameState.getGameBoard()
                .getTile(monster.getPosition().getTilex(), monster.getPosition().getTiley()), bestTarget);
          } else {
            // move and attack
            tileClicked.moveAndAttack(monster, bestTarget.getUnitOnTile(), gameState, out,
                gameState.getGameBoard()
                    .getTile(monster.getPosition().getTilex(), monster.getPosition().getTiley()),
                bestTarget);
          }
        }
      }
    }
    /* ai move unit **/

    for (Monster monster : allMovableOnlyUnit) {
      // get best move target
      int x = monster.getPosition().getTilex();
      int y = monster.getPosition().getTiley();
      ArrayList<Tile> movableTiles = gameState.getGameBoard()
          .getMovableTiles(x, y, monster.getMovesLeft(), gameState);
      Tile bestTile = getBestMoveTarget(movableTiles);
      int delta = Math.abs(bestTile.getTilex() - monster.getPosition().getTilex()) + Math.abs(
          bestTile.getTiley() - monster.getPosition().getTiley());
      tileClicked.moveClick(monster, gameState, out, bestTile);
      // set waiting time according to the moving distance
      CommonUtils.longlongSleep(1075 * delta);

    }
    // Ai's turn end
    EndTurnClicked endTurnClicked = new EndTurnClicked();
    endTurnClicked.endTurn(out, gameState);
    BasicCommands.addPlayer1Notification(out, "Your turn!", 2);
  }

  private Tile getBestAttackTarget(ArrayList<Tile> tileList) {
    Tile bestTile = null;
    int highestScore = -10;
    for (Tile tile : tileList) {
      int score = 0;
      Monster monster = tile.getUnitOnTile();
      if (monster.getClass() == Avatar.class) {
        score += 10000;
      } else if (monster.getAbilities() != null && !monster.getAbilities().isEmpty()) {
        score += 1000;
      }
      score += monster.getAttack();
      score -= monster.getHealth();
      // best one so far
      if (score > highestScore) {
        bestTile = tile;
        highestScore = score;
      }
    }
    return bestTile;
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

  private ArrayList<Monster> getAllMovableOnlyUnit(
      ArrayList<Monster> allAvailableUnit) {
    ArrayList<Monster> list = new ArrayList<>();
    for (Monster m : allAvailableUnit) {
      ArrayList<Tile> attachableTiles = gameState.gameBoard.getAttachableTiles(m.getPosition().getTilex(),
          m.getPosition().getTiley(), m.getMovesLeft(), gameState, m.getAttackDistance());
      if (attachableTiles.isEmpty()) {
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
    list.sort(new Comparator<Entry<Tile, Integer>>() {
      public int compare(Entry<Tile, Integer> o1, Entry<Tile, Integer> o2) {
        return (o2.getValue() - o1.getValue());
      }
    });
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
    list.sort(new Comparator<Entry<Monster, Integer>>() {
      public int compare(Entry<Monster, Integer> o1, Entry<Monster, Integer> o2) {
        return (o1.getValue() - o2.getValue());
      }
    });
    List<Monster> list1 = list.stream().map(Entry::getKey).collect(Collectors.toList());
    return new ArrayList<>(list1);
  }

}
