package com.avlsimulator.ui.animations;

import com.avlsimulator.model.AVLNode;
import com.avlsimulator.ui.components.NodeRenderer;
import com.avlsimulator.ui.panels.TreePanel;
import com.avlsimulator.util.TreeLayoutCalculator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Insercion v2.4:
 * 1. snapshotAntes() se llama ANTES de insertar — guarda posiciones viejas
 * 2. animar() se llama DESPUES de insertar — recalcula layout y mueve nodos
 * 3. Flash verde en el nodo insertado con fade a normal
 */
public class InsertAnimation {

    private final TreePanel            treePanel;
    private final NodeMoveAnimation    moveAnim;
    private final TreeLayoutCalculator layout = new TreeLayoutCalculator();

    public InsertAnimation(TreePanel treePanel) {
        this.treePanel = treePanel;
        this.moveAnim  = new NodeMoveAnimation(treePanel);
    }

    /**
     * Llama esto ANTES de insertar el nodo en el arbol.
     * Guarda las posiciones actuales de todos los nodos.
     */
    public void snapshotAntes(AVLNode raiz) {
        moveAnim.snapshot(raiz);
    }

    /**
     * Llama esto DESPUES de insertar.
     * Recalcula el layout y anima el movimiento de todos los nodos.
     */
    public void animar(AVLNode raiz, int valor, double canvasW, double canvasH, Runnable onFinish) {
        // Recalcula posiciones finales
        layout.calcular(raiz, canvasW, canvasH);

        // Mueve nodos con interpolacion desde posiciones viejas a nuevas
        moveAnim.animar(raiz, 450, () -> {
            // Flash verde en el nodo insertado
            treePanel.resaltarNodo(valor, NodeRenderer.COLOR_INSERTADO);
            treePanel.iniciarFadeColor(valor, NodeRenderer.COLOR_INSERTADO,
                                       NodeRenderer.COLOR_NORMAL, 600);
            Timeline end = new Timeline(new KeyFrame(Duration.millis(650),
                e -> { treePanel.quitarResaltado(valor); if (onFinish != null) onFinish.run(); }
            ));
            end.play();
        });
    }
}