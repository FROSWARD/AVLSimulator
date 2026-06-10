package com.avlsimulator.ui.animations;

import com.avlsimulator.model.AVLNode;
import com.avlsimulator.service.AVLService;
import com.avlsimulator.ui.components.NodeRenderer;
import com.avlsimulator.ui.panels.TreePanel;
import com.avlsimulator.util.TreeLayoutCalculator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Eliminación v2.4:
 * 1. Flash rojo + fade opacidad a 0 en el nodo eliminado
 * 2. Elimina del árbol + recalcula layout
 * 3. Mueve el resto de nodos a sus nuevas posiciones
 */
public class DeleteAnimation {

    private final TreePanel          treePanel;
    private final AVLService         avlService;
    private final NodeMoveAnimation  moveAnim;
    private final TreeLayoutCalculator layout = new TreeLayoutCalculator();

    public DeleteAnimation(TreePanel treePanel, AVLService avlService) {
        this.treePanel  = treePanel;
        this.avlService = avlService;
        this.moveAnim   = new NodeMoveAnimation(treePanel);
    }

    public void animar(int valor, double canvasW, double canvasH, Runnable onFinish) {
        AVLNode raiz = avlService.getRaiz();
        if (raiz == null) { if (onFinish != null) onFinish.run(); return; }

        // Flash rojo + fade out del nodo
        treePanel.resaltarNodo(valor, NodeRenderer.COLOR_ELIMINAR);
        treePanel.iniciarFadeOpacidad(valor, 1.0, 0.0, 400);

        Timeline eliminar = new Timeline(new KeyFrame(Duration.millis(450), e -> {
            // Snapshot ANTES de eliminar
            moveAnim.snapshot(avlService.getRaiz());

            // Eliminar
            avlService.eliminar(valor);
            treePanel.quitarResaltado(valor);

            AVLNode nuevaRaiz = avlService.getRaiz();
            if (nuevaRaiz == null) { treePanel.redibujar(); if (onFinish != null) onFinish.run(); return; }

            // Recalcula layout
            layout.calcular(nuevaRaiz, canvasW, canvasH);

            // Mueve nodos
            moveAnim.animar(nuevaRaiz, 450, () -> {
                treePanel.redibujar();
                if (onFinish != null) onFinish.run();
            });
        }));
        eliminar.play();
    }
}
