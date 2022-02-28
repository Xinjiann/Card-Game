package events;

import akka.actor.ActorRef;
import java.util.ArrayList;
import org.checkerframework.checker.units.qual.A;
import structures.GameState;
import structures.basic.AiPlayer;
import structures.basic.Player;
import structures.basic.aiBasic.AiAction;
import structures.basic.aiBasic.AiCards;

public class AiPlayGame {

  private GameState gameState;
  private ActorRef out;

  public AiPlayGame(GameState gameState, ActorRef out) {
    this.gameState = gameState;
    this.out = out;
  }

  public void paly() {

    AiPlayer ai = gameState.getAiPlayer();
    ArrayList<AiAction> action = ai.getAction(gameState.getGameBoard());

  }
}
