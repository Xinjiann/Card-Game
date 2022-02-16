package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import structures.basic.abilities.Ability;

public class Monster  extends Unit{

  @JsonIgnore
  protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file

  int attack;
  int health;
  Player owner;
  boolean moved;
  boolean attacked;
  ArrayList<Ability> abilities;

  public Monster() {
    super();
  }

  public Monster(int id, UnitAnimationSet animations, ImageCorrection correction) {

    // Specify id, UnitAnimationSet, ImageCorrection and/or Tile
    super(id, animations, correction);

  }

  public int getAttack() {
    return attack;
  }

  public void setAttack(int attack) {
    this.attack = attack;
  }

  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
  }

  public Player getOwner() {
    return owner;
  }

  public void setOwner(Player owner) {
    this.owner = owner;
  }

  public boolean isMoved() {
    return moved;
  }

  public void setMoved(boolean moved) {
    this.moved = moved;
  }

  public boolean isAttacked() {
    return attacked;
  }

  public void setAttacked(boolean attacked) {
    this.attacked = attacked;
  }

  public ArrayList<Ability> getAbilities() {
    return abilities;
  }

  public void setAbilities(ArrayList<Ability> abilities) {
    this.abilities = abilities;
  }
}
