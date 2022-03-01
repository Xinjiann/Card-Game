package structures.basic;

import java.util.HashMap;
import structures.GameState;
import structures.basic.aiBasic.AiCards;
import structures.basic.aiBasic.AiMove;

public class AiPlayer extends Player{

  public AiPlayer() {
    super();
  }

  public AiPlayer(int health, int mana) {
    super(health, mana);
  }

  public HashMap<Tile, Card> getCardAction(Board gameBoard, GameState gameState) {
    AiCards aiCards = new AiCards(this);
    return aiCards.getCardsToPlay(gameBoard, gameState);
  }

  public HashMap<Tile, Monster> getMoveAction(Board gameBoard, GameState gameState) {
    AiMove aiMove = new AiMove(this);
    return aiMove.getMoves(gameBoard, gameState);
  }
}
