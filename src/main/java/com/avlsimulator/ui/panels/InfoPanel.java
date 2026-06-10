package com.avlsimulator.ui.panels;

import com.avlsimulator.service.AVLService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel de información v2.2 — drawer lateral derecho.
 * Métricas + último recorrido + historial.
 */
public class InfoPanel extends VBox {

    private static final String BG      = "#0D1528";
    private static final String BG_CARD = "#111827";
    private static final String BLUE    = "#3B82F6";
    private static final String PURPLE  = "#7C3AED";
    private static final String GREEN   = "#10B981";
    private static final String TEAL    = "#14B8A6";
    private static final String DIM     = "#64748B";
    private static final String MAIN    = "#F1F5F9";
    private static final String LABEL   = "#94A3B8";

    private final AVLService avlService;
    private final Label valAltura, valNodos, valBalance, valUltimaOp;
    private final Label lblRecorrido;
    private final VBox  listaHistorial;
    private final List<String> historial = new ArrayList<>();

    public InfoPanel(AVLService avlService) {
        this.avlService = avlService;

        setStyle("-fx-background-color: " + BG + ";");
        setSpacing(0);

        // ── Header del panel ─────────────────────────────────
        Label hdr = new Label("📊  INFORMACIÓN DEL ÁRBOL");
        hdr.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:"+MAIN+";");
        VBox hdrBox = new VBox(hdr);
        hdrBox.setPadding(new Insets(14, 14, 12, 14));
        hdrBox.setStyle("-fx-background-color:#111827;-fx-border-color:#1A2540;-fx-border-width:0 0 1 0;");

        // ── Métricas ─────────────────────────────────────────
        valAltura   = metricVal("0");
        valNodos    = metricVal("0");
        valBalance  = metricVal("AVL");
        valUltimaOp = metricVal("—");

        VBox metricas = new VBox(6,
            tarjeta("📐", "Altura",    valAltura,   BLUE),
            tarjeta("⬡",  "Nodos",     valNodos,    PURPLE),
            tarjeta("⚖",  "Balance",   valBalance,  GREEN),
            tarjeta("⏱",  "Última op", valUltimaOp, TEAL)
        );
        metricas.setPadding(new Insets(12, 12, 12, 12));

        // ── Recorrido ─────────────────────────────────────────
        Rectangle sep1 = sep();
        Label lblSecRec = secLabel("ÚLTIMO RECORRIDO");
        lblRecorrido = new Label("—");
        lblRecorrido.setStyle("-fx-font-size:11px;-fx-text-fill:"+LABEL+";");
        lblRecorrido.setWrapText(true);
        VBox secRec = new VBox(6, lblSecRec, lblRecorrido);
        secRec.setPadding(new Insets(10, 14, 10, 14));

        // ── Historial ─────────────────────────────────────────
        Rectangle sep2 = sep();
        Label lblSecHist = secLabel("HISTORIAL");
        listaHistorial = new VBox(4);

        ScrollPane scroll = new ScrollPane(listaHistorial);
        scroll.setStyle("-fx-background:transparent;-fx-background-color:transparent;");
        scroll.setFitToWidth(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox secHist = new VBox(6, lblSecHist, scroll);
        secHist.setPadding(new Insets(10, 14, 14, 14));
        VBox.setVgrow(secHist, Priority.ALWAYS);

        getChildren().addAll(hdrBox, metricas, sep1, secRec, sep2, secHist);
    }

    // ── API ──────────────────────────────────────────────────

    public void actualizar() {
        valAltura.setText(String.valueOf(avlService.getAltura()));
        valNodos.setText(String.valueOf(avlService.getTotalNodos()));
        valBalance.setText("AVL");
    }

    public void mostrarRecorrido(String nombre, List<Integer> valores) {
        String seq = valores.toString().replace("[","").replace("]","");
        lblRecorrido.setText(nombre + ":\n" + (seq.length()>90 ? seq.substring(0,87)+"…" : seq));
    }

    public void agregarHistorial(String msg) {
        valUltimaOp.setText(msg.length()>16 ? msg.substring(0,13)+"…" : msg);
        historial.add(0, msg);
        if (historial.size() > 60) historial.remove(historial.size()-1);
        refrescar();
    }

    // ── Helpers ──────────────────────────────────────────────

    private void refrescar() {
        listaHistorial.getChildren().clear();
        for (int i = 0; i < historial.size(); i++) {
            String item = historial.get(i);
            String dot  = item.startsWith("Insertar") ? "#10B981"
                        : item.startsWith("Eliminar") ? "#EF4444"
                        : item.startsWith("Buscar")   ? "#7C3AED" : DIM;
            Label d = new Label("●");
            d.setStyle("-fx-font-size:8px;-fx-text-fill:"+dot+";");
            Label l = new Label((i+1)+".  "+item);
            l.setStyle("-fx-font-size:11px;-fx-text-fill:"+(i==0?MAIN:DIM)+";");
            HBox row = new HBox(5, d, l);
            row.setAlignment(Pos.CENTER_LEFT);
            listaHistorial.getChildren().add(row);
        }
    }

    private HBox tarjeta(String ico, String lbl, Label valor, String accent) {
        Label icoL = new Label(ico);
        icoL.setStyle("-fx-font-size:14px;-fx-text-fill:"+accent+";-fx-min-width:20px;");
        Label lblL = new Label(lbl);
        lblL.setStyle("-fx-font-size:10px;-fx-text-fill:"+LABEL+";");
        valor.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:"+accent+";");
        VBox txt = new VBox(1, valor, lblL);
        HBox card = new HBox(10, icoL, txt);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(8, 10, 8, 10));
        card.setStyle("-fx-background-color:"+BG_CARD+";-fx-background-radius:7;" +
                      "-fx-border-color:#1E293B;-fx-border-width:1;-fx-border-radius:7;");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private Label metricVal(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:"+MAIN+";");
        return l;
    }

    private Label secLabel(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:10px;-fx-font-weight:bold;-fx-text-fill:"+DIM+";");
        return l;
    }

    private Rectangle sep() {
        Rectangle r = new Rectangle(1, 1);
        r.setFill(Color.web("#1A2540"));
        r.widthProperty().bind(widthProperty());
        return r;
    }
}
