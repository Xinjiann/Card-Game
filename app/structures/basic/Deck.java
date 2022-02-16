package structures.basic;

import java.util.ArrayList;
import java.util.List;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Deck {

  ArrayList<Card> cardList;

  public ArrayList<Card> getCardList() {
    return cardList;
  }

  public Deck() {
    this.cardList = new ArrayList<Card>();
  }

  public void humanDeck() {
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

    for (int i = 0; i<deck1Cards.length; i++) {
      Card card = BasicObjectBuilders.loadCard(deck1Cards[i], i, Card.class);
      this.cardList.add(card);
    }
  }

  public void aiDeck() {
    String[] deck2Cards = {
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
    for (int i = 0; i<deck2Cards.length; i++) {
      Card card = BasicObjectBuilders.loadCard(deck2Cards[i], i, Card.class);
      this.cardList.add(card);
    }
  }

  public void setCardList(ArrayList<Card> cardList) {
    this.cardList = cardList;
  }



  public void delCard(int i) {
    this.cardList.remove(i);
  }
}
