package structures.basic.abilities;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public interface Ability {

	
	public void execute(Monster monsterEntity, GameState gameState, ActorRef out);

//	public Class<? extends Monster> getTargetType();
//	public boolean targetEnemy();
//
//	// Enum value used to control when an ability is called (e.g. on summon, death etc)
//	public Call_IDs getCallID();
//
//	// Effect animation for front end display
	public EffectAnimation getEffectAnimation();
}
