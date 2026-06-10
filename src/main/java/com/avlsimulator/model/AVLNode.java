package com.avlsimulator.model;

/**
 * Representa un nodo del arbol AVL.
 * Cada nodo almacena su valor, altura, factor de balance
 * y referencias a sus hijos izquierdo y derecho.
 */
public class AVLNode {

    public int valor;
    public int altura;
    public AVLNode izquierdo;
    public AVLNode derecho;

    // Coordenadas para dibujar el nodo en el canvas
    public double x;
    public double y;

    public AVLNode(int valor) {
        this.valor = valor;
        this.altura = 0;
        this.izquierdo = null;
        this.derecho = null;
        this.x = 0;
        this.y = 0;
    }

    /**
     * Retorna el factor de balance de este nodo.
     * FB = altura(izquierdo) - altura(derecho)
     */
    public int getFactorBalance() {
        int altIzq = (izquierdo != null) ? izquierdo.altura : -1;
        int altDer = (derecho != null) ? derecho.altura : -1;
        return altIzq - altDer;
    }

    @Override
    public String toString() {
        return "Nodo(" + valor + ", h=" + altura + ", FB=" + getFactorBalance() + ")";
    }
}