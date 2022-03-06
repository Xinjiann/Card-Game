package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;

import java.util.ArrayList;
import java.util.HashSet;

import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Card;
import structures.basic.Monster;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;
import structures.basic.abilities.Ability;
import structures.basic.abilities.WhenToCall;
import utils.BasicObjectBuilders;
import utils.CommonUtils;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile. The event returns the x (horizontal) and y (vertical) indices of
 * the tile that was clicked. Tile indices start at 1.
 * <p>
 * { messageType = “tileClicked” tilex = <x index of the tile> tiley = <y index
 * of the tile> }
 *
 * @author Dr. Richard McCreadie
 */
public class TileClicked implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        if (gameState.isLock()) {
            BasicCommands.addPlayer1Notification(out, "please wait...", 2);
            return;
        }

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
                    BasicCommands.addPlayer1Notification(out, "This unit will be capable of actions in next turn", 2);
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
        ArrayList<Tile> movableTiles = gameState.getGameBoard().getMovableTiles(x, y, monster.getMovesLeft(),
                gameState);
        ArrayList<Tile> attachableTiles = gameState.getGameBoard().getAttachableTiles(x, y, monster.getMovesLeft(),
                gameState, monster.getAttackDistance());
        if (gameState.getGameBoard().getAllAttachableAndMovableTiles().isEmpty() || monster.isFrozen()) {
            BasicCommands.addPlayer1Notification(out, "No more actions allowed for this unit in this turn", 2);
            return;
        }
        CommonUtils.drawTilesInBatch(out, movableTiles, 1);
        CommonUtils.drawTilesInBatch(out, attachableTiles, 2);
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
        // remove highlight
        CommonUtils.drawTilesInBatch(out, gameState.getGameBoard().getSpellArea(), 0);
        Monster monster = clickedTile.getUnitOnTile();
        // check ability
        if (selectedCard.getAbilityList() != null) {
            for (Ability ability : selectedCard.getAbilityList()) {
                ability.execute(monster, gameState, out);
                if (ability.getEffectAnimation() != null) {
                    BasicCommands.playEffectAnimation(out, ability.getEffectAnimation(), clickedTile);
                }
            }
        }
        // update front end
        BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
        CommonUtils.sleep();
        BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());
        // mana cost
        gameState.getTurnOwner().setMana(gameState.getTurnOwner().getMana() - manaCost);
        BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
        CommonUtils.sleep();
        BasicCommands.setPlayer2Mana(out, gameState.getAiPlayer());
        // remove hand card
        gameState.getTurnOwner().getHand().getHandList().remove(selectedCard);
        // front end rm card
        if (gameState.getTurnOwner() == gameState.getHumanPlayer()) {
            CommonUtils.drawCardsInHand(out, gameState.getTurnOwner().getHand().getHandList());
        }
        // unselect card
        gameState.setCardSelected(null);
        // update health
        BasicCommands.setUnitHealth(out, monster, monster.getHealth());
        CommonUtils.sleep();
        BasicCommands.setUnitAttack(out, monster, monster.getAttack());
        CommonUtils.sleep();
        // unit dead
        if (monster.getHealth() == 0) {
            BasicCommands.playUnitAnimation(out, monster, UnitAnimationType.death);
            CommonUtils.longlongSleep(1500);// time to play animation
            BasicCommands.deleteUnit(out, monster);
            clickedTile.rmUnitOnTile();
        }

        // check ability
        this.checkAbility(gameState, out);
    }

    private void checkAbility(GameState gameState, ActorRef out) {
        ArrayList<Tile> list = CommonUtils.getAllUnits(gameState);
        for (Tile tile : list) {
            Monster monster = tile.getUnitOnTile();
            CommonUtils.executeMonsterAbility(out, gameState, WhenToCall.castSpell, monster, tile);
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
        gameState.getTurnOwner().setMana(gameState.getTurnOwner().getMana() - manaCost);
        BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
        CommonUtils.sleep();
        BasicCommands.setPlayer2Mana(out, gameState.getAiPlayer());

        Monster monster = BasicObjectBuilders.loadMonsterUnit(selectedCard.getUnitConfigFiles(), selectedCard,
                gameState, out, Monster.class);
        monster.setPositionByTile(clickedTile);
        // check ability
        if (selectedCard.getAbilityList() != null) {
            for (Ability ability : selectedCard.getAbilityList()) {
                if (ability.getWhenTOCall() == WhenToCall.summon) {
                    ability.execute(monster, gameState, out);
                    if (ability.getEffectAnimation() != null) {
                        BasicCommands.playEffectAnimation(out, ability.getEffectAnimation(), clickedTile);
                    }
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
        // front end rm card
        if (gameState.getTurnOwner() == gameState.getHumanPlayer()) {
            CommonUtils.drawCardsInHand(out, gameState.getTurnOwner().getHand().getHandList());
        }
        CommonUtils.sleep();
        // update front end
        BasicCommands.setUnitAttack(out, monster, monster.getAttack());
        CommonUtils.sleep();
        BasicCommands.setUnitHealth(out, monster, monster.getHealth());
        CommonUtils.sleep();
        gameState.setCardSelected(null);
    }

    private void afterUnitSelectedClick(Monster monster, GameState gameState, ActorRef out, Tile clickedTile) {
        Monster previousMonster = gameState.getUnitSelected();
        if (clickedTile.getUnitOnTile() != null && clickedTile.getUnitOnTile().getOwner() == gameState.getTurnOwner()) {
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

    public void moveClick(Monster previousMonster, GameState gameState, ActorRef out, Tile clickedTile) {

        int previous_x = previousMonster.getPosition().getTilex();
        int previous_y = previousMonster.getPosition().getTiley();
        Tile previousTile = gameState.gameBoard.getTile(previous_x, previous_y);

        if ((!gameState.getGameBoard().getMovableTiles().isEmpty())) {
            int deltaX = Math.abs(previous_x - clickedTile.getTilex());
            int deltaY = Math.abs(previous_y - clickedTile.getTiley());
            if (!gameState.getGameBoard().getMovableTiles().contains(clickedTile)) {
                // outweigh the move limit of 2
                BasicCommands.addPlayer1Notification(out, "You can not move that far", 2);
            } else {
                previousMonster.setMovesLeft(previousMonster.getMovesLeft() - (deltaX + deltaY));
                previousMonster.setPositionByTile(clickedTile);
                CommonUtils.drawTilesInBatch(out, gameState.getGameBoard().getAllAttachableAndMovableTiles(), 0);
                previousTile.rmUnitOnTile();
                clickedTile.setUnitOnTile(previousMonster);
                gameState.setUnitSelected(null);
                // front end work
                BasicCommands.moveUnitToTile(out, previousMonster, clickedTile);
                // animation
                BasicCommands.playUnitAnimation(out, previousMonster, UnitAnimationType.move);
            }
        } else {
            BasicCommands.addPlayer1Notification(out, "you can not move to that tile", 2);
        }
    }

    private void enemyClick(Monster clickedMonster, GameState gameState, ActorRef out, Tile clickedTile) {
        Monster previousMonster = gameState.getUnitSelected();
        Tile previousTile = gameState.gameBoard.getTile(previousMonster.getPosition().getTilex(),
                previousMonster.getPosition().getTiley());

        if (gameState.getGameBoard().getAllAttachableAndMovableTiles().contains(clickedTile)) {
            if (Math.abs(previousTile.getTilex() - clickedTile.getTilex()) <= previousMonster.getAttackDistance()
                    && Math.abs(previousTile.getTiley() - clickedTile.getTiley()) <= previousMonster
                    .getAttackDistance()) {
                attack(previousMonster, clickedMonster, gameState, out, previousTile, clickedTile);
                gameState.setUnitSelected(null);
            } else {
                moveAndAttack(previousMonster, clickedMonster, gameState, out, previousTile, clickedTile);
            }
        } else {
            BasicCommands.addPlayer1Notification(out, "you can not attack that unit", 2);
        }

    }

    public void attack(Monster attacker, Monster defender, GameState gameState, ActorRef out, Tile previousTile,
                       Tile clickedTile) {
        // remove all highlight
        CommonUtils.rmMonsterSelectedHighlightTiles(attacker, gameState, out);
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
        CommonUtils.sleep();
        BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
        CommonUtils.sleep();
        BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());
        // play animation
        BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
        if (this.gameOver(gameState, out)) {
            gameState.setGameover(true);
        }
        // execute avatar be attacked ability
        if (defender.getClass() == Avatar.class) {
            ArrayList<Tile> list = CommonUtils.getAllUnits(gameState);
            for (Tile tile : list) {
                Monster monster = tile.getUnitOnTile();
                if (monster.getOwner() == defender.getOwner()) {
                    CommonUtils.executeMonsterAbility(out, gameState, WhenToCall.avatarBeAttacked, monster, tile);
                }
            }
        }
        if (!survived) {
            // unit dead
            BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.death);
            CommonUtils.longlongSleep(1500);// time to play animation
            BasicCommands.deleteUnit(out, defender);

            clickedTile.rmUnitOnTile();
            // re-idle
            BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.idle);

            // execute death ability
            CommonUtils.executeMonsterAbility(out, gameState, WhenToCall.death, defender, clickedTile);
        } else {
            // counter-attack
            if (Math.abs(previousTile.getTilex() - clickedTile.getTilex()) > defender.getAttackDistance()
                    && Math.abs(previousTile.getTiley() - clickedTile.getTiley()) > defender.getAttackDistance()) {
                return;
            }
            survived = attacker.beAttacked(defender.getAttack());
            // play animation
            BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.attack);
            // update front end
            BasicCommands.setUnitHealth(out, attacker, attacker.getHealth());
            CommonUtils.sleep();
            BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
            CommonUtils.sleep();
            BasicCommands.setPlayer2Health(out, gameState.getAiPlayer());

            CommonUtils.longlongSleep(2200);
            if (this.gameOver(gameState, out)) {
                gameState.setGameover(true);
            }
            // execute avatar be attacked ability
            if (attacker.getClass() == Avatar.class) {
                ArrayList<Tile> list = CommonUtils.getAllUnits(gameState);
                for (Tile tile : list) {
                    Monster monster = tile.getUnitOnTile();
                    if (monster.getOwner() == attacker.getOwner()) {
                        CommonUtils.executeMonsterAbility(out, gameState, WhenToCall.avatarBeAttacked, monster, tile);
                    }
                }
            }
            // if die from counter-attack
            if (!survived) {
                BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.death);
                CommonUtils.longlongSleep(1300);// time to play animation
                BasicCommands.deleteUnit(out, attacker);

                previousTile.rmUnitOnTile();
                gameState.setUnitSelected(null);

                // execute death ability
                CommonUtils.executeMonsterAbility(out, gameState, WhenToCall.death, attacker, previousTile);

            } else {
                BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.idle);
            }
            BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.idle);

        }
    }

    private boolean gameOver(GameState gameState, ActorRef out) {
        if (gameState.getHumanPlayer().getHealth() <= 0 || gameState.getAiPlayer().getHealth() <= 0) {
            String s = gameState.getHumanPlayer().getHealth() == 0 ? "lose" : "win";
            BasicCommands.addPlayer1Notification(out, "You " + s, 10);
            return true;
        }
        return false;
    }

    public void moveAndAttack(Monster previousMonster, Monster clickedMonster, GameState gameState, ActorRef out,
                              Tile previousTile, Tile clickedTile) {

        ArrayList<Tile> movableTiles = gameState.gameBoard.getMovableTiles(previousTile.getTilex(),
                previousTile.getTiley(), previousMonster.getMovesLeft(), gameState);
        Tile tileToGo = null;
        for (Tile tile : movableTiles) {
            HashSet<Tile> attachableTiles = gameState.gameBoard.hasMovedAttachableTiles(tile.getTilex(),
                    tile.getTiley(), gameState.getTurnOwner(), previousMonster.getAttackDistance());
            if (attachableTiles.contains(clickedTile)) {
                tileToGo = tile;
                break;
            }
        }
        if (tileToGo != null) {
            moveClick(previousMonster, gameState, out, tileToGo);
            // set waiting time according to the moving distance
            int delta = Math.abs(tileToGo.getTilex() - previousTile.getTilex())
                    + Math.abs(tileToGo.getTiley() - previousTile.getTiley());
            CommonUtils.longlongSleep(1075 * delta);
            attack(previousMonster, clickedMonster, gameState, out, tileToGo, clickedTile);
            // unselect unit
            gameState.setUnitSelected(null);
        } else {
            BasicCommands.addPlayer1Notification(out, "you can not attack that unit", 2);
        }
    }

    private void friendClick(Monster previousMonster, Monster clickedMonster, GameState gameState, ActorRef out,
                             Tile clickedTile) {
        Tile previousTile = gameState.gameBoard.getTile(previousMonster.getPosition().getTilex(),
                previousMonster.getPosition().getTiley());
        // first remove all the highlight tiles
        CommonUtils.rmMonsterSelectedHighlightTiles(previousMonster, gameState, out);
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
