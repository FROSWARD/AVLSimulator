package com.avlsimulator.ui;

import com.avlsimulator.service.AVLService;
import com.avlsimulator.ui.panels.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainApp extends Application {

    private static final String VERSION = "2.2.0";

    @Override
    public void start(Stage stage) {
        mostrarSplash(stage);
    }

    // ── Splash ───────────────────────────────────────────────

    private void mostrarSplash(Stage mainStage) {
        Stage splash = new Stage(StageStyle.TRANSPARENT);
        splash.setAlwaysOnTop(true);

        StackPane root = new StackPane();
        root.setPrefSize(480, 320);

        Rectangle bg = new Rectangle(480, 320,
            new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0B0F1A")),
                new Stop(1, Color.web("#0E1528"))));
        bg.setArcWidth(16); bg.setArcHeight(16);

        Rectangle border = new Rectangle(480, 320);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.web("#1A3A6B", 0.7));
        border.setStrokeWidth(1.5);
        border.setArcWidth(16); border.setArcHeight(16);

        ImageView logo = new ImageView();
        try { logo.setImage(new Image(getClass().getResourceAsStream("/logo.png"))); }
        catch (Exception ignored) {}
        logo.setFitWidth(110); logo.setFitHeight(110);
        logo.setPreserveRatio(true);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#3B82F6", 0.85));
        glow.setRadius(32); glow.setSpread(0.3);
        logo.setEffect(glow);

        Label titulo = new Label("AVL Simulator");
        titulo.setStyle("-fx-font-family:'Segoe UI',Arial;-fx-font-size:30px;" +
                        "-fx-font-weight:bold;-fx-text-fill:white;");
        Label sub = new Label("Visualiza y comprende árboles AVL");
        sub.setStyle("-fx-font-size:13px;-fx-text-fill:#5B8FD6;");
        Label ver = new Label("v" + VERSION + "  ·  Estructura de Datos II");
        ver.setStyle("-fx-font-size:11px;-fx-text-fill:#3A5A9A;");

        Rectangle barBg = new Rectangle(260, 4, Color.web("#1A3A6B", 0.5));
        barBg.setArcWidth(4); barBg.setArcHeight(4);
        Rectangle bar = new Rectangle(0, 4,
            new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#3B82F6")),
                new Stop(1, Color.web("#7C3AED"))));
        bar.setArcWidth(4); bar.setArcHeight(4);

        StackPane barWrap = new StackPane(barBg, bar);
        barWrap.setAlignment(Pos.CENTER_LEFT);
        barWrap.setMaxWidth(260);

        VBox content = new VBox(10, logo, titulo, sub, ver, barWrap);
        content.setAlignment(Pos.CENTER);
        root.getChildren().addAll(bg, content, border);

        Scene scene = new Scene(root, Color.TRANSPARENT);
        splash.setScene(scene);
        splash.centerOnScreen();
        splash.show();

        // Animaciones — sin anonymous subclasses sobre clases final
        FadeTransition fadeIn = new FadeTransition(Duration.millis(450), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        Timeline loadBar = new Timeline(
            new KeyFrame(Duration.millis(0),    new KeyValue(bar.widthProperty(), 0)),
            new KeyFrame(Duration.millis(1200), new KeyValue(bar.widthProperty(), 260, Interpolator.EASE_BOTH))
        );

        ScaleTransition pulse = new ScaleTransition(Duration.millis(700), logo);
        pulse.setFromX(0.85); pulse.setFromY(0.85);
        pulse.setToX(1.0);    pulse.setToY(1.0);
        pulse.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition intro = new ParallelTransition(fadeIn, pulse, loadBar);
        intro.setOnFinished(e -> {
            PauseTransition pausa = new PauseTransition(Duration.millis(250));
            pausa.setOnFinished(ev -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(350), root);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(done -> {
                    splash.close();
                    lanzarMain(mainStage);
                });
                fadeOut.play();
            });
            pausa.play();
        });
        intro.play();
    }

    // ── Ventana principal ─────────────────────────────────────

    private void lanzarMain(Stage stage) {
        AVLService  svc     = new AVLService();
        InfoPanel   info    = new InfoPanel(svc);
        InfoOverlay overlay = new InfoOverlay(info);
        TreePanel   tree    = new TreePanel(svc);
        TopBar      topBar  = new TopBar(svc, tree, info, overlay);

        // Árbol + overlay superpuesto a la derecha
        StackPane center = new StackPane(tree, overlay);
        StackPane.setAlignment(overlay, Pos.CENTER_RIGHT);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(center);
        root.setStyle("-fx-background-color: #080E1C;");

        Scene scene = new Scene(root, 1200, 740);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        try { stage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png"))); }
        catch (Exception ignored) {}

        stage.setTitle("AVL Simulator v" + VERSION);
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(580);

        root.setOpacity(0);
        stage.show();

        FadeTransition ft = new FadeTransition(Duration.millis(350), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        tree.redibujar();
    }

    public static void main(String[] args) { launch(args); }
}
