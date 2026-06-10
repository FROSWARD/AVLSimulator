package com.avlsimulator.ui.panels;

import com.avlsimulator.service.AVLService;
import com.avlsimulator.service.TraversalService.TipoRecorrido;
import com.avlsimulator.ui.animations.DeleteAnimation;
import com.avlsimulator.ui.animations.InsertAnimation;
import com.avlsimulator.ui.animations.TraversalAnimation;
import com.avlsimulator.ui.components.NodeRenderer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class TopBar extends VBox {

    private static final String BG1  = "#0D1528";
    private static final String BG2  = "#0A1020";
    private static final String BLUE = "#3B82F6";
    private static final String PURP = "#7C3AED";
    private static final String RED  = "#EF4444";
    private static final String GRAY = "#374151";
    private static final String DIM  = "#64748B";
    private static final String MAIN = "#F1F5F9";

    private final AVLService         avlService;
    private final TreePanel          treePanel;
    private final InfoPanel          infoPanel;
    private final InsertAnimation    insertAnim;
    private final DeleteAnimation    deleteAnim;
    private final TraversalAnimation traversalAnim;

    private final TextField txtValor;
    private final Slider    sldVelocidad;
    private boolean animando = false;

    // Duracion de animacion en ms — controlada por el slider
    private double duracionAnim = 600;

    public TopBar(AVLService avlService, TreePanel treePanel,
                  InfoPanel infoPanel, InfoOverlay infoOverlay) {
        this.avlService    = avlService;
        this.treePanel     = treePanel;
        this.infoPanel     = infoPanel;
        this.insertAnim    = new InsertAnimation(treePanel);
        this.deleteAnim    = new DeleteAnimation(treePanel, avlService);
        this.traversalAnim = new TraversalAnimation(treePanel);

        setSpacing(0);

        // ══ BARRA 1 ══════════════════════════════════════════
        HBox bar1 = new HBox(10);
        bar1.setStyle("-fx-background-color:" + BG1 + ";" +
                      "-fx-border-color:#1A2540;-fx-border-width:0 0 1 0;");
        bar1.setPadding(new Insets(8, 16, 8, 16));
        bar1.setAlignment(Pos.CENTER_LEFT);

        // Logo mas grande
        ImageView logo = new ImageView();
        try { logo.setImage(new Image(getClass().getResourceAsStream("/logo.png"))); }
        catch (Exception ignored) {}
        logo.setFitWidth(72);
        logo.setFitHeight(72);
        logo.setPreserveRatio(true);

        VBox brand = new VBox(2,
            styledLabel("AVL Simulator", MAIN, 16, true),
            styledLabel("Estructura de Datos II", DIM, 10, false)
        );
        brand.setAlignment(Pos.CENTER_LEFT);
        HBox logoBox = new HBox(10, logo, brand);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        txtValor = new TextField();
        txtValor.setPromptText("Número...");
        txtValor.setPrefWidth(120);
        txtValor.setStyle(
            "-fx-background-color:#1E293B;-fx-text-fill:" + MAIN + ";" +
            "-fx-prompt-text-fill:" + DIM + ";-fx-border-color:#1E3A5F;" +
            "-fx-border-width:1;-fx-border-radius:6;-fx-background-radius:6;" +
            "-fx-font-size:13px;-fx-padding:6 10;"
        );
        txtValor.setOnAction(e -> onInsertar());

        Button btnIns = btnPrimary("＋   Insertar", BLUE, e -> onInsertar());
        Button btnBus = btnPrimary("🔍  Buscar",    PURP, e -> onBuscar());
        Button btnEli = btnPrimary("✕   Eliminar",  RED,  e -> onEliminar());
        Button btnLmp = btnOutline("⌫   Limpiar",         e -> onLimpiar());

        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);
        Button btnInfo = btnOutline("📊  Info", e -> infoOverlay.toggle());

        bar1.getChildren().addAll(logoBox, vDiv(), txtValor, btnIns, btnBus, btnEli, btnLmp, sp1, btnInfo);

        // ══ BARRA 2 ══════════════════════════════════════════
        HBox bar2 = new HBox(10);
        bar2.setStyle("-fx-background-color:" + BG2 + ";" +
                      "-fx-border-color:#1A2540;-fx-border-width:0 0 1 0;");
        bar2.setPadding(new Insets(6, 16, 6, 16));
        bar2.setAlignment(Pos.CENTER_LEFT);

        Label lblRec = sectionLabel("RECORRIDOS");
        Button bInO  = btnTraversal("↻  InOrder",     () -> onRecorrido(TipoRecorrido.INORDER));
        Button bPreO = btnTraversal("↺  PreOrder",    () -> onRecorrido(TipoRecorrido.PREORDER));
        Button bPosO = btnTraversal("⇌  PostOrder",   () -> onRecorrido(TipoRecorrido.POSTORDER));
        Button bBFS  = btnTraversal("⊞  BFS (Nivel)", () -> onRecorrido(TipoRecorrido.BFS));
        HBox recGrp  = new HBox(6, bInO, bPreO, bPosO, bBFS);
        recGrp.setAlignment(Pos.CENTER_LEFT);

        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);

        // Slider: 200ms = rapido, 1500ms = lento
        // Controla todas las animaciones
        sldVelocidad = new Slider(200, 1500, 600);
        sldVelocidad.setPrefWidth(140);
        sldVelocidad.setStyle("-fx-accent:" + BLUE + ";");
        sldVelocidad.valueProperty().addListener((o, ov, nv) -> {
            duracionAnim = nv.doubleValue();
            traversalAnim.setVelocidadMs(duracionAnim);
        });

        HBox velGrp = new HBox(6,
            sectionLabel("VELOCIDAD DE ANIMACIÓN"),
            tinyLabel("Rápido"), sldVelocidad, tinyLabel("Lento")
        );
        velGrp.setAlignment(Pos.CENTER_LEFT);

        bar2.getChildren().addAll(lblRec, recGrp, sp2, velGrp);

        getChildren().addAll(bar1, bar2);
    }

    // ── Acciones ─────────────────────────────────────────────

    private void onInsertar() {
        if (animando) return;
        int v = parse(); if (v == Integer.MIN_VALUE) return;

        // Snapshot ANTES de insertar — aqui estan las posiciones viejas
        insertAnim.snapshotAntes(avlService.getRaiz());

        if (!avlService.insertar(v)) { alerta("El valor " + v + " ya existe."); return; }

        animando = true;
        insertAnim.animar(avlService.getRaiz(), v,
            treePanel.getCanvasWidth(), treePanel.getCanvasHeight(), () -> {
                animando = false;
                infoPanel.actualizar();
                infoPanel.agregarHistorial("Insertar " + v);
            });
        txtValor.clear();
    }

    private void onEliminar() {
        if (animando) return;
        int v = parse(); if (v == Integer.MIN_VALUE) return;
        if (avlService.buscar(v) == null) { alerta("El valor " + v + " no existe."); return; }
        animando = true;
        deleteAnim.animar(v, treePanel.getCanvasWidth(), treePanel.getCanvasHeight(), () -> {
            animando = false;
            infoPanel.actualizar();
            infoPanel.agregarHistorial("Eliminar " + v);
        });
        txtValor.clear();
    }

    private void onBuscar() {
        if (animando) return;
        int v = parse(); if (v == Integer.MIN_VALUE) return;
        if (avlService.buscar(v) == null) { alerta("El valor " + v + " no existe."); return; }
        animando = true;
        treePanel.resaltarNodo(v, NodeRenderer.COLOR_BUSCADO);
        infoPanel.agregarHistorial("Buscar " + v + " — Encontrado");
        javafx.animation.Timeline tl = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(duracionAnim), e -> {
                treePanel.quitarResaltado(v); animando = false;
            })
        );
        tl.play();
        txtValor.clear();
    }

    private void onLimpiar() {
        if (animando) return;
        avlService.limpiar();
        treePanel.limpiarResaltados();
        infoPanel.actualizar();
        infoPanel.agregarHistorial("Árbol limpiado");
    }

    private void onRecorrido(TipoRecorrido tipo) {
        if (animando) return;
        if (avlService.estaVacio()) { alerta("El árbol está vacío."); return; }
        List<Integer> vals = avlService.ejecutarRecorrido(tipo);
        String nombre = avlService.getNombreRecorrido(tipo);
        infoPanel.mostrarRecorrido(nombre, vals);
        animando = true;
        traversalAnim.animar(vals, () -> {
            animando = false;
            infoPanel.agregarHistorial(nombre);
        });
    }

    private int parse() {
        try { return Integer.parseInt(txtValor.getText().trim()); }
        catch (NumberFormatException e) { alerta("Ingresa un número entero válido."); return Integer.MIN_VALUE; }
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("AVL Simulator"); a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color:#1E293B;");
        a.showAndWait();
    }

    // ── Helpers UI ────────────────────────────────────────────

    private Button btnPrimary(String t, String c, javafx.event.EventHandler<javafx.event.ActionEvent> h) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:"+c+";-fx-text-fill:white;-fx-font-size:12px;" +
                   "-fx-font-weight:bold;-fx-background-radius:6;-fx-cursor:hand;-fx-padding:6 16;");
        b.setOnAction(h); return b;
    }

    private Button btnOutline(String t, javafx.event.EventHandler<javafx.event.ActionEvent> h) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color:transparent;-fx-text-fill:"+MAIN+";" +
                   "-fx-border-color:"+GRAY+";-fx-border-width:1;-fx-border-radius:6;" +
                   "-fx-background-radius:6;-fx-font-size:12px;-fx-cursor:hand;-fx-padding:6 14;");
        b.setOnAction(h); return b;
    }

    private Button btnTraversal(String t, Runnable action) {
        Button b = new Button(t);
        String base = "-fx-background-color:#1A2540;-fx-text-fill:"+MAIN+";" +
                      "-fx-font-size:11.5px;-fx-background-radius:5;-fx-cursor:hand;-fx-padding:5 12;";
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(base.replace("#1A2540","#243558")));
        b.setOnMouseExited(e  -> b.setStyle(base));
        b.setOnAction(e -> action.run()); return b;
    }

    private Rectangle vDiv() {
        Rectangle r = new Rectangle(1, 34); r.setFill(Color.web("#1A2540")); return r;
    }

    private Label styledLabel(String t, String c, int size, boolean bold) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:"+size+"px;-fx-text-fill:"+c+";"+(bold?"-fx-font-weight:bold;":""));
        return l;
    }

    private Label sectionLabel(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:9.5px;-fx-font-weight:bold;-fx-text-fill:"+DIM+";-fx-padding:0 6 0 0;");
        return l;
    }

    private Label tinyLabel(String t) {
        Label l = new Label(t); l.setStyle("-fx-font-size:9px;-fx-text-fill:"+DIM+";"); return l;
    }
}