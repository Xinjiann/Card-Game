package structures.basic;

import java.util.ArrayList;
import java.util.Collections;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Deck {

  ArrayList<Card> cardList;
  String owner;

  public ArrayList<Card> getCardList() {
    return cardList;
  }

  public Deck() {
    this.cardList = new ArrayList<Card>();
  }
  
  //set human deck	
  public void humanDeck() {
    this.owner = "human";
    Card card;
    String[] deck1Cards = {
        StaticConfFiles.c_azure_herald,
        StaticConfFiles.c_azurite_lion,
        StaticConfFiles.c_comodo_charger,
        StaticConfFiles.c_fire_spitter,
        StaticConfFiles.c_hailstone_golem,
        StaticConfFiles.c_ironcliff_guardian,
        StaticConfFiles.c_pureblade_enforcer,
        StaticConfFiles.c_silverguard_knight,
        StaticConfFiles.c_sundrop_elixir,
        StaticConfFiles.c_truestrike
    };
    String[] deck1Units = {
        StaticConfFiles.u_azure_herald,
        StaticConfFiles.u_azurite_lion,
        StaticConfFiles.u_comodo_charger,
        StaticConfFiles.u_fire_spitter,
        StaticConfFiles.u_hailstone_golem,
        StaticConfFiles.u_ironcliff_guardian,
        StaticConfFiles.u_pureblade_enforcer,
        StaticConfFiles.u_silverguard_knight,

    };
    for (int i = 0; i<8; i++) {
      card = BasicObjectBuilders.loadCard(deck1Cards[i], deck1Units[i], i+2, Card.class); // 3 initial cards so index start from 2
      this.cardList.add(card);
      card = BasicObjectBuilders.loadCard(deck1Cards[i], deck1Units[i], i+2+deck1Units.length, Card.class);
      this.cardList.add(card);
    }

    for (int j = 8; j < 10; j++) {
      card = BasicObjectBuilders.loadCard(deck1Cards[j], j+2, Card.class);
      this.cardList.add(card);
      card = BasicObjectBuilders.loadCard(deck1Cards[j], j+2+deck1Units.length, Card.class);
      this.cardList.add(card);
    }

  }

  //set ai deck
  public void aiDeck() {
    this.owner = "ai";
    Card card;
    String[] deck2Cards = {
        StaticConfFiles.c_blaze_hound,
        StaticConfFiles.c_bloodshard_golem,
        StaticConfFiles.c_hailstone_golem,
        StaticConfFiles.c_planar_scout,
        StaticConfFiles.c_windshrike,
        StaticConfFiles.c_pyromancer,
        StaticConfFiles.c_rock_pulveriser,
        StaticConfFiles.c_serpenti,
        StaticConfFiles.c_staff_of_ykir,
        StaticConfFiles.c_entropic_decay,
    };
    String[] deck2Units = {
        StaticConfFiles.u_blaze_hound,
        StaticConfFiles.u_bloodshard_golem,
        StaticConfFiles.u_hailstone_golemR,
        StaticConfFiles.u_planar_scout,
        StaticConfFiles.u_windshrike,
        StaticConfFiles.u_pyromancer,
        StaticConfFiles.u_rock_pulveriser,
        StaticConfFiles.u_serpenti,
    };
    for (int i = 0; i<8; i++) {
      card = BasicObjectBuilders.loadCard(deck2Cards[i], deck2Units[i], i+2+2*deck2Cards.length, Card.class); // 3 initial cards so index start from 2
      this.cardList.add(card);
      card = BasicObjectBuilders.loadCard(deck2Cards[i], deck2Units[i], i+2+2*deck2Cards.length + deck2Units.length, Card.class);
      this.cardList.add(card);
    }

    for (int i = 8; i<10; i++) {
      card = BasicObjectBuilders.loadCard(deck2Cards[i], i + 2 + 2*deck2Cards.length, Card.class);
      this.cardList.add(card);
      card = BasicObjectBuilders.loadCard(deck2Cards[i], i + 2 + 2*deck2Cards.length + deck2Units.length,
          Card.class);
      this.cardList.add(card);
    }
  }

  public void setCardList(ArrayList<Card> cardList) {
    this.cardList = cardList;
  }

  // Shuffles deck
  public void shuffleDeck() {
    Collections.shuffle(cardList);
  }

  public void delCard(int i) {
    this.cardList.remove(i);
  }
}
