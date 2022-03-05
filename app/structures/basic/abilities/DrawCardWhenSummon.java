package structures.basic.abilities;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.CommonUtils;

public class DrawCardWhenSummon implements Ability{

  @Override
  public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
    gameState.getAiPlayer().getHand().drawCard(gameState.getAiPlayer().getDeck(), gameState, out);
    gameState.getHumanPlayer().getHand().drawCard(gameState.getHumanPlayer().getDeck(), gameState, out);
    BasicCommands.addPlayer1Notification(out, "Both player draw a card", 2);
    CommonUtils.drawCardsInHand(out, gameState.getHumanPlayer().getHand().getHandList());
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
