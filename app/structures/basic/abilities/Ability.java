package structures.basic.abilities;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;

public interface Ability {

	
	public boolean execute(Monster monsterEntity, GameState gameState); 

	public Class<? extends Monster> getTargetType();
	public boolean targetEnemy(); 
	
	// Enum value used to control when an ability is called (e.g. on summon, death etc) 
	public Call_IDs getCallID(); 	
	
	// Effect animation for front end display
	public EffectAnimation getEffectAnimation();
}
