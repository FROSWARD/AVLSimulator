package com.avlsimulator.ui.panels;

import com.avlsimulator.model.AVLNode;
import com.avlsimulator.service.AVLService;
import com.avlsimulator.ui.components.NodeRenderer;
import com.avlsimulator.util.TreeLayoutCalculator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class TreePanel extends VBox {

    private static final double CANVAS_MIN_ANCHO = 1200.0;
    private static final double CANVAS_MIN_ALTO  = 700.0;
    private static final String BG_CANVAS = "#0B1220";

    private final Canvas canvas;
    private final Pane   canvasPane;
    private final AVLService avlService;
    private final TreeLayoutCalculator layoutCalculator;
    private final NodeRenderer nodeRenderer;
    private final Map<Integer, Color>   coloresNodos  = new HashMap<>();
    private final Map<Integer, Double>  opacidadNodos = new HashMap<>();
    private final Tooltip tooltip;

    private final Label lblNodeCount   = new Label("0 nodos");
    private final Label lblStatusBadge;

    private double radioActual = TreeLayoutCalculator.RADIO_MAX;
    private double zoomFactor  = 1.0;

    public TreePanel(AVLService avlService) {
        this.avlService       = avlService;
        this.layoutCalculator = new TreeLayoutCalculator();
        this.nodeRenderer     = new NodeRenderer();

        setStyle("-fx-background-color: #080E1C;");

        lblStatusBadge = new Label("Balanceado  ✓");
        lblStatusBadge.setStyle(
            "-fx-background-color: #052e16;" +
            "-fx-text-fill: #34d399;" +
            "-fx-font-size: 11px;" +
            "-fx-padding: 3 10 3 10;" +
            "-fx-background-radius: 12;"
        );
        lblNodeCount.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        canvas = new Canvas(CANVAS_MIN_ANCHO, CANVAS_MIN_ALTO);
        canvasPane = new Pane(canvas);
        canvasPane.setStyle("-fx-background-color: " + BG_CANVAS + ";");
        canvasPane.setPrefSize(CANVAS_MIN_ANCHO, CANVAS_MIN_ALTO);

        tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.millis(80));
        tooltip.setHideDelay(Duration.millis(80));
        tooltip.setFont(Font.font("Consolas", FontWeight.NORMAL, 12));
        tooltip.setStyle(
            "-fx-background-color: #0F172A; -fx-text-fill: #CBD5E1;" +
            "-fx-border-color: #1E3A5F; -fx-border-width: 1;" +
            "-fx-padding: 8 12; -fx-background-radius: 6;"
        );

        canvas.setOnMouseMoved(e -> {
            AVLNode nodo = buscarNodo(avlService.getRaiz(), e.getX() / zoomFactor, e.getY() / zoomFactor);
            if (nodo != null) {
                tooltip.setText(
                    "Valor  : " + nodo.valor + "\n" +
                    "Altura : " + nodo.altura + "\n" +
                    "FB     : " + nodo.getFactorBalance() + "\n" +
                    "Izq    : " + (nodo.izquierdo != null ? nodo.izquierdo.valor : "null") + "\n" +
                    "Der    : " + (nodo.derecho   != null ? nodo.derecho.valor   : "null")
                );
                if (!tooltip.isShowing()) tooltip.show(canvas, e.getScreenX()+14, e.getScreenY()+14);
            } else { tooltip.hide(); }
        });
        canvas.setOnMouseExited(e -> tooltip.hide());

        ScrollPane scroll = new ScrollPane(canvasPane);
        scroll.setStyle("-fx-background-color: " + BG_CANVAS + "; -fx-background: " + BG_CANVAS + ";");
        scroll.setFitToWidth(false); scroll.setFitToHeight(false);
        scroll.setPannable(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(buildHeader(), scroll);
    }

    // ── Header ───────────────────────────────────────────────
    private HBox buildHeader() {
        Label treeIcon = new Label("🌳");
        treeIcon.setStyle("-fx-font-size: 18px;");
        Label titulo = new Label("ÁRBOL AVL");
        titulo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #CBD5E1;");

        HBox left = new HBox(8, treeIcon, titulo, lblStatusBadge, lblNodeCount);
        left.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Label btnZoomIn  = iconBtn("＋", () -> { zoomFactor = Math.min(zoomFactor+0.2,3.0); aplicarZoom(); });
        Label btnZoomOut = iconBtn("－", () -> { zoomFactor = Math.max(zoomFactor-0.2,0.3); aplicarZoom(); });
        Label btnReset   = iconBtn("↺", () -> { zoomFactor = 1.0; aplicarZoom(); });

        HBox header = new HBox(left, spacer, new HBox(4, btnZoomIn, btnZoomOut, btnReset));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 16, 10, 16));
        header.setStyle("-fx-background-color: #0D1528; -fx-border-color: #1A2540; -fx-border-width: 0 0 1 0;");
        return header;
    }

    private Label iconBtn(String text, Runnable action) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:14px;-fx-text-fill:#64748B;-fx-padding:4 8;" +
                   "-fx-background-color:#1A2540;-fx-background-radius:5;-fx-cursor:hand;");
        l.setOnMouseEntered(e -> l.setStyle(l.getStyle().replace("#1A2540","#1E3050")));
        l.setOnMouseExited(e  -> l.setStyle(l.getStyle().replace("#1E3050","#1A2540")));
        l.setOnMouseClicked(e -> action.run());
        return l;
    }

    private void aplicarZoom() {
        canvas.setScaleX(zoomFactor); canvas.setScaleY(zoomFactor);
        canvasPane.setPrefSize(canvas.getWidth()*zoomFactor, canvas.getHeight()*zoomFactor);
    }

    // ── Dibujo ───────────────────────────────────────────────
    public void redibujar() {
        int totalNodos = avlService.getTotalNodos();
        lblNodeCount.setText(totalNodos + " nodos");

        double w = Math.max(CANVAS_MIN_ANCHO, totalNodos * 55.0);
        double h = Math.max(CANVAS_MIN_ALTO,  (avlService.getAltura() + 2) * 100.0);
        canvas.setWidth(w); canvas.setHeight(h);
        canvasPane.setPrefSize(w * zoomFactor, h * zoomFactor);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.web(BG_CANVAS));
        gc.fillRect(0, 0, w, h);

        if (avlService.estaVacio()) { dibujarVacio(gc, w, h); return; }

        layoutCalculator.calcular(avlService.getRaiz(), w, h);
        radioActual = layoutCalculator.getRadioActual();
        dibujarAristas(gc, avlService.getRaiz(), radioActual);
        dibujarNodos(gc, avlService.getRaiz(), radioActual);
    }

    private void dibujarAristas(GraphicsContext gc, AVLNode n, double r) {
        if (n == null) return;
        if (n.izquierdo != null) { nodeRenderer.dibujarArista(gc,n,n.izquierdo,r); dibujarAristas(gc,n.izquierdo,r); }
        if (n.derecho   != null) { nodeRenderer.dibujarArista(gc,n,n.derecho,r);   dibujarAristas(gc,n.derecho,r);   }
    }

    private void dibujarNodos(GraphicsContext gc, AVLNode n, double r) {
        if (n == null) return;
        Color base  = coloresNodos.getOrDefault(n.valor, NodeRenderer.COLOR_NORMAL);
        double opa  = opacidadNodos.getOrDefault(n.valor, 1.0);
        double prev = gc.getGlobalAlpha();
        gc.setGlobalAlpha(opa);
        nodeRenderer.dibujar(gc, n, base, r);
        gc.setGlobalAlpha(prev);
        dibujarNodos(gc, n.izquierdo, r);
        dibujarNodos(gc, n.derecho,   r);
    }

    private AVLNode buscarNodo(AVLNode n, double mx, double my) {
        if (n == null) return null;
        double dx=mx-n.x, dy=my-n.y;
        if (Math.sqrt(dx*dx+dy*dy) <= radioActual) return n;
        AVLNode izq = buscarNodo(n.izquierdo, mx, my);
        return izq != null ? izq : buscarNodo(n.derecho, mx, my);
    }

    private void dibujarVacio(GraphicsContext gc, double w, double h) {
        gc.setFill(Color.web("#CBD5E1", 0.5));
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("🌲", w/2, h/2 - 10);
        gc.setFill(Color.web("#CBD5E1", 0.7));
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        gc.fillText("Árbol vacío", w/2, h/2 + 38);
        gc.setFill(Color.web("#64748B"));
        gc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        gc.fillText("Inserta un valor para comenzar", w/2, h/2 + 62);
    }

    // ── API pública ──────────────────────────────────────────
    public void resaltarNodo(int valor, Color color) { coloresNodos.put(valor, color); redibujar(); }
    public void quitarResaltado(int valor)           { coloresNodos.remove(valor); opacidadNodos.remove(valor); redibujar(); }
    public void limpiarResaltados()                  { coloresNodos.clear(); opacidadNodos.clear(); redibujar(); }
    public Canvas getCanvas()                        { return canvas; }
    public double getCanvasWidth()                   { return canvas.getWidth(); }
    public double getCanvasHeight()                  { return canvas.getHeight(); }

    /** Fade suave de un color a otro en N ms */
    public void iniciarFadeColor(int valor, Color desde, Color hasta, double duracionMs) {
        int pasos = 20;
        double intervalo = duracionMs / pasos;
        Timeline tl = new Timeline();
        for (int i = 0; i <= pasos; i++) {
            final int paso = i;
            tl.getKeyFrames().add(new KeyFrame(Duration.millis(intervalo * i), e -> {
                double t = (double) paso / pasos;
                coloresNodos.put(valor, desde.interpolate(hasta, t));
                redibujar();
            }));
        }
        tl.play();
    }

    /** Fade de opacidad del nodo de `desde` a `hasta` en N ms */
    public void iniciarFadeOpacidad(int valor, double desde, double hasta, double duracionMs) {
        int pasos = 15;
        double intervalo = duracionMs / pasos;
        Timeline tl = new Timeline();
        for (int i = 0; i <= pasos; i++) {
            final int paso = i;
            tl.getKeyFrames().add(new KeyFrame(Duration.millis(intervalo * i), e -> {
                double t   = (double) paso / pasos;
                opacidadNodos.put(valor, desde + (hasta - desde) * t);
                redibujar();
            }));
        }
        tl.play();
    }
}
