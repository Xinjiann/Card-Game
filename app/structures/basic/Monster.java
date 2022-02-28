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
  int maxHealth;
  Player owner;
  ArrayList<Ability> abilities;
  int movesLeft;
  int attackDistance;
  int attackCount;
  boolean frozen;
  boolean isAlive;

  public Monster() {
    super();
    this.attackDistance = 1;
    this.attackCount = 2;
    this.movesLeft = 2;
    this.abilities = null;
    this.frozen = false;
    this.isAlive = true;
  }

  public Monster(int id, UnitAnimationSet animations, ImageCorrection correction) {
    super(id, animations, correction);
  }

  public boolean beAttacked(int attack) {
    if (this.health > attack) {
      this.health -= attack;
    } else {
      this.health = 0;
      this.isAlive = false;
    }
    return this.isAlive;
  }

  public void attack() {
    this.attackCount -= 1;
    if (this.attackCount == 0) {
      this.frozen = true;
      this.movesLeft = 0;
    }
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

  public boolean isAlive() {
    return isAlive;
  }

  public void setAlive(boolean alive) {
    isAlive = alive;
  }

  public int getAttackDistance() {
    return attackDistance;
  }

  public void setAttackDistance(int attackDistance) {
    this.attackDistance = attackDistance;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public void setMaxHealth(int maxHealth) {
    this.maxHealth = maxHealth;
  }

  public int getAttackCount() {
    return attackCount;
  }

  public void setAttackCount(int attackCount) {
    this.attackCount = attackCount;
  }

  public void addMana(int turnCount) {
  }
}
