package com.avlsimulator.ui.components;

import com.avlsimulator.model.AVLNode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Dibuja nodos AVL con degradado + brillo v2.1
 */
public class NodeRenderer {

    public static final Color COLOR_NORMAL    = Color.web("#1A4B8F");
    public static final Color COLOR_INSERTADO = Color.web("#065F46");
    public static final Color COLOR_ELIMINAR  = Color.web("#7F1D1D");
    public static final Color COLOR_ROTACION  = Color.web("#78350F");
    public static final Color COLOR_RECORRIDO = Color.web("#4C1D95");
    public static final Color COLOR_VISITADO  = Color.web("#1e3a5f");   // trail recorrido
    public static final Color COLOR_BUSCADO   = Color.web("#134E4A");
    public static final Color COLOR_BORDE     = Color.web("#0B1220");
    public static final Color COLOR_TEXTO     = Color.web("#F1F5F9");
    public static final Color COLOR_FB        = Color.web("#94A3B8");

    private static final double BORDE = 2.5;

    public void dibujar(GraphicsContext gc, AVLNode nodo, Color color, double radio) {
        double x = nodo.x, y = nodo.y;
        double fontSize   = Math.max(8, radio * 0.52);
        double fbFontSize = Math.max(7, radio * 0.36);

        // Sombra
        gc.setFill(Color.rgb(0, 0, 0, 0.35));
        gc.fillOval(x - radio + 4, y - radio + 4, radio * 2, radio * 2);

        // Borde
        gc.setFill(COLOR_BORDE);
        gc.fillOval(x - radio - BORDE, y - radio - BORDE,
                    (radio + BORDE) * 2, (radio + BORDE) * 2);

        // Degradado
        Color colorTop = color.brighter().interpolate(Color.web("#AACCFF"), 0.25);
        gc.setFill(new LinearGradient(
            x - radio, y - radio, x + radio, y + radio, false, CycleMethod.NO_CYCLE,
            new Stop(0.0, colorTop), new Stop(1.0, color)
        ));
        gc.fillOval(x - radio, y - radio, radio * 2, radio * 2);

        // Brillo
        gc.setFill(Color.rgb(255, 255, 255, 0.18));
        gc.fillOval(x - radio * 0.55, y - radio * 0.65, radio * 1.1, radio * 0.7);

        // Valor
        gc.setFill(COLOR_TEXTO);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(nodo.valor), x, y + fontSize * 0.37);

        // Factor de balance con semáforo
        if (radio >= 14) {
            int fb = nodo.getFactorBalance();
            Color fbColor = fb == 0 ? Color.web("#34D399")
                          : Math.abs(fb) == 1 ? Color.web("#FCD34D")
                          : Color.web("#F87171");
            gc.setFill(fbColor);
            gc.setFont(Font.font("Consolas", FontWeight.NORMAL, fbFontSize));
            gc.fillText(String.valueOf(fb), x, y + radio + fbFontSize + 2);
        }
    }

    public void dibujarArista(GraphicsContext gc, AVLNode padre, AVLNode hijo, double radio) {
        if (padre == null || hijo == null) return;
        double dx = hijo.x - padre.x, dy = hijo.y - padre.y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        if (dist == 0) return;
        double sx = padre.x + (dx/dist)*radio, sy = padre.y + (dy/dist)*radio;
        double ex = hijo.x  - (dx/dist)*radio, ey = hijo.y  - (dy/dist)*radio;
        gc.setStroke(Color.web("#1E3A5F", 0.85));
        gc.setLineWidth(Math.max(1.2, radio * 0.07));
        gc.strokeLine(sx, sy, ex, ey);
    }
}
