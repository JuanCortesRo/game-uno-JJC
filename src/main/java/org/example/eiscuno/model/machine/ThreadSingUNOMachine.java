package org.example.eiscuno.model.machine;

import javafx.scene.control.Button;

import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadSingUNOMachine implements Runnable{
    private ArrayList<Card> cardsPlayer;
    private ArrayList<Card> cardsMachine;
    private Button unoButton;
    public boolean canSingUnoMachine;
    private boolean canSingUnoPlayer;
    private Deck deck;
    private Player humanPlayer;
    public GameUnoController callback;

    public ThreadSingUNOMachine(GameUnoController callback, ArrayList<Card> cardsMachine, Player humanPlayer, Deck deck, ArrayList<Card> cardsPlayer, Button unoButton, boolean canSingUnoMachine, boolean canSingUnoPlayer){
        this.cardsPlayer = cardsPlayer;
        this.unoButton = unoButton;
        this.canSingUnoMachine = canSingUnoMachine;
        this.canSingUnoPlayer = canSingUnoPlayer;
        this.deck = deck;
        this.humanPlayer = humanPlayer;
        this.cardsMachine = cardsMachine;
        this.callback = callback;
    }

    @Override
    public void run(){
        while (true){
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hasOneCardTheHumanPlayer();
            hasOneCardTheMachinePlayer();
        }
    }

    private void hasOneCardTheHumanPlayer(){
        if(cardsPlayer.size() == 1 && this.canSingUnoMachine){
            System.out.println("LA MAQUINA CANTA UNO AL JUGADOR, EL JUGADOR COME UNA CARTA");
            this.canSingUnoPlayer = false;
            this.humanPlayer.addCard(this.deck.takeCard());
            callback.printCardsHumanPlayer();
        }
        else if (cardsPlayer.size() != 1 && !canSingUnoMachine){
            canSingUnoMachine = true;
        }
    }

    private void hasOneCardTheMachinePlayer(){
        if(cardsMachine.size() == 1 && canSingUnoMachine){
            System.out.println("LA MAQUINA CANTA UNO PARA SI MISMA");
            callback.canSingUnoPlayer = false;
        }
        else if (cardsMachine.size() != 1 && !canSingUnoMachine){
            canSingUnoMachine = true;
        }
    }
}
