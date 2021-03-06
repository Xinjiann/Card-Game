package structures.basic;

import java.util.HashMap;
import structures.GameState;

import structures.basic.aiBasic.AiCards;

public class AiPlayer extends Player{

  public AiPlayer() {
    super();
  }

  public AiPlayer(int health, int mana) {
    super(health, mana);
  }
  
  //create aicards for this ai and return the named new map
  public HashMap<Tile, Card> getCardAction(Board gameBoard, GameState gameState) {
    AiCards aiCards = new AiCards(this);
    return aiCards.getCardsToPlay(gameBoard, gameState);
  }

}
