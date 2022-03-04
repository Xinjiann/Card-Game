package structures.basic.abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import structures.basic.Position;

public class Provoke implements Ability {
	
	
	public Provoke() {

	  }

	@Override
	public void execute(Monster monsterEntity, GameState gameState, ActorRef out) {
		Position position = monsterEntity.getPosition();
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++) {
				if (j == 0)
					continue;
				else {
					if (!gameState.gameBoard.getTile(position.getTilex() + i, position.getTiley() + j).getAvailable()) {
						Monster monsterOnTile = gameState.gameBoard
								.getTile(position.getTilex() + i, position.getTiley() + j).getUnitOnTile();
						if (monsterEntity.getOwner() != monsterOnTile.getOwner()) {
							monsterOnTile.addProvokeOwner(monsterEntity);
							monsterOnTile.setBeenProvoke(true);
						}
					}
				}
			}
	}

	@Override
	public EffectAnimation getEffectAnimation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WhenToCall getWhenTOCall() {
		// TODO Auto-generated method stub
		return null;
	}

}
