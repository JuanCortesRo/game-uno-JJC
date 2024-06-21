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
    private Circle colorCircle;
    private volatile boolean hasPlayerPlayed;
    private volatile Card currentCard;
    private MachinePlayCallback callback;

    public ThreadPlayMachine(Deck deck,Table table, Player humanPlayer, Player machinePlayer, ImageView tableImageView, Pane gamePane, Circle colorCircle, MachinePlayCallback callback) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.humanPlayer = humanPlayer;
        this.tableImageView = tableImageView;
        this.gamePane = gamePane;
        this.hasPlayerPlayed = false;
        this.currentCard = null;
        this.callback = callback;
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
                        System.out.println("NO HABIA NINGUNA CARTA, LA MAQUINA JUGÓ PRIMERO");
                        cardPlayed = true;
                        break;
                    } else if (Objects.equals(card.getValue(), "EAT4")) {
                        for (int j = 0; j < 4; j++) {
                            this.humanPlayer.addCard(this.deck.takeCard());
                        }
                        Platform.runLater(() -> {
                            callback.printCardsHumanPlayer();
                        });
                        System.out.println("EL ENEMIGO HACE COMER 4 AL JUGADOR");
                        String newColor = chooseRandomColor();
                        card.setColor(newColor);
                        System.out.println("LA MAQUINA CAMBIÓ DE COLOR A " + newColor);
                        playTheEnemy(card, i);
                        cardPlayed = true;
                        break;
                    } else if (Objects.equals(card.getValue(), "NEWCOLOR")) {
                        String newColor = chooseRandomColor();
                        card.setColor(newColor);
                        System.out.println("LA MAQUINA CAMBIÓ DE COLOR A " + newColor);
                        playTheEnemy(card, i);
                        cardPlayed = true;
                        break;
                    } else if ((currentCard.getValue() != null && currentCard.getColor() != null)) {
                        if (currentCard.isCompatible(card)) {
                            if (Objects.equals(card.getValue(), "EAT2")) {
                                for (int j = 0; j < 2; j++) {
                                    this.humanPlayer.addCard(this.deck.takeCard());
                                }
                                Platform.runLater(() -> {
                                    callback.printCardsHumanPlayer();
                                });
                                playTheEnemy(card, i);
                                System.out.println("EL ENEMIGO HACE COMER 2 CARTAS AL JUGADOR");
                                cardPlayed = true;
                                break;
                            } else if (Objects.equals(card.getValue(), "REVERSE")) {
                                playTheEnemy(card, i);
                                System.out.println("EN EL JUEGO CAMBIA DE SENTIDO");
                                cardPlayed = true;
                                break;
                            } else if (Objects.equals(card.getValue(), "SKIP")) {
                                playTheEnemy(card, i);
                                System.out.println("EL ENEMIGO HACE PERDER TURNO AL JUGADOR");
                                cardPlayed = false;
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                playTheEnemy(card, i);
                                System.out.println("NORMAL");
                                cardPlayed = true;
                                break;
                            }
                        }
                    }
                }

                if (!cardPlayed) {
                    System.out.println("La máquina no pudo jugar ninguna carta y tomará una del montón");
                    this.machinePlayer.addCard(this.deck.takeCard());
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
        table.addCardOnTheTable(card);
        tableImageView.setImage(card.getImage());
        machinePlayer.removeCard(i);
        currentCard = card;
        changeBackgroundColor(currentCard);
        currentCard.printColor();
        putCardOnTheTable(card);
    }

    private String chooseRandomColor() {
        String[] colors = {"RED", "GREEN", "BLUE", "YELLOW"}; // Ejemplo de colores disponibles
        Random random = new Random();
        int index = random.nextInt(colors.length);
        return colors[index];
    }

    public interface MachinePlayCallback {
        void onMachinePlayed();
        void enablePlayerCards();
        void printCardsHumanPlayer();
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




    private void putCardOnTheTable(Card card){
        table.addCardOnTheTable(card);
        tableImageView.setImage(card.getImage());
    }

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }

    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }

    public Card getCurrentCard() {
        return currentCard;
    }
}