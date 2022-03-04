package structures.basic.abilities;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.CommonUtils;
import utils.StaticConfFiles;

public class growWhenCastSpell implements Ability{

  private EffectAnimation effectAnimation;

  public growWhenCastSpell() {

    this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
  }

  @Override
  public EffectAnimation getEffectAnimation() {
    return this.effectAnimation;
  }

  @Override
  public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
    if (gameState.getTurnOwner() != monsterEntity.getOwner()) {
      monsterEntity.setAttack(monsterEntity.getAttack()+1);
      monsterEntity.setHealth(Math.min(monsterEntity.getHealth()+1, monsterEntity.getMaxHealth()));
      CommonUtils.tinySleep();
      BasicCommands.setUnitAttack(out, monsterEntity, monsterEntity.getAttack());
      CommonUtils.sleep();
      BasicCommands.setUnitHealth(out, monsterEntity, monsterEntity.getHealth());
    }
  }

  @Override
  public WhenToCall getWhenTOCall() {
    return WhenToCall.castSpell;
  }


}
