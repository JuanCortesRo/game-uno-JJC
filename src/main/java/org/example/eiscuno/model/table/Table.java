package org.example.eiscuno.model.table;

import org.example.eiscuno.model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the table in the Uno game where cards are played.
 */
public class Table {
    private ArrayList<Card> cardsTable;

    /**
     * Constructs a new Table object with no cards on it.
     */
    public Table(){
        this.cardsTable = new ArrayList<Card>();
    }

    /**
     * Adds a card to the table.
     *
     * @param card The card to be added to the table.
     */
    public void addCardOnTheTable(Card card){
        this.cardsTable.add(card);
    }

    /**
     * Retrieves the current card on the table.
     *
     * @return The card currently on the table.
     * @throws IndexOutOfBoundsException if there are no cards on the table.
     */
    public Card getCurrentCardOnTheTable() throws IndexOutOfBoundsException {
        if (cardsTable.isEmpty()) {
            throw new IndexOutOfBoundsException("There are no cards on the table.");
        }
        return this.cardsTable.get(this.cardsTable.size()-1);
    }

    public List<Card> clearTable() {
        List<Card> cardsOnTable = new ArrayList<>(this.cardsTable);

        if (!cardsOnTable.isEmpty()) {

            Card lastCard = cardsOnTable.remove(cardsOnTable.size() - 1);
            this.cardsTable.clear();
            this.cardsTable.add(lastCard);

            System.out.println("Mesa limpia. Última carta: " + lastCard.getValue()+ " "+lastCard.getColor());
        } else {
            System.out.println("La mesa ya está vacía.");
        }

        return cardsOnTable;
    }
}
