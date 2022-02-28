package events;

import akka.actor.ActorRef;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import structures.GameState;
import structures.basic.AiPlayer;
import structures.basic.Card;
import structures.basic.Tile;

public class AiPlayGame {

  private GameState gameState;
  private ActorRef out;

  public AiPlayGame(GameState gameState, ActorRef out) {
    this.gameState = gameState;
    this.out = out;
  }

  public void paly() {

    AiPlayer ai = gameState.getAiPlayer();
    HashMap<Tile, Card> cardCombo = ai.getCardAction(gameState.getGameBoard(), gameState);

    if (!cardCombo.isEmpty()) {
      Iterator<Entry<Tile, Card>> iterator = cardCombo.entrySet().iterator();
      while(iterator.hasNext()){
        Entry<Tile, Card> entry = iterator.next();
        if (entry.getKey() == null || entry.getValue() == null) {
          continue;
        }
        if (entry.getValue().getType().equals("spell")) {
          // todo spell
        } else if (entry.getValue().getType().equals("monster")) {
          // todo summon
        }
      }
    }

  }
}
