package structures.basic.abilities;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class addAttackWhenAvatarBeAttack implements Ability{

  private EffectAnimation effectAnimation;

  public addAttackWhenAvatarBeAttack() {

    this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
  }

  @Override
  public EffectAnimation getEffectAnimation() {
    return this.effectAnimation;
  }

  @Override
  public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
    monsterEntity.setAttack(monsterEntity.getAttack()+2);
    BasicCommands.setUnitAttack(out, monsterEntity, monsterEntity.getAttack());
  }

  @Override
  public WhenToCall getWhenTOCall() {
    return WhenToCall.avatarBeAttacked;
  }

}
