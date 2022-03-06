package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Truestrike implements Ability {

	private EffectAnimation effectAnimation;

	public Truestrike() {

		this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
	}

	@Override
	public void execute(Monster monster, GameState gameState, ActorRef out) {
		monster.beAttacked(2);
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
