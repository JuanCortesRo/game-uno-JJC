package org.example.eiscuno.model.game;
import javafx.stage.Stage;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.testfx.framework.junit5.ApplicationTest;

class GameUnoTest extends ApplicationTest {
    @Override
    public void start(Stage stage) {
    }

    private GameUno gameUno;
    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;

    /**
     * Set up the test environment before each test.
     * Initializes the players, deck, table, and game.
     */

    @BeforeEach
    public void setUp() {
        humanPlayer = new Player("HUMAN_PLAYER");
        machinePlayer = new Player("MACHINE_PLAYER");
        deck = new Deck();
        table = new Table();
        gameUno = new GameUno(humanPlayer, machinePlayer, deck, table);
    }

    /**
     * Test that gives each player 5 cards when the game is starting.
     */

    @Test
    public void testStartGame() {
        gameUno.startGame();
        assertEquals(5, humanPlayer.getCardsPlayer().size(), "Human player should have 5 cards after game start");
        assertEquals(5, machinePlayer.getCardsPlayer().size(), "Machine player should have 5 cards after game start");
    }

    /**
     * Test that makes the eatCard method correctly adds the specified number of cards to the player's hand.
     */

    @Test
    public void testEatCard() {
        gameUno.startGame();
        int initialSize = humanPlayer.getCardsPlayer().size();
        gameUno.eatCard(humanPlayer, 2);
        assertEquals(initialSize + 2, humanPlayer.getCardsPlayer().size(), "Human player should have more cards after eating");

        initialSize = machinePlayer.getCardsPlayer().size();
        gameUno.eatCard(machinePlayer, 3);
        assertEquals(initialSize + 3, machinePlayer.getCardsPlayer().size(), "Machine player should have more cards after eating");
    }

    /**
     * Test that makes the playCard method correctly places the card on the table.
     */

    @Test
    public void testPlayCard() {
        gameUno.startGame();
        Card card = humanPlayer.getCardsPlayer().get(0); // Asumimos que el jugador tiene cartas.
        gameUno.playCard(card);
        assertEquals(card, table.getCurrentCardOnTheTable(), "The card on the table should be the played card");
    }


    /**
     * Test that makes the method getCurrentVisibleCardsHumanPlayer correctly returns the visible cards for the human player.
     */

    @Test
    public void testGetCurrentVisibleCardsHumanPlayer() {
        gameUno.startGame();
        Card[] visibleCards = gameUno.getCurrentVisibleCardsHumanPlayer(0);
        assertTrue(visibleCards.length > 0, "There should be visible cards for the human player");
    }
}