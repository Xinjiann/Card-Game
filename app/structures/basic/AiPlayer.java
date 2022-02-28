package structures.basic;

import java.util.ArrayList;
import structures.basic.aiBasic.AiAction;
import structures.basic.aiBasic.AiCards;

public class AiPlayer extends Player{

  public AiPlayer() {
    super();
  }

  public AiPlayer(int health, int mana) {
    super(health, mana);
  }

  public ArrayList<AiAction> getAction(Board gameBoard) {
    AiCards aiCards = new AiCards(this);
    return aiCards.getCardsToPlay();
  }
}
