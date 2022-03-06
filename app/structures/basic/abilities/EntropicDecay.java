package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import structures.basic.Avatar;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class EntropicDecay implements Ability {

    private EffectAnimation effectAnimation;

    public EntropicDecay() {
        this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_martyrdom);
    }

    @Override
    public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
        // Can't be execute if target is Avatar
        if (!(monsterEntity instanceof Avatar))
            monsterEntity.setHealth(0);
    }

    @Override
    public WhenToCall getWhenTOCall() {
        return WhenToCall.spell;
    }

    @Override
    public EffectAnimation getEffectAnimation() {
        return this.effectAnimation;
    }

}
