package com.avlsimulator.util;

import com.avlsimulator.model.AVLNode;

/**
 * Calcula las coordenadas X,Y de cada nodo del arbol.
 * El espaciado y el radio se ajustan dinamicamente
 * segun el numero de nodos para evitar superposiciones.
 */
public class TreeLayoutCalculator {

    public static final double RADIO_MAX     = 24.0;
    public static final double RADIO_MIN     = 12.0;
    private static final double MARGEN       = 40.0;

    private int contadorX;
    private int totalNodos;
    private double radioActual;

    /**
     * Calcula y asigna las coordenadas X,Y a todos los nodos.
     */
    public void calcular(AVLNode raiz, double ancho, double alto) {
        if (raiz == null) return;

        contadorX  = 0;
        totalNodos = contarNodos(raiz);

        // Radio dinamico segun cantidad de nodos
        radioActual = calcularRadio(totalNodos);

        // Espaciado horizontal dinamico
        double espacioH = calcularEspacioHorizontal(ancho, totalNodos, radioActual);

        // Espaciado vertical dinamico
        double espacioV = calcularEspacioVertical(alto, raiz, radioActual);

        // Paso 1: asignar X con inOrder
        asignarX(raiz, espacioH);

        // Paso 2: centrar en el canvas
        double minX       = encontrarMinX(raiz);
        double maxX       = encontrarMaxX(raiz);
        double anchoArbol = maxX - minX;
        double offsetX    = (ancho - anchoArbol) / 2.0 - minX;

        // Paso 3: asignar Y y aplicar offset X
        double inicioY = espacioV + radioActual;
        asignarYyOffset(raiz, 0, offsetX, inicioY, espacioV);

        // Paso 4: escalar si aun se sale
        ajustarBordes(raiz, ancho, alto);
    }

    private double calcularRadio(int nodos) {
        if (nodos <= 7)  return RADIO_MAX;
        if (nodos <= 15) return 20.0;
        if (nodos <= 25) return 16.0;
        return RADIO_MIN;
    }

    private double calcularEspacioHorizontal(double ancho, int nodos, double radio) {
        if (nodos <= 1) return ancho / 2.0;
        double espacio = (ancho - MARGEN * 2) / nodos;
        double minEspacio = radio * 2 + 4;
        double maxEspacio = radio * 4;
        return Math.max(minEspacio, Math.min(maxEspacio, espacio));
    }

    private double calcularEspacioVertical(double alto, AVLNode raiz, double radio) {
        int altura = obtenerAltura(raiz);
        if (altura <= 0) return 80.0;
        double espacio = (alto - MARGEN * 2) / (altura + 1);
        double minEspacio = radio * 2 + 20;
        return Math.max(minEspacio, Math.min(90.0, espacio));
    }

    private int obtenerAltura(AVLNode nodo) {
        if (nodo == null) return -1;
        return nodo.altura;
    }

    private void asignarX(AVLNode nodo, double espacioH) {
        if (nodo == null) return;
        asignarX(nodo.izquierdo, espacioH);
        nodo.x = contadorX * espacioH;
        contadorX++;
        asignarX(nodo.derecho, espacioH);
    }

    private void asignarYyOffset(AVLNode nodo, int nivel, double offsetX,
                                  double inicioY, double espacioV) {
        if (nodo == null) return;
        nodo.x += offsetX;
        nodo.y  = inicioY + nivel * espacioV;
        asignarYyOffset(nodo.izquierdo, nivel + 1, offsetX, inicioY, espacioV);
        asignarYyOffset(nodo.derecho,   nivel + 1, offsetX, inicioY, espacioV);
    }

    private void ajustarBordes(AVLNode raiz, double ancho, double alto) {
        double minX      = encontrarMinX(raiz);
        double maxX      = encontrarMaxX(raiz);
        double maxY      = encontrarMaxY(raiz);
        double anchoArbol = maxX - minX;

        if (anchoArbol + MARGEN * 2 > ancho) {
            double escala = (ancho - MARGEN * 2) / anchoArbol;
            escalarX(raiz, minX, escala, MARGEN);
        }

        if (maxY + MARGEN > alto) {
            double escala = (alto - MARGEN * 2) / maxY;
            escalarY(raiz, escala);
        }
    }

    private void escalarX(AVLNode nodo, double minX, double escala, double margen) {
        if (nodo == null) return;
        nodo.x = (nodo.x - minX) * escala + margen;
        escalarX(nodo.izquierdo, minX, escala, margen);
        escalarX(nodo.derecho,   minX, escala, margen);
    }

    private void escalarY(AVLNode nodo, double escala) {
        if (nodo == null) return;
        nodo.y = nodo.y * escala;
        escalarY(nodo.izquierdo, escala);
        escalarY(nodo.derecho,   escala);
    }

    private double encontrarMinX(AVLNode nodo) {
        if (nodo == null) return Double.MAX_VALUE;
        return Math.min(nodo.x, Math.min(
            encontrarMinX(nodo.izquierdo),
            encontrarMinX(nodo.derecho)
        ));
    }

    private double encontrarMaxX(AVLNode nodo) {
        if (nodo == null) return Double.MIN_VALUE;
        return Math.max(nodo.x, Math.max(
            encontrarMaxX(nodo.izquierdo),
            encontrarMaxX(nodo.derecho)
        ));
    }

    private double encontrarMaxY(AVLNode nodo) {
        if (nodo == null) return 0;
        return Math.max(nodo.y, Math.max(
            encontrarMaxY(nodo.izquierdo),
            encontrarMaxY(nodo.derecho)
        ));
    }

    private int contarNodos(AVLNode nodo) {
        if (nodo == null) return 0;
        return 1 + contarNodos(nodo.izquierdo) + contarNodos(nodo.derecho);
    }

    public double getRadioActual() {
        return radioActual > 0 ? radioActual : RADIO_MAX;
    }
}