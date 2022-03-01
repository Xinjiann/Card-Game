package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class StaffOfYkir implements Ability{

  private EffectAnimation effectAnimation;

  public StaffOfYkir() {

    this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
  }

  @Override
  public void execute(Monster monster, GameState gameState) {
    monster.setAttack(monster.getAttack()+2);
  }

  @Override
  public WhenToCall getWhenTOCall() {
    return null;
  }

  @Override
  public EffectAnimation getEffectAnimation() {
    return this.effectAnimation;
  }
}
