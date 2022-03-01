package structures.basic.abilities;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Monster;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class Pyromancer implements Ability{

  private EffectAnimation effectAnimation;

  public Pyromancer() {

    this.effectAnimation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
  }

  @Override
  public void execute(Monster monster, GameState gameState) {
//    monster.setAttackDistance(13);
//    Tile[][] allTile =  gameState.getGameBoard().getGameBoard();
//    for (int i = 0; i < allTile.length; i++) {
//      Tile[] tiles = allTile[i];
//      for (int j = 0; j < tiles.length; j++) {
//        Tile tile = tiles[j];
//        if (tile.getUnitOnTile() != null&& tile.getUnitOnTile().getOwner() != gameState.getTurnOwner()) {
//          BasicCommands.drawTile(out, tile, 2);
//        }
//      }
//    }
  }

  @Override
  public WhenToCall getWhenTOCall() {
    return null;
  }

  @Override
  public EffectAnimation getEffectAnimation() {
    return this.effectAnimation;
  }
}
