package com.avlsimulator.ui.panels;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Side;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Panel de información deslizable (drawer) desde la derecha.
 * Se superpone sobre el TreePanel con fade + slide.
 */
public class InfoOverlay extends StackPane {

    private final InfoPanel infoPanel;
    private boolean visible = false;

    public InfoOverlay(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;

        infoPanel.setPrefWidth(280);
        infoPanel.setMaxWidth(280);
        infoPanel.setStyle(infoPanel.getStyle() +
            "-fx-effect: dropshadow(gaussian, #000000AA, 24, 0.4, -4, 0);");

        // Empieza oculto a la derecha
        infoPanel.setTranslateX(300);
        infoPanel.setOpacity(0);

        setPickOnBounds(false); // no bloquea clicks cuando está oculto
        getChildren().add(infoPanel);
        StackPane.setAlignment(infoPanel, javafx.geometry.Pos.CENTER_RIGHT);
    }

    public void toggle() {
        if (visible) ocultar(); else mostrar();
    }

    private void mostrar() {
        visible = true;
        infoPanel.setVisible(true);

        TranslateTransition slide = new TranslateTransition(Duration.millis(280), infoPanel);
        slide.setToX(0);

        FadeTransition fade = new FadeTransition(Duration.millis(280), infoPanel);
        fade.setToValue(1);

        slide.play(); fade.play();
    }

    private void ocultar() {
        visible = false;

        TranslateTransition slide = new TranslateTransition(Duration.millis(220), infoPanel);
        slide.setToX(300);

        FadeTransition fade = new FadeTransition(Duration.millis(220), infoPanel);
        fade.setToValue(0);
        fade.setOnFinished(e -> infoPanel.setVisible(false));

        slide.play(); fade.play();
    }

    public boolean isOpen() { return visible; }
}
