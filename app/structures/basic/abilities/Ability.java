package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public interface Ability {

    // method when ability is executed
    public void execute(Monster monsterEntity, GameState gameState, ActorRef out);

    // gesture for identification of ability kinds
    public WhenToCall getWhenTOCall();

    // store the animation
    public EffectAnimation getEffectAnimation();
}
