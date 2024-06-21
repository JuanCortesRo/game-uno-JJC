

package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a card in the Uno game.
 */
public class Card {
    private String url;
    private String value;
    private String color;
    private Image image;
    private ImageView cardImageView;

    /**
     * Constructs a Card with the specified image URL and name.
     *
     * @param url the URL of the card image
     * @param value of the card
     * @param color of the card
     * @param image the image of the card
     * @param cardImageView the ImageView of the card
     */
    public Card(String url, String value, String color,Image image, ImageView cardImageView) {
        this.url = url;
        this.value = value;
        this.color = color;
        this.image = new Image(String.valueOf(getClass().getResource(url)));
        this.cardImageView = createCardImageView();
    }

    /**
     * Creates and configures the ImageView for the card.
     *
     * @return the configured ImageView of the card
     */
    private ImageView createCardImageView() {
        ImageView card = new ImageView(this.image);
        card.setY(16);
        card.setFitHeight(110);
        card.setFitWidth(74);
        return card;
    }

    /**
     * Gets the ImageView representation of the card.
     *
     * @return the ImageView of the card
     */
    public ImageView getCard() {
        return cardImageView;
    }

    /**
     * Gets the image of the card.
     *
     * @return the Image of the card
     */
    public Image getImage() {
        return image;
    }

    /**
     * Returns the value of the card.
     *
     * @return The value of the card as a string.
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the color of the card.
     *
     * @return The color of the card as a string.
     */
    public String getColor() {
        return color;
    }

    /**
     * Checks if this card is compatible with another card.
     *
     * @param other The other card to compare compatibility with.
     * @return true if the cards are compatible, false otherwise.
     */
    public boolean isCompatible(Card other) {
        if (this.color != null && this.value != null) {
            if (this.color.equals(other.getColor()) || this.value.equals(other.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the color of the card to the specified new color.
     *
     * @param newColor The new color to set for the card.
     */
    public void setColor(String newColor){
        this.color = newColor;
    }

    /**
     * Prints the color and value of the card to the console.
     */
    public void printColor(){
        System.out.println("Carta actual:"+getColor()+getValue());
    }
}