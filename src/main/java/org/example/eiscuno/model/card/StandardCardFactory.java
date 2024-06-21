package org.example.eiscuno.model.card;

import javafx.scene.image.Image;

public class StandardCardFactory extends CardFactory {
    @Override
    public Card createCard(String url, String value, String color) {
        Image image = createImage(url);
        return new Card(url, value, color, image, createCardImageView(image));
    }
}