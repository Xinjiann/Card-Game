package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import structures.GameState;
import structures.basic.Monster;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import utils.CommonUtils;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile. The event
 * returns the x (horizontal) and y (vertical) indices of the tile that was clicked. Tile indices
 * start at 1.
 * <p>
 * { messageType = “tileClicked” tilex = <x index of the tile> tiley = <y index of the tile> }
 *
 * @author Dr. Richard McCreadie
 */
public class TileClicked implements EventProcessor {

  @Override
  public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

    int tilex = message.get("tilex").asInt();
    int tiley = message.get("tiley").asInt();

    Tile clickedTile = gameState.getGameBoard().getTile(tilex, tiley);
    Monster monster = clickedTile.getUnitOnTile();

    if (gameState.getUnitSelected() != null) {
      afterUnitSelectedClick(monster, gameState, out, clickedTile);
    } else if (gameState.getCardSelected() != null) {
      afterCardSelectedClick(monster, gameState, out);
    } else {
      // nothing selected before
      afterNothingClick(monster, gameState, out);
    }
  }

  private void afterNothingClick(Monster monster, GameState gameState, ActorRef out) {

    if (monster != null) {
      if (monster.getOwner() == gameState.getTurnOwner()) {
        if (!monster.isFrozen()) {
          displayAvailableTiles(gameState, out, monster);
          gameState.setUnitSelected(monster);
        } else {
          BasicCommands.addPlayer1Notification(out,
              "This unit will be capable of actions in next turn", 2);
        }
      } else {
        BasicCommands.addPlayer1Notification(out, "This unit does not belong to you", 2);
      }
    } else {
      // no unit on the tile
      BasicCommands.addPlayer1Notification(out, "Empty click", 1);
    }


  }

  private void displayAvailableTiles(GameState gameState, ActorRef out, Monster monster) {
    int x = monster.getPosition().getTilex();
    int y = monster.getPosition().getTiley();
    ArrayList<Tile> movableTiles = gameState.getGameBoard()
        .movableTiles(x, y, monster.getMovesLeft());
    ArrayList<Tile> attachableTiles = gameState.getGameBoard()
        .attachableTiles(x, y, monster.getMovesLeft());
    movableTiles.addAll(attachableTiles);
    if (movableTiles.isEmpty() || monster.isFrozen()) {
      BasicCommands.addPlayer1Notification(out,
          "No more actions allowed for this unit in this turn", 2);
    }
    for (Tile t : movableTiles) {
      if (attachableTiles.contains(t)) {
        BasicCommands.drawTile(out, t, 2);
      } else {
        BasicCommands.drawTile(out, t, 1);
      }
    }
  }

  private void afterCardSelectedClick(Monster clickedTile, GameState gameState, ActorRef out) {
  }

  private void afterUnitSelectedClick(Monster monster, GameState gameState, ActorRef out,
      Tile clickedTile) {
    Monster previousMonster = gameState.getUnitSelected();
    if (clickedTile.getUnitOnTile() != null
        && clickedTile.getUnitOnTile().getOwner() == gameState.getTurnOwner()) {
      // untarget or retarget
      friendClick(previousMonster, monster, gameState, out, clickedTile);
    } else if (clickedTile.getUnitOnTile() != null
        && clickedTile.getUnitOnTile().getOwner() != gameState.getTurnOwner()) {
      // attack
      enemyClick(monster, gameState, out, clickedTile);
    } else {
      // move
      moveClick(previousMonster, gameState, out, clickedTile);
    }
  }

  private void moveClick(Monster previousMonster, GameState gameState, ActorRef out,
      Tile clickedTile) {

    int previous_x = previousMonster.getPosition().getTilex();
    int previous_y = previousMonster.getPosition().getTiley();
    Tile previousTile = gameState.gameBoard.getTile(previous_x, previous_y);

    ArrayList<Tile> attachableTiles;
    ArrayList<Tile> movableTiles = new ArrayList<Tile>();
    // get all movable tiles
    movableTiles = gameState.getGameBoard()
        .movableTiles(previous_x, previous_y, previousMonster.getMovesLeft());
    attachableTiles = gameState.getGameBoard()
        .attachableTiles(previous_x, previous_y, previousMonster.getMovesLeft());
    movableTiles.addAll(attachableTiles);

    if ((!movableTiles.isEmpty())) {
      int deltaX = Math.abs(previous_x - clickedTile.getTilex());
      int deltaY = Math.abs(previous_y - clickedTile.getTiley());
      if ((deltaX + deltaY) > previousMonster.getMovesLeft()) {
        // outweigh the move limit of 2
        BasicCommands.addPlayer1Notification(out, "You can not move that far", 2);
      } else {
        previousMonster.setMovesLeft(previousMonster.getMovesLeft() - (deltaX + deltaY));
        previousMonster.setPositionByTile(clickedTile);
        for (Tile t : movableTiles) {
          BasicCommands.drawTile(out, t, 0);
        }
        previousTile.rmUnitOnTile();
        clickedTile.setUnitOnTile(previousMonster);
        gameState.setUnitSelected(null);
        // front end work
        BasicCommands.moveUnitToTile(out, previousMonster, clickedTile);
        // animation
        BasicCommands.playUnitAnimation(out, previousMonster, UnitAnimationType.move);
      }
    }
  }

  private void nonSpellHighlight(ActorRef out, GameState gameState, Position position){
    int x_max = gameState.gameBoard.getGameBoard()[0].length;
    int y_max = gameState.gameBoard.getGameBoard().length;
    for (int i=position.getTilex()-2; i< position.getTilex()+3; i++) {
      for (int j = position.getTiley() - 2; j < position.getTiley() + 3; j++) {
        // make sure the tile is on the board
        if (i >= 0 && i<x_max && j >= 0 && j<y_max) {

          BasicCommands.drawTile(out, gameState.gameBoard.getGameBoard()[j][i], 0);

        }

      }
    }
  }

  private void enemyClick(Monster clickedMonster, GameState gameState, ActorRef out, Tile clickedTile) {
    Monster previousMonster = gameState.getUnitSelected();
    Tile previousTile = gameState.gameBoard.getTile(previousMonster.getPosition().getTilex(), previousMonster.getPosition().getTiley());

    if (Math.abs(previousTile.getTilex()-clickedTile.getTilex()) <= previousMonster.getAttackDistance() && Math.abs(previousTile.getTiley()-clickedTile.getTiley()) <= previousMonster.getAttackDistance()) {
      attack(previousMonster, clickedMonster, gameState, out, previousTile, clickedTile);
    } else {
      moveAndAttack();
    }
    gameState.setUnitSelected(null);
  }

  private void attack(Monster attacker, Monster defender, GameState gameState, ActorRef out, Tile previousTile, Tile clickedTile) {
    // remove all highlight
    nonSpellHighlight(out, gameState, previousTile.getUnitOnTile().getPosition());
    // check coolDown
    if (attacker.isFrozen()) {
      BasicCommands.addPlayer1Notification(out, "you can not attack in this turn", 2);
      return;
    }
    // reduce attacker attack count
    attacker.attack();
    boolean survived = defender.beAttacked(attacker.getAttack());
    // play animation
    BasicCommands.playUnitAnimation(out,attacker,UnitAnimationType.attack);

    // update front end
    BasicCommands.setUnitHealth(out, defender, defender.getHealth());

    if(!survived) {
      // unit dead
      BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.death);
      CommonUtils.longlongSleep(1500);// time to play animation
      BasicCommands.deleteUnit(out, defender);

      clickedTile.rmUnitOnTile();
    } else {
      // counter-attack
      if (Math.abs(previousTile.getTilex()-clickedTile.getTilex()) > defender.getAttackDistance() && Math.abs(previousTile.getTiley()-clickedTile.getTiley()) > defender.getAttackDistance()) {
        return;
      }
      survived = attacker.beAttacked(defender.getAttack());
      // play animation
      BasicCommands.playUnitAnimation(out,defender,UnitAnimationType.attack);
      // update front end
      BasicCommands.setUnitHealth(out, attacker, attacker.getHealth());
      CommonUtils.longlongSleep(2800);
      //if die from counter-attack
      if (!survived) {
        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.death);
        CommonUtils.longlongSleep(1500);// time to play animation
        BasicCommands.deleteUnit(out, attacker);

        previousTile.rmUnitOnTile();
        gameState.setUnitSelected(null);

      } else {
        BasicCommands.playUnitAnimation(out,attacker,UnitAnimationType.idle);
      }
      BasicCommands.playUnitAnimation(out,defender,UnitAnimationType.idle);

    }
  }

  private void moveAndAttack() {
  }

  private void friendClick(Monster previousMonster, Monster clickedMonster, GameState gameState,
      ActorRef out, Tile clickedTile) {
    Tile previousTile = gameState.gameBoard.getTile(previousMonster.getPosition().getTilex(),
        previousMonster.getPosition().getTiley());
    // first remove all the highlight tiles
    rmAllActionTiles(previousMonster, gameState, out);
    if (previousTile == clickedTile) {
      // remove selected unit
      gameState.setUnitSelected(null);
    } else {
      // retarget the unit
      gameState.setUnitSelected(clickedMonster);
      CommonUtils.sleep();
      // draw highlight for new target
      displayAvailableTiles(gameState, out, clickedMonster);
    }
  }

  private void rmAllActionTiles(Monster monster, GameState gameState, ActorRef out) {
    int previous_x = monster.getPosition().getTilex();
    int previous_y = monster.getPosition().getTiley();
    ArrayList<Tile> movableTiles = gameState.getGameBoard()
        .movableTiles(previous_x, previous_y, monster.getMovesLeft());
    ArrayList<Tile> attachableTiles = gameState.getGameBoard()
        .attachableTiles(previous_x, previous_y, monster.getMovesLeft());
    movableTiles.addAll(attachableTiles);
    for (Tile t : movableTiles) {
      BasicCommands.drawTile(out, t, 0);
    }
  }

}
