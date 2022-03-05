package structures.basic.abilities;

import java.util.HashSet;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Board;
import utils.CommonUtils;

public class Provoke implements Ability {
	
	
	public Provoke() {

	  }

	@Override
	public void execute(Monster monsterEntity, GameState gameState) {
		HashSet<Monster> monsterOnTiles = CommonUtils.getMonsterAround(monsterEntity, gameState);
		if (monsterOnTiles.isEmpty()) {
			for (Monster monsterOnTile : monsterOnTiles) {
				if (monsterEntity.getHealth() > 0) {
					monsterOnTile.setBeenProvoke(true);
					monsterOnTile.addProvokeOwners(monsterEntity);
				} else {
					monsterOnTile.delProvokeOwners(monsterEntity);
					if(monsterOnTile.getProvokeOwners().isEmpty()) {
						monsterOnTile.setBeenProvoke(false);}
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
		return WhenToCall.aura;
	}

}
