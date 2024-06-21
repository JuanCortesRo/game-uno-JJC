package org.example.eiscuno.model.machine;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.controller.GameUnoController;

import javax.net.ssl.SSLContext;
import java.util.Objects;
import java.util.Random;

import static org.example.eiscuno.model.unoenum.EISCUnoEnum.CARD_UNO;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Deck deck;
    private Player machinePlayer;
    private Player humanPlayer;
    private ImageView tableImageView;
    private Pane gamePane;
    private GameUno gameUno;
    private Circle colorCircle;
    private volatile boolean hasPlayerPlayed;
    private volatile Card currentCard;
    private MachinePlayCallback callback;

    public ThreadPlayMachine(Deck deck,Table table, Player humanPlayer, Player machinePlayer, ImageView tableImageView, Pane gamePane, GameUno gameUno, Circle colorCircle, MachinePlayCallback callback) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.humanPlayer = humanPlayer;
        this.tableImageView = tableImageView;
        this.gamePane = gamePane;
        this.hasPlayerPlayed = false;
        this.currentCard = null;
        this.callback = callback;
        this.gameUno = gameUno;
        this.colorCircle = colorCircle;
        this.deck = deck;
    }

    public void run() {
        while (true) {
            if (hasPlayerPlayed) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("- - - - - TURNO MAQUINA - - - - -");

                boolean cardPlayed = false;
                for (int i = 0; i < machinePlayer.getCardsPlayer().size(); i++) {
                    Card card = machinePlayer.getCardsPlayer().get(i);

                    if (this.currentCard == null && card.getColor() != null) {
                        playTheEnemy(card, i);
                        System.out.println("- No habia ninguna carta inicialmente, la maquina comienza el juego");
                        cardPlayed = true;
                        break;
                    } else if (Objects.equals(card.getValue(), "EAT4")) {
                        gameUno.eatCard(humanPlayer,4);
                        Platform.runLater(() -> {
                            callback.printCardsHumanPlayer();
                        });
                        System.out.println("- El jugador come 4 cartas y pierde turno, la maquina vuelve a tirar");
                        String newColor = chooseRandomColor();
                        card.setColor(newColor);
                        playTheEnemy(card, i);
                        System.out.println("- El color actual ha cambiado, el nuevo color es " + newColor);
                        cardPlayed = false;
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (Objects.equals(card.getValue(), "NEWCOLOR")) {
                        String newColor = chooseRandomColor();
                        card.setColor(newColor);
                        System.out.println("- El color actual ha cambiado, el nuevo color es " + newColor);
                        playTheEnemy(card, i);
                        cardPlayed = true;
                        break;
                    } else if ((currentCard.getValue() != null && currentCard.getColor() != null)) {
                        if (currentCard.isCompatible(card)) {
                            if (Objects.equals(card.getValue(), "EAT2")) {
                                gameUno.eatCard(humanPlayer,2);
                                Platform.runLater(() -> {
                                    callback.printCardsHumanPlayer();
                                });
                                playTheEnemy(card, i);
                                System.out.println("- El jugador come 2 cartas y pierde turno, la maquina vuelve a tirar");
                                cardPlayed = false;
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else if (Objects.equals(card.getValue(), "REVERSE")) {
                                playTheEnemy(card, i);
                                System.out.println("- El juego cambia de sentido");
                                cardPlayed = true;
                                break;
                            } else if (Objects.equals(card.getValue(), "SKIP")) {
                                playTheEnemy(card, i);
                                System.out.println("- El enemigo hace que el jugador pierda turno, volverá a tirar");
                                cardPlayed = false;
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                playTheEnemy(card, i);
                                System.out.println("- Se tiró una carta común, nada pasa");
                                cardPlayed = true;
                                break;
                            }
                        }
                    }
                }

                if (!cardPlayed) {
                    if (deck.isEmpty()){
                        callback.restoreDeckIfNeeded();
                        System.out.println("esta vacia");
                    } else {
                        System.out.println("La máquina no pudo jugar ninguna carta y comerá una carta de la baraja");
                        this.machinePlayer.addCard(this.deck.takeCard());
                    }
                }

                Platform.runLater(() -> {
                    callback.enablePlayerCards();
                });

                hasPlayerPlayed = false;

                if (callback != null) {
                    callback.onMachinePlayed();
                }
            }
        }
    }


    private void playTheEnemy(Card card, int i){
        gameUno.playCard(card);
        tableImageView.setImage(card.getImage());
        machinePlayer.removeCard(i);
        currentCard = card;
        changeBackgroundColor(currentCard);
        currentCard.printColor();
        tableImageView.setImage(card.getImage());
        callback.checkIfAnyPlayerWins();
    }

    private String chooseRandomColor() {
        String[] colors = {"RED", "GREEN", "BLUE", "YELLOW"};
        Random random = new Random();
        int index = random.nextInt(colors.length);
        return colors[index];
    }

    public interface MachinePlayCallback {
        void onMachinePlayed();
        void enablePlayerCards();
        void printCardsHumanPlayer();
        void restoreDeckIfNeeded();
        void checkIfAnyPlayerWins();
    }

    public void changeBackgroundColor(Card currentCard) {
        ScaleTransition circleZoom = new ScaleTransition(Duration.seconds(0.4), colorCircle);
        circleZoom.setFromY(0);
        circleZoom.setFromX(0);
        circleZoom.setToY(40);
        circleZoom.setToX(40);

        String cardColor;
        if(currentCard.getColor()==null){
            cardColor = "BLACK";
        } else {
            cardColor = currentCard.getColor();
        }
        switch (cardColor) {
            case "GREEN":
                colorCircle.setStyle("-fx-fill: #54a954");
                break;
            case "BLUE":
                colorCircle.setStyle("-fx-fill: #5252fe");
                break;
            case "RED":
                colorCircle.setStyle("-fx-fill: #ff3737");
                break;
            case "YELLOW":
                colorCircle.setStyle("-fx-fill: #ffbd39");
                break;
            case "BLACK":
                colorCircle.setStyle("-fx-fill: BLACK");
                break;
        }
        circleZoom.play();

        circleZoom.setOnFinished(event -> {
            //System.out.println("Animation finished. Changing background color.");
            switch (cardColor) {
                case "GREEN":
                    gamePane.setStyle("-fx-background-color: #54a954");
                    break;
                case "BLUE":
                    gamePane.setStyle("-fx-background-color: #5252fe");
                    break;
                case "RED":
                    gamePane.setStyle("-fx-background-color: #ff3737");
                    break;
                case "YELLOW":
                    gamePane.setStyle("-fx-background-color: #ffbd39");
                    break;
                case "BLACK":
                    gamePane.setStyle("-fx-background-color: BLACK");
                    break;
            }
        });
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }

    public void setCurrentCard() {
        this.currentCard = table.getCurrentCardOnTheTable();
    }

    public Card getCurrentCard() {
        return currentCard;
    }
}