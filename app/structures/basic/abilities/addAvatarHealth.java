package structures.basic.abilities;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class addAvatarHealth implements Ability {

    private EffectAnimation effectAnimation;

    public addAvatarHealth() {

        this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
    }

    @Override
    public EffectAnimation getEffectAnimation() {
        return this.effectAnimation;
    }

    @Override
    public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
        gameState.getHumanAvatar().setHealth(Math.min(gameState.getHumanAvatar().getHealth() + 3, 20));
        BasicCommands.setUnitHealth(out, gameState.getHumanAvatar(), gameState.getHumanAvatar().getHealth());
    }

    @Override
    public WhenToCall getWhenTOCall() {
        return WhenToCall.summon;
    }

}
