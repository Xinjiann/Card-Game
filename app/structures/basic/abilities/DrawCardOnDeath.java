package structures.basic.abilities;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public class DrawCardOnDeath implements Ability {
	
	
	public DrawCardOnDeath() {
		
	}

	@Override
	public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
		if(!monsterEntity.isAlive()) {
			monsterEntity.getOwner().getHand().drawCard(monsterEntity.getOwner().getDeck());
			BasicCommands.addPlayer1Notification(out, "player2 draw 1 card", 2);
		}
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
