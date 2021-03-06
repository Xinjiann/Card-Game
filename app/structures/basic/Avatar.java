package structures.basic;

public class Avatar extends Monster{

  public Avatar() {
    super();
    this.health = 20;
    this.attack = 2;
    this.maxHealth = 20;
  }

  @Override
  public boolean beAttacked(int d) {
    if(this.health - d <= 0) {
      this.health = 0;
      this.getOwner().setHealth(this.health);
      return false;
    } else {
      this.health -= d;
      this.getOwner().setHealth(this.health);
      return true;
    }
  }

}
