package structures.basic.abilities;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public interface Ability {

	
	public void execute(Monster monsterEntity, GameState gameState);

	public WhenToCall getWhenTOCall();

	public EffectAnimation getEffectAnimation();
}
