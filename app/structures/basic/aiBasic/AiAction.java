package structures.basic.aiBasic;

import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Tile;

public class AiAction {

  private Card card;
  private Tile targetTile;
  private Monster monster;

  public AiAction(Card card, Tile targetTile, Monster monster) {
    this.card = card;
    this.targetTile = targetTile;
    this.monster = monster;
  }

  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  public Tile getTargetTile() {
    return targetTile;
  }

  public void setTargetTile(Tile targetTile) {
    this.targetTile = targetTile;
  }

  public Monster getMonster() {
    return monster;
  }

  public void setMonster(Monster monster) {
    this.monster = monster;
  }
}
