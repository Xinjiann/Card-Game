package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class addAvatarHealth implements Ability{

  private EffectAnimation effectAnimation;

  public addAvatarHealth() {

    this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
  }

  @Override
  public EffectAnimation getEffectAnimation() {
    return this.effectAnimation;
  }

  @Override
  public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
    gameState.getTurnOwner().setHealth(Math.min(gameState.getTurnOwner().getHealth()+3, 20));
  }

  @Override
  public WhenToCall getWhenTOCall() {
    return WhenToCall.summon;
  }

}
