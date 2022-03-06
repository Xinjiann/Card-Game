package structures.basic.aiBasic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import structures.GameState;
import structures.basic.AiPlayer;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Hand;
import structures.basic.Tile;

public class AiCards {
    // variables
    private AiPlayer aiPlayer;
    private Hand hand;

    // Constructor
    public AiCards(AiPlayer aiPlayer) {
        this.aiPlayer = aiPlayer;
        this.hand = aiPlayer.getHand();
    }

    public HashMap<Tile, Card> getCardsToPlay(Board gameBoard, GameState gameState) {
        // get all cards can be used
        ArrayList<Card> cardList = this.allAvalibleCards();
        // get card usage strategy
        ArrayList<Card> bestCombo = this.bestCombo(cardList);
        // get specific usage actions
        HashMap<Tile, Card> cardToTile = this.cardToTile(bestCombo, gameBoard, gameState);
        return cardToTile;
    }

    private HashMap<Tile, Card> cardToTile(ArrayList<Card> bestCombo, Board gameBoard, GameState gameState) {
        HashMap<Tile, Card> map = new HashMap<>();
        ArrayList<Tile> summonableTiles = gameBoard.getSummonableTiles(aiPlayer);
        gameBoard.setSummonAreaSimple(summonableTiles);
        ArrayList<Card> spellList = new ArrayList<>();
        ArrayList<Card> monsterList = new ArrayList<>();
        for (Card c : bestCombo) {
            if (c.getType().equals("spell")) {
                spellList.add(c);
            } else {
                monsterList.add(c);
            }
        }
        // monster cards to tiles
        for (int i = 0; i < Math.min(summonableTiles.size(), monsterList.size()); i++) {
            map.put(summonableTiles.get(i), monsterList.get(i));
        }
        // spell cards to tiles
        for (Card spell : spellList) {
            Tile tile;
            switch (spell.getCardname()) {
                case "Sundrop Elixir":
                    gameBoard.setAllUnitTiles();
                    break;
                case "Truestrike":
                    gameBoard.setAllEnemyTiles(gameState);
                    break;
                case "Staff of Y'Kir'":
                    gameBoard.setAvatarArea(gameState);
                    break;
                case "Entropic Decay":
                    gameBoard.setNoneAvatarUnitArea(gameState);
                    break;
            }
            if (!gameBoard.getSpellArea().isEmpty()) {
                tile = gameBoard.getSpellArea().get(0);
                map.put(tile, spell);
            }
        }
        return map;
    }

    // get card usage strategy
    private ArrayList<Card> bestCombo(ArrayList<Card> cardList) {

        HashMap<ArrayList<Card>, Integer> comboMap = new HashMap<>();
        if (cardList.isEmpty()) {
            return new ArrayList<>();
        }
        int manaLeft;
        int score;
        for (int i = 0; i < cardList.size(); i++) {
            ArrayList<Card> combo = new ArrayList<>();
            manaLeft = this.aiPlayer.getMana();
            if (cardList.get(i).getManacost() <= manaLeft) {
                combo.add(cardList.get(i));
                manaLeft -= cardList.get(i).getManacost();
            } else {
                continue;
            }
            for (int j = i + 1; j < cardList.size(); j++) {
                if (cardList.get(j).getManacost() <= manaLeft) {
                    combo.add(cardList.get(j));
                    manaLeft -= cardList.get(j).getManacost();
                }
            }
            score = 9 - manaLeft;
            comboMap.put(combo, score);
        }
        List<Entry<ArrayList<Card>, Integer>> list = new ArrayList<>(comboMap.entrySet());
        list.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
        return list.get(0).getKey();
    }

    // get list of cards can be used
    private ArrayList<Card> allAvalibleCards() {
        ArrayList<Card> list = new ArrayList<>();
        for (Card card : this.hand.getHandList()) {
            if (card.getManacost() <= aiPlayer.getMana()) {
                list.add(card);
            }
        }
        return list;
    }

}
