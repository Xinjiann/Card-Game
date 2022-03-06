package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import structures.basic.abilities.Ability;

public class Monster extends Unit {
    // variables
    @JsonIgnore
    protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java
    // objects from a file

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
    boolean provoke;

    // constructor 1
    public Monster() {
        super();
        this.attackDistance = 1;
        this.attackCount = 0;
        this.maxAttackCount = 1;
        this.movesLeft = 0;
        this.maxMove = 2;
        this.abilities = null;
        this.frozen = true;
        this.isAlive = true;
    }

    // constructor 2
    public Monster(int id, UnitAnimationSet animations, ImageCorrection correction) {
        super(id, animations, correction);
    }

    // methods deal with been attack
    public boolean beAttacked(int attack) {
        if (this.health > attack) {
            this.health -= attack;
        } else {
            this.health = 0;
            this.isAlive = false;
        }
        return this.isAlive;
    }

    // methods deal with attack
    public void attack() {
        this.attackCount -= 1;
        // freeze the monster according to the rule
        if (this.attackCount == 0) {
            this.frozen = true;
            this.movesLeft = 0;
        }
    }

    // getters and setters
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
        // freeze the monster according to the rule
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

    public boolean isProvoke() {
        return provoke;
    }

    public void setProvoke(boolean provoke) {
        this.provoke = provoke;
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
