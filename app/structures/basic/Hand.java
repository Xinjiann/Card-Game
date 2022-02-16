package structures.basic;

import java.util.ArrayList;

public class Hand {

  ArrayList<Card> handList;

  public Hand() {
    this.handList = new ArrayList<Card>();
  }

  public void initialHand(Deck deck) {

    ArrayList<Card> drawDeck = deck.getCardList();//create temporary instance of player deck

    //finds top three cards from deck
    Card cardOne = drawDeck.get(0);
    Card cardTwo = drawDeck.get(1);
    Card cardThree = drawDeck.get(2);

    //adds the cards to the Hand class's array of Cards
    handList.add(cardOne);
    handList.add(cardTwo);
    handList.add(cardThree);

    //removes three cards from deck
    deck.delCard(0);
    deck.delCard(1);
    deck.delCard(2);
  }

  public ArrayList<Card> getHandList() {
    return handList;
  }

  public void setHandList(ArrayList<Card> handList) {
    this.handList = handList;
  }
}
