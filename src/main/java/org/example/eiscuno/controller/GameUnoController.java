package org.example.eiscuno.controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.*;

import static org.example.eiscuno.model.unoenum.EISCUnoEnum.*;

/**
 * Controller class for the Uno game.
 */
public class GameUnoController implements ThreadPlayMachine.MachinePlayCallback {
    @FXML
    public Button unoButton;
    @FXML
    private Pane gamePane;
    @FXML
    private Pane centerPane;
    @FXML
    private GridPane gridPaneCardsMachine;
    @FXML
    private GridPane gridPaneCardsPlayer;
    @FXML
    private ImageView tableImageView;
    @FXML
    private Button exitButton;
    @FXML
    private Button cardsButton;
    @FXML
    private Circle colorCircle;

    private Player humanPlayer;
    public boolean canSingUnoPlayer;
    public boolean canSingUnoMachineForPlayer;
    public boolean canSingUnoMachineForMachine;
    private Player machinePlayer;
    public boolean canSingUnoMachine;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;
    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;
    private List<ImageView> playerCardImageViews = new ArrayList<>();

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        System.out.println("- - - - - TURNO JUGADOR - - - - -");
        initVariables();
        this.gameUno.startGame();
        printCardsHumanPlayer();
        printCardsMachine();
        setupGridPane();

        threadSingUNOMachine = new ThreadSingUNOMachine(this, this.machinePlayer.getCardsPlayer(), this.humanPlayer, this.deck, this.humanPlayer.getCardsPlayer(), this.unoButton, this.canSingUnoMachine, this.canSingUnoPlayer);
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNOMachine");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.deck, this.table, this.humanPlayer, this.machinePlayer, this.tableImageView, this.gamePane, this.gameUno, this.colorCircle, this);
        threadPlayMachine.start();

        cardsButton.setOnMouseEntered(event -> nodeZoom(true,cardsButton,1.1));
        cardsButton.setOnMouseExited(event -> nodeZoom(false,cardsButton,1.1));
        exitButton.setOnMouseEntered(event -> nodeZoom(true,exitButton, 1.2));
        exitButton.setOnMouseExited(event -> nodeZoom(false,exitButton,1.2));
    }

    /**
     * Initializes the variables for the game.
     */
    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;
        this.canSingUnoPlayer = true;
        this.canSingUnoMachine = true;
    }

    /**
     * Prints the human player's cards on the grid pane.
     */
    @Override
    public void printCardsHumanPlayer() {
        Platform.runLater(() -> {
            try {
                gridPaneCardsPlayer.getChildren().clear();
                Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

                for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
                    Card card = currentVisibleCardsHumanPlayer[i];
                    ImageView cardImageView = card.getCard();

                    cardImageView.setOnMouseEntered(event -> nodeZoom(true, cardImageView, 1.2));
                    cardImageView.setOnMouseExited(event -> nodeZoom(false, cardImageView, 1.2));

                    cardImageView.setOnMouseClicked((MouseEvent event) -> {
                        if (Objects.equals(card.getValue(), "EAT4")) {
                            gameUno.eatCard(machinePlayer,4);
                            addChangeColorButtons(card, false);
                            System.out.println("- La maquina come 4 cartas y pierde turno");
                        } else if (Objects.equals(card.getValue(), "NEWCOLOR")) {
                            addChangeColorButtons(card, true);
                            System.out.println("- El color actual ha cambiado");
                        } else if (card.getValue() != null && card.getColor() != null) {
                            if (threadPlayMachine.getCurrentCard() == null || threadPlayMachine.getCurrentCard().isCompatible(card)) {
                                if (Objects.equals(card.getValue(), "EAT2")) {
                                    gameUno.eatCard(machinePlayer,2);
                                    playWithThe(card, false);
                                    System.out.println("- La maquina come 2 cartas y pierde turno");
                                } else if (Objects.equals(card.getValue(), "REVERSE")) {
                                    playWithThe(card, true);
                                    System.out.println("- El juego cambia de sentido");
                                } else if (Objects.equals(card.getValue(), "SKIP")) {
                                    playWithThe(card, false);
                                    System.out.println("- La maquina pierde turno, se vuelve a tirar");
                                } else {
                                    playWithThe(card, true);
                                    System.out.println("- Se tiró una carta común, nada pasa");
                                }
                            } else {
                                System.out.println("No puedes jugar esta carta.");
                            }
                        }
                    });
                    playerCardImageViews.add(cardImageView);
                    this.gridPaneCardsPlayer.add(cardImageView, i, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error actualizando cartas del jugador: " + e.getMessage());
            }
        });
    }


    /**
     * Disable all player card images.
     */
    private void disablePlayerCards() {
        for (ImageView imageView : playerCardImageViews) {
            imageView.setDisable(true);
            nodeZoom(true,imageView,0.95);
            applyLowContrastEffect(imageView);
        }
        cardsButton.setDisable(true);
        applyLowContrastEffect(cardsButton);
    }

    /**
     * Enable all player card images.
     */
    @Override
    public void enablePlayerCards() {
        Platform.runLater(() -> {
            synchronized (playerCardImageViews) {
                for (ImageView imageView : playerCardImageViews) {
                    imageView.setDisable(false);
                    nodeZoom(true, imageView, 1);
                    clearEffects(imageView);
                }
            }
            cardsButton.setDisable(false);
            clearEffects(cardsButton);
        });
    }

    /**
     * Applies a low contrast effect to the given node.
     * @param node The node to apply the contrast effect to.
     */
    public static void applyLowContrastEffect(Node node) {
        // Create a ColorAdjust object to adjust contrast
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(-0.5); // Use a negative value to reduce contrast

        // Apply the ColorAdjust effect to the node
        node.setEffect(colorAdjust);
    }

    /**
     * Clears any effects applied to the node.
     * @param node The node to clear the effect from.
     */
    public static void clearEffects(Node node) {
        // Remove any effects from the node
        node.setEffect(null);
    }

    /**
     * Callback method invoked when the machine (computer player) has completed its turn.
     */
    @Override
    public void onMachinePlayed() {
        Platform.runLater(this::printCardsMachine);
        nodeZoom(false,tableImageView, 1.2);
        setupGridPane();
        cardsButton.setDisable(false);
        canSingUnoPlayer = true;
        System.out.println("- - - - - TURNO JUGADOR - - - - -");
        if(machinePlayer.getCardsPlayer().size()==1){
            nodeZoom(true,unoButton,1.3);
        }
    }

    /**
     * Plays a card in the game Uno, updating various game elements and triggering actions accordingly.
     *
     * @param card The card to be played in the game.
     * @param hasPlayerPlayed Indicates whether the player has already played their turn.
     *                       If true, disables player cards and triggers animations.
     */
    private void playWithThe(Card card, boolean hasPlayerPlayed) {
        gameUno.playCard(card);
        tableImageView.setImage(card.getImage());
        nodeZoom(false, tableImageView, 1.2);
        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
        threadPlayMachine.setHasPlayerPlayed(hasPlayerPlayed);
        card.printColor();
        setupGridPane();
        printCardsMachine();
        printCardsHumanPlayer();
        threadPlayMachine.setCurrentCard();
        threadPlayMachine.changeBackgroundColor(card.getColor());
        checkIfAnyPlayerWins();
        if (hasPlayerPlayed){
            disablePlayerCards();
            playWaveTranslateAnimation(gridPaneCardsMachine, Duration.seconds(0.5), 20);
        }
        if(humanPlayer.getCardsPlayer().size()==1){
            nodeZoom(true,unoButton,1.3);
        }
    }

    private void setButtonProps(Button button, int layoutX, int layoutY, String color, int rotation){
        button.setPrefSize(80, 80);
        button.setLayoutX(layoutX);
        button.setLayoutY(layoutY);
        button.setStyle("-fx-background-radius: 5 5 60 5; -fx-background-color: "+color+";-fx-rotate: "+rotation);
    }

    /**
     * Adds color change buttons to the interface when a special Uno card (like Wild or Wild Draw Four) is played,
     * allowing the player to select a new color for the card.
     *
     * @param card The Uno card that triggered the color change option.
     * @param hasPlayerPlayed Indicates whether the player has already played their turn.
     *                       If true, certain UI elements and actions are disabled temporarily.
     */
    private void addChangeColorButtons(Card card, boolean hasPlayerPlayed) {
        disablePlayerCards();

        // Crear y configurar botones de colores
        Button[] colorButtons = new Button[]{
                createColorButton(card, hasPlayerPlayed, "RED", 49, 0, "#ff3737", 180),
                createColorButton(card, hasPlayerPlayed, "BLUE", 134, 0, "#5252fe", 270),
                createColorButton(card, hasPlayerPlayed, "YELLOW", 49, 85, "#ffbd39", 90),
                createColorButton(card, hasPlayerPlayed, "GREEN", 134, 85, "#54a954", 0)
        };

        colorButtonsAnimations(true, colorButtons);

        for (Button button : colorButtons) {
            button.setOnMouseClicked(event -> {
                card.setColor(button.getId()); // Asignar el color basado en el ID del botón
                playWithThe(card, hasPlayerPlayed);
                colorButtonsAnimations(false, colorButtons);
                if (!hasPlayerPlayed) {
                    enablePlayerCards();
                }
            });
        }

        // Agregar botones al centro
        centerPane.getChildren().addAll(colorButtons);
    }

    private Button createColorButton(Card card, boolean hasPlayerPlayed, String color, int x, int y, String hexColor, int rotation) {
        Button button = new Button();
        button.setId(color); // Usar el ID del botón para almacenar el color
        setButtonProps(button, x, y, hexColor, rotation);
        return button;
    }

    /**
     * Update and display the machine player's cards in the corresponding grid pane.
     */
    private void printCardsMachine() {
        this.gridPaneCardsMachine.getChildren().clear();
        Card[] currentVisibleCardsMachinePlayer = this.machinePlayer.getCardsPlayer().toArray(new Card[0]);
        this.gridPaneCardsMachine.setAlignment(Pos.CENTER);

        for (int i = 0; i < currentVisibleCardsMachinePlayer.length; i++) {
            Card card = currentVisibleCardsMachinePlayer[i];
            //ImageView machineCardImageView = card.getCard();
            ImageView machineCardImageView = new ImageView(new Image(String.valueOf(getClass().getResource(CARD_UNO.getFilePath()))));
            machineCardImageView.setTranslateX(-(currentVisibleCardsMachinePlayer.length/0.75));

            machineCardImageView.setFitHeight(110);
            machineCardImageView.setFitWidth(74);

            StackPane stackPane = new StackPane(machineCardImageView);
            stackPane.setAlignment(Pos.CENTER);

            this.gridPaneCardsMachine.add(stackPane, i, 0);
        }
    }

    /**
     * Configure the grid pane that displays the machine player's cards.
     * Adjusts the width of the grid pane and the width of each column to accommodate the current number of cards.
     */
    private void setupGridPane() {
        int numColumns = machinePlayer.getCardsPlayer().size();
        double gridPaneWidth = 353;
        double columnWidth = gridPaneWidth / numColumns;

        gridPaneCardsMachine.setPrefWidth(gridPaneWidth);
        gridPaneCardsMachine.setMinWidth(gridPaneWidth);
        gridPaneCardsMachine.setMaxWidth(gridPaneWidth);

        gridPaneCardsMachine.getColumnConstraints().clear();

        for (int i = 0; i < numColumns; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(columnWidth);
            column.setMinWidth(columnWidth);
            column.setMaxWidth(columnWidth);
            gridPaneCardsMachine.getColumnConstraints().add(column);
        }
    }

    /**
     * Controls the zoom animation of a node
     *
     * @param doZoom  Specifies whether to zoom in (`true`) or out (`false`).
     * @param node    The JavaFX node to apply the zoom animation to.
     * @param to      The target scale value for zooming (`1.0` represents the node's original size).
     */
    public void nodeZoom(boolean doZoom, Node node,double to){
        ScaleTransition translateIn = new ScaleTransition(Duration.seconds(0.2), node);
        translateIn.setToX(to);
        translateIn.setToY(to);
        ScaleTransition translateOut = new ScaleTransition(Duration.seconds(0.2), node);
        translateOut.setFromY(to);
        translateOut.setFromX(to);
        translateOut.setToX(1);
        translateOut.setToY(1);
        if (doZoom){
            translateIn.play();
        }else {
            translateOut.play();
        }
    }

    /**
     * Controls the animations of the colors selection for the +4 and wild card
     */
    private void colorButtonsAnimations(boolean isEnter, Button... buttons) {
        SequentialTransition sequentialTransition  = new SequentialTransition();

        for (Button button : buttons) {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.15), button);
            ScaleTransition scaleTransition1 = new ScaleTransition(Duration.seconds(0.05),button);

            if (isEnter) {
                scaleTransition.setFromX(0);
                scaleTransition.setFromY(0);
                scaleTransition.setToX(1.2);
                scaleTransition.setToY(1.2);
                scaleTransition1.setFromX(1.2);
                scaleTransition1.setFromY(1.2);
                scaleTransition1.setToX(1);
                scaleTransition1.setToY(1);
            } else {
                scaleTransition.setFromX(1);
                scaleTransition.setFromY(1);
                scaleTransition.setToX(0);
                scaleTransition.setToY(0);
                scaleTransition.setOnFinished(event -> {
                    centerPane.getChildren().remove(button);
                });
            }

            sequentialTransition.getChildren().addAll(scaleTransition, scaleTransition1);
        }

        sequentialTransition.play();
    }

    /**
     * Controls the animation of the machineGridPane when it's the Machine turn
     *
     * @param gridPane    The JavaFX GridPane containing nodes to animate.
     * @param duration    The total duration of the translation animation.
     * @param translateY  The amount to translate nodes vertically.
     */
    public static void playWaveTranslateAnimation(GridPane gridPane, Duration duration, double translateY) {
        ParallelTransition parallelTransition = new ParallelTransition();
        Duration delayBetweenAnimations = duration.divide(4);  // Ajuste del retraso para crear el solapamiento

        for (int row = 0; row < gridPane.getRowCount(); row++) {
            for (int col = 0; col < gridPane.getColumnCount(); col++) {
                Node node = getNodeFromGridPane(gridPane, col, row);
                if (node != null) {
                    SequentialTransition translateWithOverlap = createTranslateTransitionWithOverlap(node, duration, translateY);
                    translateWithOverlap.setDelay(delayBetweenAnimations.multiply(row * gridPane.getColumnCount() + col));  // Ajustar el retraso para cada nodo
                    parallelTransition.getChildren().add(translateWithOverlap);
                }
            }
        }
        parallelTransition.setCycleCount(Animation.INDEFINITE);  // Configurar la animación para que se repita indefinidamente
        parallelTransition.play();
    }

    /**
     * Creates a sequential transition that moves a node vertically with an overlap effect.
     *
     * @param node       The JavaFX node to animate.
     * @param duration   The total duration of the translation animation.
     * @param translateY The amount to translate the node vertically.
     * @return A SequentialTransition object that combines two TranslateTransition animations to create an overlap effect.
     */
    private static SequentialTransition createTranslateTransitionWithOverlap(Node node, Duration duration, double translateY) {
        TranslateTransition translateDown = new TranslateTransition(duration.divide(2), node);
        translateDown.setByY(translateY);

        TranslateTransition translateUp = new TranslateTransition(duration.divide(2), node);
        translateUp.setByY(-translateY);

        SequentialTransition translateWithOverlap = new SequentialTransition();
        translateWithOverlap.getChildren().addAll(translateDown, translateUp);

        return translateWithOverlap;
    }

    /**
     * Retrieves the node at the specified column and row from a GridPane.
     *
     * @param gridPane The GridPane from which to retrieve the node.
     * @param col      The column index of the node to retrieve.
     * @param row      The row index of the node to retrieve.
     * @return The Node at the specified column and row in the GridPane, or null if no such node exists.
     */
    private static Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    /**
     * Finds the position of a specific card in the human player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
     */
    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < this.humanPlayer.getCardsPlayer().size(); i++) {
            if (this.humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public void checkIfAnyPlayerWins(){
        Platform.runLater(() -> {
                    System.out.println(machinePlayer.getCardsPlayer().size());
                    if(humanPlayer.getCardsPlayer().size()==0){
                        threadPlayMachine.stop();
                        System.out.println("___________________________________________\n            ¡EL JUGADOR GANÓ! \n___________________________________________");
//                        showAlert("¡EL JUGADOR GANÓ!", "¡Felicidades! Has ganado el juego.");
                        Image winImage = new Image(String.valueOf(getClass().getResource(WIN_.getFilePath())));
                        ImageView winImageView = new ImageView(winImage);
                        gamePane.getChildren().add(winImageView);
                        disablePlayerCards();
                        disablePlayerCards();
                    } else if (machinePlayer.getCardsPlayer().size()==0) {
                        threadPlayMachine.stop();
                        System.out.println("___________________________________________\n            ¡LA MÁQUINA GANÓ! \n___________________________________________");
                        Image looseImage = new Image(String.valueOf(getClass().getResource(LOOSE_.getFilePath())));
                        ImageView looseImageView = new ImageView(looseImage);
                        gamePane.getChildren().add(looseImageView);
                        //showAlert("¡LA MÁQUINA GANÓ!", "Lo siento, la máquina ha ganado el juego.");
                        disablePlayerCards();
                    }
                });
    }

    @Override
    public void restoreDeckIfNeeded() {
        List<Card> cardsOnTable = table.clearTable();

        if (!cardsOnTable.isEmpty()) {
            Card lastCard = cardsOnTable.get(cardsOnTable.size() - 1);
            System.out.println("Mesa limpia. Última carta: " + lastCard.getValue() + " " + lastCard.getColor());

            Stack<Card> newDeckStack = new Stack<>();
            newDeckStack.addAll(cardsOnTable);
            Collections.shuffle(newDeckStack);

            while (!newDeckStack.isEmpty()) {
                deck.push(newDeckStack.pop());
            }

            System.out.println("Mazo restaurado con las cartas de la mesa.");

        } else {
            System.out.println("La mesa ya está vacía.");
        }
    }

    /**
     * Handles the "Back" button action to show the previous set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleBack(ActionEvent event) {
        printCardsHumanPlayer();
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the "Next" button action to show the next set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleNext(ActionEvent event) {
        printCardsHumanPlayer();
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 4) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the action of taking a card.
     *
     * @param event the action event
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        if (deck.isEmpty()) {
            restoreDeckIfNeeded();
            System.out.println("esta vacia");
        }
        else {
            System.out.println("- El jugador come una carta y pierde turno");
            this.humanPlayer.addCard(this.deck.takeCard());
            printCardsHumanPlayer();
            threadPlayMachine.setHasPlayerPlayed(true);
            disablePlayerCards();
            playWaveTranslateAnimation(gridPaneCardsMachine, Duration.seconds(0.5),20);
        }

    }

    /**
     * Handles the action of close the game.
     *
     * @param event the action event
     */
    @FXML
    void onHandleExit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Handles the action of saying "Uno".
     *
     * @param event the action event
     */
    @FXML
    void onHandleUno(ActionEvent event) {
        nodeZoom(true,unoButton,1);
        if (humanPlayer.getCardsPlayer().size() == 1 && canSingUnoPlayer){
            System.out.println("EL JUGADOR CANTA UNO PARA SI MISMO");
            canSingUnoMachineForPlayer = false;
        } else if (machinePlayer.getCardsPlayer().size() == 1 && canSingUnoPlayer) {
            System.out.println("EL JUGADOR CANTA UNO PARA LA MAQUINA, LA MAQUINA COME UNA CARTA");
            canSingUnoMachineForMachine = false;
            machinePlayer.addCard(this.deck.takeCard());
            printCardsMachine();
            setupGridPane();
        }
    }
}