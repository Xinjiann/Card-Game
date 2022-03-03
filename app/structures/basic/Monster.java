package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashSet;
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
  int maxMove;
  int attackDistance;
  int attackCount;
  int maxAttackCount;
  boolean frozen;
  boolean isAlive;
  boolean beenProvoke;
  HashSet<Monster> provokeOwners;

  public Monster() {
    super();
    this.attackDistance = 1;
    this.attackCount = 1;
    this.maxAttackCount = 1;
    this.movesLeft = 2;
    this.maxMove = 2;
    this.abilities = null;
    this.frozen = false;
    this.isAlive = true;
    this.provokeOwners = null;
    this.beenProvoke=false;
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
    if (movesLeft == 0 && attackCount == 0) {
      this.frozen = true;
    }
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
  
  public void addProvokeOwner(Monster provokeOwner) {
	this.provokeOwners.add(provokeOwner);
  }
  
  public HashSet<Monster> getProvokeOwners() {
	return provokeOwners;
  }
  
  public boolean isBeenProvoke() {
	return beenProvoke;
  }

  public void setBeenProvoke(boolean beenProvoke) {
	this.beenProvoke = beenProvoke;
  }

  public int getMaxMove() {
    return maxMove;
  }

  public void setMaxMove(int maxMove) {
    this.maxMove = maxMove;
  }

  public int getMaxAttackCount() {
    return maxAttackCount;
  }

  public void setMaxAttackCount(int maxAttackCount) {
    this.maxAttackCount = maxAttackCount;
  }
}
