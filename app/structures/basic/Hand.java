package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import structures.GameState;

public class Hand {

  ArrayList<Card> handList;

  public Hand() {
    this.handList = new ArrayList<>();
  }

  public void initialHand(Deck deck) {

    ArrayList<Card> drawDeck = deck.getCardList();//create temporary instance of player deck

    //finds top three cards from deck
    Card cardOne = drawDeck.get(0);
    Card cardTwo = drawDeck.get(1);
    Card cardThree = drawDeck.get(2);
//    Card card4 = drawDeck.get(3);

    //adds the cards to the Hand class's array of Cards
    handList.add(cardOne);
    handList.add(cardTwo);
    handList.add(cardThree);
//    handList.add(card4);

    //removes three cards from deck
    deck.delCard(0);
    deck.delCard(0);
    deck.delCard(0);
//    deck.delCard(3);
  }

  public void drawCard(Deck deck, GameState gameState, ActorRef out) {

    // creates temporary deck and finds top card
    ArrayList<Card> deck1= deck.getCardList();
    // if deck is empty then game over
    if (deck1.isEmpty()) {
      BasicCommands.addPlayer1Notification(out, "You " + (deck.owner.equals("human") ? "lost" : "win"), 2);
      gameState.setGameover(true);
      return;
    }
    Card card= deck1.get(0);

    //checks that hand is not full
    if (this.handList.size()<6) {

      // Add card from deck to hand
      handList.add(card);

      //removes card from deck
      deck.delCard(0);
    }
    else {
      //removes card from deck
      deck.delCard(0);
    }
  }

  public ArrayList<Card> getHandList() {
    return handList;
  }

  public void setHandList(ArrayList<Card> handList) {
    this.handList = handList;
  }
}
