package com.avlsimulator.ui.animations;

import com.avlsimulator.model.AVLNode;
import com.avlsimulator.ui.panels.TreePanel;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;

import java.util.HashMap;
import java.util.Map;

/**
 * Anima el movimiento real de nodos en el canvas:
 * interpola X,Y desde posición anterior a la nueva.
 *
 * Uso:
 *   1. Llama snapshot() ANTES de modificar el árbol → guarda posiciones actuales
 *   2. Modifica el árbol + recalcula layout
 *   3. Llama animar(raiz, duracionMs, onFinish) → mueve nodos suavemente
 */
public class NodeMoveAnimation {

    private final TreePanel treePanel;

    // valor → {xOrigen, yOrigen}
    private final Map<Integer, double[]> snapshot = new HashMap<>();

    public NodeMoveAnimation(TreePanel treePanel) {
        this.treePanel = treePanel;
    }

    /** Guarda posiciones actuales de todos los nodos antes de la operación */
    public void snapshot(AVLNode raiz) {
        snapshot.clear();
        recorrer(raiz);
    }

    private void recorrer(AVLNode n) {
        if (n == null) return;
        snapshot.put(n.valor, new double[]{n.x, n.y});
        recorrer(n.izquierdo);
        recorrer(n.derecho);
    }

    /**
     * Anima todos los nodos desde sus posiciones del snapshot hasta las
     * posiciones actuales (ya calculadas por TreeLayoutCalculator).
     * Durante la animación se redibuja el canvas cada frame.
     */
    public void animar(AVLNode raiz, double duracionMs, Runnable onFinish) {
        if (raiz == null) { if (onFinish != null) onFinish.run(); return; }

        // Captura destinos actuales
        Map<Integer, double[]> destinos = new HashMap<>();
        capturarDestinos(raiz, destinos);

        // Restaura posiciones de origen para empezar la interpolación desde ahí
        aplicarPosiciones(raiz, snapshot);

        long[] startTime = {-1L};
        long durNanos = (long)(duracionMs * 1_000_000);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (startTime[0] < 0) startTime[0] = now;

                double t = Math.min(1.0, (double)(now - startTime[0]) / durNanos);
                double eased = easeInOut(t);

                // Interpolación de cada nodo
                interpolar(raiz, snapshot, destinos, eased);
                treePanel.redibujar();

                if (t >= 1.0) {
                    stop();
                    // Asegura posición final exacta
                    aplicarPosiciones(raiz, destinos);
                    treePanel.redibujar();
                    if (onFinish != null) onFinish.run();
                }
            }
        };
        timer.start();
    }

    private void interpolar(AVLNode n, Map<Integer, double[]> desde,
                            Map<Integer, double[]> hasta, double t) {
        if (n == null) return;
        double[] org = desde.get(n.valor);
        double[] dst = hasta.get(n.valor);
        if (org != null && dst != null) {
            n.x = org[0] + (dst[0] - org[0]) * t;
            n.y = org[1] + (dst[1] - org[1]) * t;
        } else if (dst != null) {
            // Nodo nuevo: aparece desde su destino (fade-in lo maneja NodeRenderer)
            n.x = dst[0]; n.y = dst[1];
        }
        interpolar(n.izquierdo, desde, hasta, t);
        interpolar(n.derecho,   desde, hasta, t);
    }

    private void capturarDestinos(AVLNode n, Map<Integer, double[]> map) {
        if (n == null) return;
        map.put(n.valor, new double[]{n.x, n.y});
        capturarDestinos(n.izquierdo, map);
        capturarDestinos(n.derecho,   map);
    }

    private void aplicarPosiciones(AVLNode n, Map<Integer, double[]> map) {
        if (n == null) return;
        double[] pos = map.get(n.valor);
        if (pos != null) { n.x = pos[0]; n.y = pos[1]; }
        aplicarPosiciones(n.izquierdo, map);
        aplicarPosiciones(n.derecho,   map);
    }

    /** Ease in-out cúbico */
    private double easeInOut(double t) {
        return t < 0.5 ? 4*t*t*t : 1 - Math.pow(-2*t+2, 3)/2;
    }
}
