package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class SummonAnywhere implements Ability{

  private EffectAnimation effectAnimation;

  public SummonAnywhere() {

    this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
  }

  @Override
  public void execute(Monster monster, GameState gameState) {
    int x = gameState.getHumanAvatar().getPosition().getTilex();
    int limit = Math.max(gameState.getGameBoard().getX() - x, x);

    gameState.getGameBoard().setSummonDistance(limit);
  }

  @Override
  public WhenToCall getWhenTOCall() {
    return WhenToCall.cardClick;
  }

  @Override
  public EffectAnimation getEffectAnimation() {
    return this.effectAnimation;
  }
}
