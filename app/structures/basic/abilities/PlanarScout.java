package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class PlanarScout implements Ability{

  private EffectAnimation effectAnimation;

  public PlanarScout() {

    this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
  }

  @Override
  public void execute(Monster monster, GameState gameState) {
    monster.setMovesLeft(13);
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
