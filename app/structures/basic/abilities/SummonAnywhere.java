package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class SummonAnywhere implements Ability {

    @Override
    public void execute(Monster monster, GameState gameState, ActorRef out) {
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
        return null;
    }
}
