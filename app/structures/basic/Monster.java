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
  ArrayList<Ability> abilities;
  int movesLeft;
  boolean frozen;

  public Monster() {
    super();
    this.movesLeft = 2;
    this.abilities = null;
    this.frozen = false;
  }

  public Monster(int id, UnitAnimationSet animations, ImageCorrection correction) {
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


  public ArrayList<Ability> getAbilities() {
    return abilities;
  }

  public void setAbilities(ArrayList<Ability> abilities) {
    this.abilities = abilities;
  }


  public int getMovesLeft() {
    return movesLeft;
  }

  public void setMovesLeft(int movesLeft) {
    this.movesLeft = movesLeft;
  }

  public boolean isFrozen() {
    return frozen;
  }

  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }
}
