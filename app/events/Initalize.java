package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
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

    // initialize players' deck
    String[] deck1Cards = {
        StaticConfFiles.c_azure_herald,
        StaticConfFiles.c_azurite_lion,
        StaticConfFiles.c_comodo_charger,
        StaticConfFiles.c_fire_spitter,
        StaticConfFiles.c_hailstone_golem,
        StaticConfFiles.c_ironcliff_guardian,
        StaticConfFiles.c_pureblade_enforcer,
        StaticConfFiles.c_silverguard_knight,
        StaticConfFiles.c_sundrop_elixir,
        StaticConfFiles.c_truestrike
    };
    String[] deck2Cards = {
        StaticConfFiles.c_azure_herald,
        StaticConfFiles.c_azurite_lion,
        StaticConfFiles.c_comodo_charger,
        StaticConfFiles.c_fire_spitter,
        StaticConfFiles.c_hailstone_golem,
        StaticConfFiles.c_ironcliff_guardian,
        StaticConfFiles.c_pureblade_enforcer,
        StaticConfFiles.c_silverguard_knight,
        StaticConfFiles.c_sundrop_elixir,
        StaticConfFiles.c_truestrike
    };
    // initialize 2 players
    initializePlayers(out);
    // initialize 5*9 tiles
    initializeTiles(out);
    // players draw cards
    initializeHandCards(out, deck1Cards);
  }

  private void initializeHandCards(ActorRef out, String[] deck1Cards) {
    for (int i = 0; i < 3; i++) {
      Card card1 = BasicObjectBuilders.loadCard(deck1Cards[i], i, Card.class);
      BasicCommands.drawCard(out, card1, i + 1, 0);
    }
  }

  private void initializePlayers(ActorRef out) {
    // human
    Player humanPlayer = new Player(20, 2);
    BasicCommands.setPlayer1Health(out, humanPlayer);
    BasicCommands.setPlayer1Mana(out, humanPlayer);
    // ai
    Player aiPlayer = new Player(20, 2);
    BasicCommands.setPlayer2Health(out, aiPlayer);
    BasicCommands.setPlayer2Mana(out, aiPlayer);
  }

  private void initializeTiles(ActorRef out) {
    Tile tile;
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 9; j++) {
        tile = BasicObjectBuilders.loadTile(j, i);
        BasicCommands.drawTile(out, tile, 0);
      }
    }
  }

}


