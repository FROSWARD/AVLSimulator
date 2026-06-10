package com.avlsimulator.ui.panels;

import com.avlsimulator.service.AVLService;
import com.avlsimulator.service.TraversalService.TipoRecorrido;
import com.avlsimulator.ui.animations.DeleteAnimation;
import com.avlsimulator.ui.animations.InsertAnimation;
import com.avlsimulator.ui.animations.TraversalAnimation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Panel lateral izquierdo — diseño visual renovado v2.0
 * Estructura: header con logo, sección de operaciones, sección de recorridos,
 * control de velocidad.
 */
public class ControlPanel extends VBox {

    // Paleta
    private static final String BG_PANEL       = "#0D1528";
    private static final String BG_SECTION     = "#111827";
    private static final String ACCENT_BLUE    = "#3B82F6";
    private static final String ACCENT_PURPLE  = "#7C3AED";
    private static final String COLOR_RED      = "#EF4444";
    private static final String COLOR_TEAL     = "#14B8A6";
    private static final String COLOR_GRAY     = "#374151";
    private static final String TEXT_PRIMARY   = "#F1F5F9";
    private static final String TEXT_SECONDARY = "#64748B";
    private static final String TEXT_LABEL     = "#94A3B8";

    private final AVLService avlService;
    private final TreePanel treePanel;
    private final InfoPanel infoPanel;

    private final InsertAnimation insertAnimation;
    private final DeleteAnimation deleteAnimation;
    private final TraversalAnimation traversalAnimation;

    private final TextField txtValor;
    private final Slider sldVelocidad;
    private boolean animando = false;

    public ControlPanel(AVLService avlService, TreePanel treePanel, InfoPanel infoPanel) {
        this.avlService         = avlService;
        this.treePanel          = treePanel;
        this.infoPanel          = infoPanel;
        this.insertAnimation    = new InsertAnimation(treePanel);
        this.deleteAnimation    = new DeleteAnimation(treePanel, avlService);
        this.traversalAnimation = new TraversalAnimation(treePanel);

        setStyle("-fx-background-color: " + BG_PANEL + ";");
        setPrefWidth(240);
        setMinWidth(240);
        setMaxWidth(240);
        setSpacing(0);

        // ── Header ──────────────────────────────────────────
        VBox header = construirHeader();

        // ── Separador ───────────────────────────────────────
        Rectangle sep1 = separador();

        // ── Input + botones CRUD ─────────────────────────────
        txtValor = new TextField();
        txtValor.setPromptText("Ingresa un número");
        txtValor.setStyle(
            "-fx-background-color: #1E293B;" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill: " + TEXT_SECONDARY + ";" +
            "-fx-border-color: #1E3A5F;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 8 12;"
        );
        txtValor.setOnAction(e -> onInsertar());

        VBox secOps = construirSeccion("OPERACIONES",
            txtValor,
            crearBotonPrimario("＋  Insertar",   ACCENT_BLUE,   e -> onInsertar()),
            crearBotonPrimario("🔍  Buscar",      ACCENT_PURPLE, e -> onBuscar()),
            crearBotonPrimario("✕  Eliminar",    COLOR_RED,     e -> onEliminar()),
            crearBotonSecundario("⌫  Limpiar árbol",            e -> onLimpiar())
        );

        Rectangle sep2 = separador();

        // ── Recorridos ───────────────────────────────────────
        VBox secRec = construirSeccion("RECORRIDOS",
            crearBotonRecorrido("InOrder",   "↻", TipoRecorrido.INORDER),
            crearBotonRecorrido("PreOrder",  "↺", TipoRecorrido.PREORDER),
            crearBotonRecorrido("PostOrder", "⇌", TipoRecorrido.POSTORDER),
            crearBotonRecorrido("BFS (Nivel)", "⊞", TipoRecorrido.BFS)
        );

        Rectangle sep3 = separador();

        // ── Velocidad ────────────────────────────────────────
        sldVelocidad = new Slider(200, 1500, 600);
        sldVelocidad.setStyle("-fx-control-inner-background: #1E293B; -fx-accent: " + ACCENT_BLUE + ";");
        sldVelocidad.valueProperty().addListener((obs, o, n) ->
            traversalAnimation.setVelocidadMs(n.doubleValue())
        );

        Label lblLento  = etiquetaPequena("Rápido");
        Label lblRapido = etiquetaPequena("Lento");
        HBox hSlider = new HBox(6, lblLento, sldVelocidad, lblRapido);
        hSlider.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(sldVelocidad, Priority.ALWAYS);

        VBox secVel = construirSeccion("VELOCIDAD DE ANIMACIÓN", hSlider);

        // ── Spacer ───────────────────────────────────────────
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ── Footer ───────────────────────────────────────────
        Label footer = new Label("FROSWARD · Estructura de Datos II");
        footer.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        footer.setPadding(new Insets(10, 16, 12, 16));

        getChildren().addAll(
            header, sep1,
            secOps, sep2,
            secRec, sep3,
            secVel,
            spacer, footer
        );
    }

    // =========================================================
    // CONSTRUCTORES DE SECCIONES / WIDGETS
    // =========================================================

    private VBox construirHeader() {
        HBox logoRow = new HBox(10);
        logoRow.setAlignment(Pos.CENTER_LEFT);

        // Logo pequeño
        ImageView logoView = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/logo.png"));
            logoView.setImage(img);
        } catch (Exception ignored) {}
        logoView.setFitWidth(32);
        logoView.setFitHeight(32);
        logoView.setPreserveRatio(true);

        VBox textBlock = new VBox(1);
        Label titulo = new Label("AVL Simulator");
        titulo.setStyle(
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
            "-fx-font-size: 17px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );
        Label sub = new Label("Visualiza y comprende árboles AVL");
        sub.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        textBlock.getChildren().addAll(titulo, sub);

        logoRow.getChildren().addAll(logoView, textBlock);
        logoRow.setPadding(new Insets(16, 16, 14, 16));

        VBox header = new VBox(logoRow);
        header.setStyle("-fx-background-color: " + BG_PANEL + ";");
        return header;
    }

    /**
     * Sección con label de título y contenido con padding.
     */
    private VBox construirSeccion(String titulo, javafx.scene.Node... hijos) {
        Label lbl = new Label(titulo);
        lbl.setStyle(
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + TEXT_SECONDARY + ";" +
            "-fx-letter-spacing: 1.2px;"
        );

        VBox content = new VBox(6);
        content.setPadding(new Insets(10, 16, 14, 16));
        content.getChildren().add(lbl);
        for (javafx.scene.Node h : hijos) content.getChildren().add(h);
        content.setStyle("-fx-background-color: " + BG_SECTION + ";");
        return content;
    }

    private Button crearBotonPrimario(String texto, String colorHex,
                                       javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
            "-fx-background-color: " + colorHex + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12.5px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 14;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(
            "-fx-background-color: " + colorHex, "-fx-background-color: derive(" + colorHex + ", 20%)")));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(
            "-fx-background-color: derive(" + colorHex + ", 20%)", "-fx-background-color: " + colorHex)));
        btn.setOnAction(handler);
        return btn;
    }

    private Button crearBotonSecundario(String texto,
                                        javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_LABEL + ";" +
            "-fx-font-size: 12px;" +
            "-fx-border-color: " + COLOR_GRAY + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 7 14;"
        );
        btn.setOnAction(handler);
        return btn;
    }

    private HBox crearBotonRecorrido(String nombre, String icono, TipoRecorrido tipo) {
        // Fila estilo "list item" con flecha a la derecha
        Label iconLbl = new Label(icono);
        iconLbl.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + ACCENT_BLUE + ";" +
            "-fx-min-width: 22px;"
        );
        Label nameLbl = new Label(nombre);
        nameLbl.setStyle("-fx-font-size: 12.5px; -fx-text-fill: " + TEXT_PRIMARY + ";");
        Region spacer = new Region();
        Label arrow = new Label("›");
        arrow.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");

        HBox row = new HBox(8, iconLbl, nameLbl, spacer, arrow);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(7, 10, 7, 10));
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.setStyle(
            "-fx-background-color: #1A2540;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: hand;"
        );
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #1E3050;" +
            "-fx-background-radius: 6; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-background-color: #1A2540;" +
            "-fx-background-radius: 6; -fx-cursor: hand;"));
        row.setOnMouseClicked(e -> onRecorrido(tipo));
        return row;
    }

    private Label etiquetaPequena(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        return l;
    }

    private Rectangle separador() {
        Rectangle r = new Rectangle();
        r.setHeight(1);
        r.setFill(Color.web("#1A2540"));
        r.widthProperty().bind(widthProperty());
        return r;
    }

    // =========================================================
    // ACCIONES
    // =========================================================

    private void onInsertar() {
        if (animando) return;
        int valor = parsearValor();
        if (valor == Integer.MIN_VALUE) return;

        boolean insertado = avlService.insertar(valor);
        if (!insertado) {
            mostrarAlerta("El valor " + valor + " ya existe en el árbol.");
            return;
        }

        treePanel.redibujar();
        animando = true;
        insertAnimation.animar(avlService.getRaiz(), valor, treePanel.getCanvasWidth(), treePanel.getCanvasHeight(), () -> {
            animando = false;
            infoPanel.actualizar();
            infoPanel.agregarHistorial("Insertar " + valor);
        });
        txtValor.clear();
    }

    private void onEliminar() {
        if (animando) return;
        int valor = parsearValor();
        if (valor == Integer.MIN_VALUE) return;

        if (avlService.buscar(valor) == null) {
            mostrarAlerta("El valor " + valor + " no existe en el árbol.");
            return;
        }

        animando = true;
        deleteAnimation.animar(valor, treePanel.getCanvasWidth(), treePanel.getCanvasHeight(), () -> {
            animando = false;
            treePanel.redibujar();
            infoPanel.actualizar();
            infoPanel.agregarHistorial("Eliminar " + valor);
        });
        txtValor.clear();
    }

    private void onBuscar() {
        if (animando) return;
        int valor = parsearValor();
        if (valor == Integer.MIN_VALUE) return;

        if (avlService.buscar(valor) == null) {
            mostrarAlerta("El valor " + valor + " no existe en el árbol.");
            return;
        }

        animando = true;
        treePanel.resaltarNodo(valor, com.avlsimulator.ui.components.NodeRenderer.COLOR_BUSCADO);
        infoPanel.agregarHistorial("Buscar " + valor + " — Encontrado");

        javafx.animation.Timeline tl = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(1400), e -> {
                treePanel.quitarResaltado(valor);
                animando = false;
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
        if (avlService.estaVacio()) {
            mostrarAlerta("El árbol está vacío.");
            return;
        }

        List<Integer> valores = avlService.ejecutarRecorrido(tipo);
        String nombre = avlService.getNombreRecorrido(tipo);
        infoPanel.mostrarRecorrido(nombre, valores);

        animando = true;
        traversalAnimation.animar(valores, () -> {
            animando = false;
            infoPanel.agregarHistorial(nombre);
        });
    }

    private int parsearValor() {
        try {
            return Integer.parseInt(txtValor.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta("Ingresa un número entero válido.");
            return Integer.MIN_VALUE;
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("AVL Simulator");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        // Estilo dark para el diálogo
        alert.getDialogPane().setStyle("-fx-background-color: #1E293B; -fx-text-fill: white;");
        alert.showAndWait();
    }
}