package org.example.eiscuno.model.table;

import static org.junit.jupiter.api.Assertions.*;
import javafx.stage.Stage;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TableTest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
    }

    /**
     * Set up the test environment before each test.
     * Initializes the players, deck, table, and game.
     */

    @Test
    void addCardOnTheTableTest() {
        var humanPlayer = new Player("HUMAN_PLAYER");
        var machinePlayer = new Player("MACHINE_PLAYER");
        var deck = new Deck();
        var table = new Table();
        var gameUno = new GameUno(humanPlayer, machinePlayer, deck, table);

        /**
         *  Flag to determine if a red card has been put on the table.
         * */

        boolean isRedCardPut = false;

        /**
         * Draw cards from the deck until a red card is found.
         */

        while (!isRedCardPut) {
            var card = deck.takeCard();
            if (card.getColor().equals("RED")) {
                table.addCardOnTheTable(card);
                isRedCardPut = true;
            }
        }
        assertEquals("RED", table.getCurrentCardOnTheTable().getColor(), "The card on the table should be red");
    }
}