package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class Provoke implements Ability {

	public Provoke() {

	}

	@Override
	public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
		monsterEntity.setProvoke(true);
	}

	@Override
	public EffectAnimation getEffectAnimation() {
		return null;
	}

	@Override
	public WhenToCall getWhenTOCall() {
		return WhenToCall.constructor;
	}

}
