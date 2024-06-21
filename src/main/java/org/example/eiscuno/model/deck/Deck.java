package org.example.eiscuno.model.deck;

import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.example.eiscuno.model.card.Card;

import java.util.Collections;
import java.util.Stack;

import org.example.eiscuno.model.card.CardFactory;
import org.example.eiscuno.model.card.StandardCardFactory;

/**
 * Represents a deck of Uno cards.
 */
public class Deck {
    private Stack<Card> deckOfCards;
    private CardFactory cardFactory;

    /**
     * Constructs a new deck of Uno cards and initializes it.
     */
    public Deck() {
        deckOfCards = new Stack<>();
        cardFactory = new StandardCardFactory();
        initializeDeck();
    }

    /**
     * Initializes the deck with cards based on the EISCUnoEnum values.
     */
    private void initializeDeck() {
        for (EISCUnoEnum cardEnum : EISCUnoEnum.values()) {
            if (cardEnum.name().startsWith("GREEN_") ||
                    cardEnum.name().startsWith("YELLOW_") ||
                    cardEnum.name().startsWith("BLUE_") ||
                    cardEnum.name().startsWith("RED_") ||
                    cardEnum.name().startsWith("SKIP_") ||
                    cardEnum.name().startsWith("RESERVE_") ||
                    cardEnum.name().startsWith("TWO_WILD_DRAW_") ||
                    cardEnum.name().equals("FOUR_WILD_DRAW") ||
                    cardEnum.name().equals("WILD")) {
                Card card = cardFactory.createCard(
                        cardEnum.getFilePath(),
                        getCardValue(cardEnum.name()),
                        getCardColor(cardEnum.name())
                );
                deckOfCards.push(card);
            }
        }
        Collections.shuffle(deckOfCards);
    }

    /**
     * Returns the corresponding card value, or null if no match is found.
     *
     * @param name The name string of the card.
     * @return The card value associated with the ending of the name string, or null if no match is found.
     *         Possible return values:
     *         - "0" to "9" for number cards.
     *         - "EAT2" for "TWO_WILD_DRAW" (Eat 2 cards).
     *         - "NEWCOLOR" for "WILD" (Change color cards).
     *         - "REVERSE" for "RESERVE" (Reverse cards).
     *         - "SKIP" for "SKIP" (Skip cards).
     *         - "EAT4" for "FOUR_WILD_DRAW" (Eat 4 cards).
     */
    private String getCardValue(String name) {
        if (name.endsWith("0")){
            return "0";
        } else if (name.endsWith("1")){
            return "1";
        } else if (name.endsWith("2")){
            return "2";
        } else if (name.endsWith("3")){
            return "3";
        } else if (name.endsWith("4")){
            return "4";
        } else if (name.endsWith("5")){
            return "5";
        } else if (name.endsWith("6")){
            return "6";
        } else if (name.endsWith("7")){
            return "7";
        } else if (name.endsWith("8")){
            return "8";
        } else if (name.endsWith("9")){
            return "9";
        } else if (name.endsWith("TWO_WILD_DRAW")) {
            return "EAT2";//comer 2
        } else if (name.endsWith("WILD")) {
            return "NEWCOLOR";//cambiar color
        } else if (name.endsWith("RESERVE")) {
            return "REVERSE"; //reverse card
        } else if (name.endsWith("SKIP")) {
            return "SKIP";//bloquear turno
        } else if (name.endsWith("FOUR_WILD_DRAW")) {
            return "EAT4"; //comer 4
        } else {
            return null;
        }

    }

    /**
     * Returns the corresponding color for specific starting substrings, or null if no match is found.
     *
     * @param name The name string of the card.
     * @return The card color associated with the starting substring of the name string, or null if no match is found.
     *         Possible return values:
     *         - "GREEN"
     *         - "YELLOW"
     *         - "BLUE"
     *         - "RED"
     *         - null if the name does not start with any recognized color.
     */
    private String getCardColor(String name){
        if(name.startsWith("GREEN")){
            return "GREEN";
        } else if(name.startsWith("YELLOW")){
            return "YELLOW";
        } else if(name.startsWith("BLUE")){
            return "BLUE";
        } else if(name.startsWith("RED")){
            return "RED";
        } else {
            return null;
        }
    }

    /**
     * Takes a card from the top of the deck.
     *
     * @return the card from the top of the deck
     * @throws IllegalStateException if the deck is empty
     */
    public Card takeCard() {
        if (deckOfCards.isEmpty()) {
            throw new IllegalStateException("No hay más cartas en el mazo.");
        }
        return deckOfCards.pop();
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return deckOfCards.isEmpty();
    }
}