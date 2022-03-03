package structures.basic.abilities;

import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class DrawCardWhenSummon implements Ability{

  @Override
  public void execute(Monster monsterEntity, GameState gameState) {
    gameState.getAiPlayer().getHand().drawCard(gameState.getAiPlayer().getDeck());
    gameState.getHumanPlayer().getHand().drawCard(gameState.getHumanPlayer().getDeck());
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
