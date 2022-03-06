package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class AttackTwice implements Ability {

    public AttackTwice() {
    }

    @Override
    public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
        monsterEntity.setAttackCount(2);
        monsterEntity.setMaxAttackCount(2);
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
