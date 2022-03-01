package events;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import structures.GameState;
import structures.basic.AiPlayer;
import structures.basic.Card;
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

  }
}
