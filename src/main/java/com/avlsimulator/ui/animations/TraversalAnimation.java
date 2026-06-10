package com.avlsimulator.ui.animations;

import com.avlsimulator.ui.components.NodeRenderer;
import com.avlsimulator.ui.panels.TreePanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.List;

/**
 * Recorrido v2.4: trail visual — activo brilla, anterior queda tenue.
 */
public class TraversalAnimation {

    private final TreePanel treePanel;
    private Timeline timeline;
    private double velocidadMs = 600;

    public TraversalAnimation(TreePanel treePanel) {
        this.treePanel = treePanel;
    }

    public void animar(List<Integer> valores, Runnable onFinish) {
        if (timeline != null) timeline.stop();
        treePanel.limpiarResaltados();
        timeline = new Timeline();

        for (int i = 0; i < valores.size(); i++) {
            final int val  = valores.get(i);
            final int idx  = i;
            timeline.getKeyFrames().add(new KeyFrame(
                Duration.millis(velocidadMs * idx),
                e -> treePanel.resaltarNodo(val, NodeRenderer.COLOR_RECORRIDO)
            ));
            if (i > 0) {
                final int prev = valores.get(i - 1);
                timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(velocidadMs * idx - velocidadMs * 0.15),
                    e -> treePanel.resaltarNodo(prev, NodeRenderer.COLOR_VISITADO)
                ));
            }
        }

        timeline.getKeyFrames().add(new KeyFrame(
            Duration.millis(velocidadMs * valores.size()),
            e -> { treePanel.limpiarResaltados(); if (onFinish != null) onFinish.run(); }
        ));
        timeline.play();
    }

    public void detener() { if (timeline != null) { timeline.stop(); treePanel.limpiarResaltados(); } }
    public void setVelocidadMs(double ms) { this.velocidadMs = ms; }
    public double getVelocidadMs()        { return velocidadMs; }
}
