package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class SundropElixir implements Ability {
	
	private EffectAnimation effectAnimation;
	
	public SundropElixir() {
		this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
	}

	@Override
	public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
		monsterEntity.setHealth(monsterEntity.getHealth()+5);
		if(monsterEntity.getHealth()>monsterEntity.getMaxHealth())
			monsterEntity.setHealth(monsterEntity.getMaxHealth());	
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
