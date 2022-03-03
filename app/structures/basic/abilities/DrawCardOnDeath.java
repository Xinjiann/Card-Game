package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import commands.BasicCommands;

public class DrawCardOnDeath implements Ability {
	
	
	public DrawCardOnDeath() {
		
	}

	@Override
	public void execute(Monster monsterEntity, GameState gameState) {
		if(!monsterEntity.isAlive())
			monsterEntity.getOwner().getHand().drawCard(monsterEntity.getOwner().getDeck());
	}

	@Override
	public EffectAnimation getEffectAnimation() {
		return null;
	}

	@Override
	public WhenToCall getWhenTOCall() {
		return WhenToCall.death;
	}

}
