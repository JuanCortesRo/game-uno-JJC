package org.example.eiscuno.model.card;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class CardFactory {
    public abstract Card createCard(String url, String value, String color);

    protected Image createImage(String url) {
        return new Image(String.valueOf(getClass().getResource(url)));
    }

    protected ImageView createCardImageView(Image image) {
        ImageView card = new ImageView(image);
        card.setY(16);
        card.setFitHeight(110);
        card.setFitWidth(74);
        return card;
    }
}