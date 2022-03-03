package structures.basic.abilities;

import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class Flying implements Ability {

  @Override
  public void execute(Monster monsterEntity, GameState gameState) {
    int movesLeft =
        Math.max(gameState.getGameBoard().getX() - monsterEntity.getPosition().getTilex(),
            monsterEntity.getPosition().getTilex()) + Math.max(
            gameState.getGameBoard().getY() - monsterEntity.getPosition().getTiley(),
            monsterEntity.getPosition().getTiley()) - 1;
    monsterEntity.setMovesLeft(movesLeft);
  }

  @Override
  public WhenToCall getWhenTOCall() {
    return WhenToCall.summon;
  }

  @Override
  public EffectAnimation getEffectAnimation() {
    return null;
  }
}
