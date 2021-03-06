package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class Flying implements Ability {

  @Override
  public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
    int movesLeft =
        Math.max(gameState.getGameBoard().getX() - monsterEntity.getPosition().getTilex(),
            monsterEntity.getPosition().getTilex()) + Math.max(
            gameState.getGameBoard().getY() - monsterEntity.getPosition().getTiley(),
            monsterEntity.getPosition().getTiley()) - 1;
    monsterEntity.setMovesLeft(movesLeft);
  }

  @Override
  public WhenToCall getWhenTOCall() {
    return WhenToCall.constructor;
  }

  @Override
  public EffectAnimation getEffectAnimation() {
    return null;
  }
}
