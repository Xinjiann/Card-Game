package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import java.util.ArrayList;
import play.api.Play;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Monster;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.CommonUtils;
import utils.StaticConfFiles;

/**
 * Indicates that both the core game loop in the browser is starting, meaning that it is ready to
 * recieve commands from the back-end.
 * <p>
 * { messageType = “initalize” }
 *
 * @author Dr. Richard McCreadie
 */
public class Initalize implements EventProcessor {

  @Override
  public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

    gameState.gameInitalised = true;
    gameState.something = true;

    // initialize 2 players
    initializePlayers(out, gameState);
    // initialize 5*9 tiles
    initializeTiles(out, gameState);
    // initialize avatars
    initializeAvatars(out, gameState);
  }

  private void initializeAvatars(ActorRef out, GameState gameState) {
    //humanAvatar
    Avatar humanAvatar = gameState.getHumanAvatar();
    Tile tile1 = gameState.getGameBoard().getTile(1, 2);
    tile1.setUnitOnTile(humanAvatar);
    BasicCommands.drawUnit(out, humanAvatar, tile1);
    CommonUtils.sleep();// time for front end to process
    BasicCommands.setUnitAttack(out, humanAvatar, humanAvatar.getAttack());
    CommonUtils.sleep();
    BasicCommands.setUnitHealth(out, humanAvatar, humanAvatar.getHealth());
    CommonUtils.sleep();

    //aiAvatar
    Avatar aiAvatar = gameState.getAiAvatar();
    Tile tile2 = gameState.getGameBoard().getTile(7, 2);
    tile2.setUnitOnTile(aiAvatar);
    BasicCommands.drawUnit(out, aiAvatar, tile2);
    CommonUtils.sleep(); // time for front end to process
    BasicCommands.setUnitAttack(out, aiAvatar, aiAvatar.getAttack());
    CommonUtils.sleep();
    BasicCommands.setUnitHealth(out, aiAvatar, aiAvatar.getHealth());
    CommonUtils.sleep();
  }

  private void initializePlayers(ActorRef out, GameState gameState) {

    Player humanPlayer = gameState.getHumanPlayer();
    CommonUtils.sleep(); // time for front end to process
    BasicCommands.setPlayer1Health(out, humanPlayer);

    Player aiPlayer = gameState.getAiPlayer();
    CommonUtils.sleep(); // time for front end to process
    BasicCommands.setPlayer2Health(out, aiPlayer);
    CommonUtils.sleep();
    // set mana
    gameState.giveMana();
    BasicCommands.setPlayer1Mana(out, humanPlayer);
    CommonUtils.sleep();
    BasicCommands.setPlayer2Mana(out, aiPlayer);
    //draw hand cards
    for (int i=0; i<humanPlayer.getHand().getHandList().size(); i++) {
      BasicCommands.drawCard(out, humanPlayer.getHand().getHandList().get(i), i, 0);
    }
  }

  private void initializeTiles(ActorRef out, GameState gameState) {
    ArrayList<Tile> list = new ArrayList<>();
    Board board = gameState.gameBoard;
    for (int i = 0; i<board.getGameBoard().length; i++) {
      for (int k = 0; k<board.getGameBoard()[0].length; k++) {
        list.add(board.getGameBoard()[i][k]);
      }
    }
    CommonUtils.drawTilesInBatch(out, list, 0);
  }

}


