package com.avlsimulator.ui.animations;

import com.avlsimulator.ui.components.NodeRenderer;
import com.avlsimulator.ui.panels.TreePanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.List;

/**
 * Rotación v2.4: parpadeo naranja × 2, luego redibujo.
 */
public class RotationAnimation {

    private final TreePanel treePanel;
    private Timeline timeline;

    public RotationAnimation(TreePanel treePanel) {
        this.treePanel = treePanel;
    }

    public void animar(List<Integer> nodosRotacion, Runnable onFinish) {
        if (timeline != null) timeline.stop();
        nodosRotacion.forEach(v -> treePanel.resaltarNodo(v, NodeRenderer.COLOR_ROTACION));

        timeline = new Timeline(
            new KeyFrame(Duration.millis(180), e -> nodosRotacion.forEach(v ->
                treePanel.resaltarNodo(v, NodeRenderer.COLOR_NORMAL))),
            new KeyFrame(Duration.millis(360), e -> nodosRotacion.forEach(v ->
                treePanel.resaltarNodo(v, NodeRenderer.COLOR_ROTACION))),
            new KeyFrame(Duration.millis(540), e -> nodosRotacion.forEach(v ->
                treePanel.resaltarNodo(v, NodeRenderer.COLOR_NORMAL))),
            new KeyFrame(Duration.millis(640), e -> {
                treePanel.redibujar();
                if (onFinish != null) onFinish.run();
            })
        );
        timeline.play();
    }

    public void detener() { if (timeline != null) timeline.stop(); }
}
