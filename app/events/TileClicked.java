package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import java.util.ArrayList;
import java.util.HashSet;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.abilities.Ability;
import structures.basic.abilities.WhenToCall;
import utils.BasicObjectBuilders;
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
      afterCardSelectedClick(clickedTile, gameState, out);
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
        .getMovableTiles(x, y, monster.getMovesLeft());
    ArrayList<Tile> attachableTiles = gameState.getGameBoard()
        .getAttachableTiles(x, y, monster.getMovesLeft(), monster.getAttackDistance());
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

  private void afterCardSelectedClick(Tile clickedTile, GameState gameState, ActorRef out) {
    Card selectedCard = gameState.getCardSelected();
    if (selectedCard.getType().equals("spell")) {
      playSpell(gameState, out, clickedTile, selectedCard);
    } else if (selectedCard.getType().equals("unit")) {
      summonUnit(gameState, out, clickedTile, selectedCard);
    } else {
      BasicCommands.addPlayer1Notification(out, "card does not exist", 2);
      CommonUtils.rmAllHighlight(gameState, out);
      gameState.setCardSelected(null);
    }
  }

  public void playSpell(GameState gameState, ActorRef out, Tile clickedTile, Card selectedCard) {
    int manaCost = selectedCard.getManacost();
    if (!gameState.gameBoard.getSpellArea().contains(clickedTile)) {
      BasicCommands.addPlayer1Notification(out, "no target", 2);
      return;
    }
    //remove highlight
    CommonUtils.rmListHighlight(out, gameState.getGameBoard().getSpellArea());
    Monster monster = clickedTile.getUnitOnTile();
    // check ability
    if (selectedCard.getAbilityList() != null) {
      for (Ability ability : selectedCard.getAbilityList()) {
        ability.execute(monster, gameState);
        if (ability.getEffectAnimation() != null) {
          BasicCommands.playEffectAnimation(out, ability.getEffectAnimation(), clickedTile);
        }
      }
    }
    // unselect card
    gameState.setCardSelected(null);
    // update health
    BasicCommands.setUnitHealth(out, monster, monster.getHealth());
    CommonUtils.sleep();
    BasicCommands.setUnitAttack(out, monster, monster.getAttack());
    // unit dead
    if (monster.getHealth() == 0) {
      BasicCommands.playUnitAnimation(out, monster, UnitAnimationType.death);
      CommonUtils.longlongSleep(1500);// time to play animation
      BasicCommands.deleteUnit(out, monster);
      clickedTile.rmUnitOnTile();
    }
  }

  public void summonUnit(GameState gameState, ActorRef out, Tile clickedTile, Card selectedCard) {
    int manaCost = selectedCard.getManacost();
    // check if clicked tile is allowed to put unit
    if (!gameState.getGameBoard().getSummonArea().contains(clickedTile)) {
      BasicCommands.addPlayer1Notification(out, "you can not summon unit at here", 2);
      return;
    }
    // mana cost
    gameState.getTurnOwner().setMana(gameState.getTurnOwner().getMana()-manaCost);
    BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
    BasicCommands.setPlayer2Mana(out, gameState.getAiPlayer());

    Monster monster = BasicObjectBuilders.loadMonsterUnit(selectedCard.getUnitConfigFiles(), selectedCard, gameState.getTurnOwner(), Monster.class);
    monster.setPositionByTile(clickedTile);
    // check ability
    if (selectedCard.getAbilityList() != null) {
      for (Ability ability : selectedCard.getAbilityList()) {
        if (ability.getWhenTOCall() == WhenToCall.summon) {
          ability.execute(monster, gameState);
        }
        if (ability.getEffectAnimation() != null) {
          BasicCommands.playEffectAnimation(out, ability.getEffectAnimation(), clickedTile);
        }
      }
    }
    clickedTile.setUnitOnTile(monster);

    // remove all highlight tiles
    CommonUtils.rmAllHighlight(gameState, out);
    // summon unit
    BasicCommands.drawUnit(out, monster, clickedTile);
    // remove hand card
    gameState.getTurnOwner().getHand().getHandList().remove(selectedCard);
    BasicCommands.deleteCard(out, gameState.getCardPos());
    CommonUtils.sleep();
    // update front end
    BasicCommands.setUnitAttack(out, monster, monster.getAttack());
    CommonUtils.sleep();
    BasicCommands.setUnitHealth(out, monster, monster.getHealth());
    gameState.setCardSelected(null);
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
    ArrayList<Tile> movableTiles;
    // get all movable tiles
    movableTiles = gameState.getGameBoard()
        .getMovableTiles(previous_x, previous_y, previousMonster.getMovesLeft());
    attachableTiles = gameState.getGameBoard()
        .getAttachableTiles(previous_x, previous_y, previousMonster.getMovesLeft(), previousMonster.getAttackDistance());
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
      moveAndAttack(previousMonster, clickedMonster, gameState, out, previousTile, clickedTile);
    }
    gameState.setUnitSelected(null);
  }

  private void attack(Monster attacker, Monster defender, GameState gameState, ActorRef out, Tile previousTile, Tile clickedTile) {
    // remove all highlight
    nonSpellHighlight(out, gameState, attacker.getPosition());
    // check coolDown
    if (attacker.isFrozen()) {
      BasicCommands.addPlayer1Notification(out, "you can not attack in this turn", 2);
      return;
    }
    // reduce attacker attack count
    attacker.attack();
    boolean survived = defender.beAttacked(attacker.getAttack());
    // update front end
    BasicCommands.setUnitHealth(out, defender, defender.getHealth());
    // play animation
    BasicCommands.playUnitAnimation(out,attacker,UnitAnimationType.attack);


    if(!survived) {
      // unit dead
      BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.death);
      CommonUtils.longlongSleep(1500);// time to play animation
      BasicCommands.deleteUnit(out, defender);

      clickedTile.rmUnitOnTile();
      // re-idle
      BasicCommands.playUnitAnimation(out,attacker,UnitAnimationType.idle);
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
      CommonUtils.longlongSleep(2500);

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

  private void moveAndAttack(Monster previousMonster, Monster clickedMonster,
      GameState gameState, ActorRef out, Tile previousTile, Tile clickedTile) {

    ArrayList<Tile> movableTiles = gameState.gameBoard.getMovableTiles(previousTile.getTilex(), previousTile.getTiley() , previousMonster.getMovesLeft());
    Tile tileToGo = null;
    for (Tile tile : movableTiles) {
      HashSet<Tile> attachableTiles = gameState.gameBoard.hasMovedAttachableTiles(tile.getTilex(), tile.getTiley(), gameState.getTurnOwner(), previousMonster.getAttackDistance());
      if (attachableTiles.contains(clickedTile)) {
        tileToGo = tile;
        break;
      }
    }
    if (tileToGo != null) {
      moveClick(previousMonster, gameState, out, tileToGo);
      CommonUtils.longlongSleep(2200);
      attack(previousMonster, clickedMonster, gameState, out, tileToGo, clickedTile);
    }
  }

  private void friendClick(Monster previousMonster, Monster clickedMonster, GameState gameState,
      ActorRef out, Tile clickedTile) {
    Tile previousTile = gameState.gameBoard.getTile(previousMonster.getPosition().getTilex(),
        previousMonster.getPosition().getTiley());
    // first remove all the highlight tiles
    CommonUtils.rmMonsterHighlightTiles(previousMonster, gameState, out);
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

}
