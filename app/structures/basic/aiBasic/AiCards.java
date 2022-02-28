package structures.basic.aiBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import structures.basic.AiPlayer;
import structures.basic.Card;
import structures.basic.Hand;

public class AiCards {

  private AiPlayer aiPlayer;
  private Hand hand;

  public AiCards(AiPlayer aiPlayer) {
    this.aiPlayer = aiPlayer;
    this.hand = aiPlayer.getHand();
  }

  public ArrayList<AiAction> getCardsToPlay() {
    ArrayList<Card> cardList = this.allAvalibleCards();

    ArrayList<Card> bestCombo = this.bestCombo(cardList);

    //Todo add tile and card link


    return new ArrayList<AiAction>();
  }

  private ArrayList<Card> bestCombo(ArrayList<Card> cardList) {

    HashMap<ArrayList<Card>, Integer> comboMap = new HashMap<>();
    if (cardList.isEmpty()) {
      return new ArrayList<>();
    }
    int manaLeft;
    int score;
    for (int i=0; i<cardList.size(); i++) {
      ArrayList<Card> combo = new ArrayList<>();
      manaLeft = this.aiPlayer.getMana();
      if (cardList.get(i).getManacost() <= manaLeft) {
        combo.add(cardList.get(i));
        manaLeft -= cardList.get(i).getManacost();
      } else {
        continue;
      }
      for (int j=i+1; j<cardList.size(); j++) {
        if (cardList.get(j).getManacost() <= manaLeft) {
          combo.add(cardList.get(j));
          manaLeft -= cardList.get(j).getManacost();
        }
      }
      score = 9-manaLeft;
      comboMap.put(combo, score);
    }
    List<Entry<ArrayList<Card>, Integer>> list = new ArrayList<>(comboMap.entrySet());
    list.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
    return list.get(0).getKey();
  }

  private ArrayList<Card> allAvalibleCards() {
    ArrayList<Card> list = new ArrayList<>();
    for (Card card : this.hand.getHandList()) {
      if (card.getManacost() <= aiPlayer.getMana()) {
        list.add(card);
      }
    }
    return list;
  }

}
