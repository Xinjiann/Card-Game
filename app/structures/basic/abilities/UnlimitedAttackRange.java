package structures.basic.abilities;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class UnlimitedAttackRange implements Ability{

  private EffectAnimation effectAnimation;

  public UnlimitedAttackRange() {

    this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
  }

  @Override
  public void execute(Monster monster, GameState gameState) {
    int x = monster.getPosition().getTilex();
    int attackDistance = Math.max(gameState.getGameBoard().getX() - x, x);
    monster.setAttackDistance(attackDistance);
  }

  @Override
  public WhenToCall getWhenTOCall() {
    return WhenToCall.summon;
  }

  @Override
  public EffectAnimation getEffectAnimation() {
    return this.effectAnimation;
  }
}
