package com.avlsimulator.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Implementacion completa del arbol AVL.
 *
 * Criterio de eliminacion con dos hijos (definido por el profesor):
 * Se usa el MAYOR del subarbol IZQUIERDO como nodo reemplazo.
 *
 * Operaciones:
 *   - insertar(valor)
 *   - eliminar(valor)
 *   - buscar(valor)
 *   - inOrder(), preOrder(), postOrder(), bfs()
 */
public class AVLTree {

    private AVLNode raiz;

    public AVLTree() {
        this.raiz = null;
    }

    // =========================================================
    // UTILIDADES INTERNAS
    // =========================================================

    private int altura(AVLNode nodo) {
        return (nodo == null) ? -1 : nodo.altura;
    }

    private void actualizarAltura(AVLNode nodo) {
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));
    }

    private int getBalance(AVLNode nodo) {
        return (nodo == null) ? 0 : altura(nodo.izquierdo) - altura(nodo.derecho);
    }

    // =========================================================
    // ROTACIONES
    // =========================================================

    /**
     * Rotacion simple a la derecha — Caso LL.
     */
    private AVLNode rotarDerecha(AVLNode C) {
        AVLNode B  = C.izquierdo;
        AVLNode T2 = B.derecho;

        B.derecho   = C;
        C.izquierdo = T2;

        actualizarAltura(C);
        actualizarAltura(B);

        return B;
    }

    /**
     * Rotacion simple a la izquierda — Caso RR.
     */
    private AVLNode rotarIzquierda(AVLNode A) {
        AVLNode B  = A.derecho;
        AVLNode T2 = B.izquierdo;

        B.izquierdo = A;
        A.derecho   = T2;

        actualizarAltura(A);
        actualizarAltura(B);

        return B;
    }

    /**
     * Aplica la rotacion correcta segun el factor de balance.
     */
    private AVLNode rebalancear(AVLNode nodo) {
        actualizarAltura(nodo);
        int balance = getBalance(nodo);

        // Caso LL
        if (balance > 1 && getBalance(nodo.izquierdo) >= 0)
            return rotarDerecha(nodo);

        // Caso LR
        if (balance > 1 && getBalance(nodo.izquierdo) < 0) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }

        // Caso RR
        if (balance < -1 && getBalance(nodo.derecho) <= 0)
            return rotarIzquierda(nodo);

        // Caso RL
        if (balance < -1 && getBalance(nodo.derecho) > 0) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    // =========================================================
    // INSERCION
    // =========================================================

    public void insertar(int valor) {
        raiz = insertar(raiz, valor);
    }

    private AVLNode insertar(AVLNode nodo, int valor) {
        if (nodo == null)
            return new AVLNode(valor);

        if (valor < nodo.valor)
            nodo.izquierdo = insertar(nodo.izquierdo, valor);
        else if (valor > nodo.valor)
            nodo.derecho = insertar(nodo.derecho, valor);
        else
            return nodo; // duplicados no permitidos

        return rebalancear(nodo);
    }

    // =========================================================
    // ELIMINACION
    // =========================================================

    public void eliminar(int valor) {
        raiz = eliminar(raiz, valor);
    }

    private AVLNode eliminar(AVLNode nodo, int valor) {
        if (nodo == null)
            return null;

        if (valor < nodo.valor)
            nodo.izquierdo = eliminar(nodo.izquierdo, valor);
        else if (valor > nodo.valor)
            nodo.derecho = eliminar(nodo.derecho, valor);
        else {
            // Nodo encontrado

            // Caso 1: sin hijos o un solo hijo
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho   == null) return nodo.izquierdo;

            // Caso 2: dos hijos
            // Criterio del profesor: usar el MAYOR del subarbol IZQUIERDO
            AVLNode mayor  = getMayorNodo(nodo.izquierdo);
            nodo.valor     = mayor.valor;
            nodo.izquierdo = eliminar(nodo.izquierdo, mayor.valor);
        }

        return rebalancear(nodo);
    }

    /**
     * Retorna el nodo con el mayor valor en el subarbol dado.
     * Es el nodo mas a la derecha del subarbol.
     */
    private AVLNode getMayorNodo(AVLNode nodo) {
        while (nodo.derecho != null)
            nodo = nodo.derecho;
        return nodo;
    }

    // =========================================================
    // BUSQUEDA
    // =========================================================

    public boolean buscar(int valor) {
        return buscar(raiz, valor) != null;
    }

    public AVLNode buscarNodo(int valor) {
        return buscar(raiz, valor);
    }

    private AVLNode buscar(AVLNode nodo, int valor) {
        if (nodo == null)        return null;
        if (valor == nodo.valor) return nodo;
        if (valor < nodo.valor)  return buscar(nodo.izquierdo, valor);
        return buscar(nodo.derecho, valor);
    }

    // =========================================================
    // RECORRIDOS
    // =========================================================

    public List<Integer> inOrder() {
        List<Integer> resultado = new ArrayList<>();
        inOrder(raiz, resultado);
        return resultado;
    }

    private void inOrder(AVLNode nodo, List<Integer> resultado) {
        if (nodo == null) return;
        inOrder(nodo.izquierdo, resultado);
        resultado.add(nodo.valor);
        inOrder(nodo.derecho, resultado);
    }

    public List<Integer> preOrder() {
        List<Integer> resultado = new ArrayList<>();
        preOrder(raiz, resultado);
        return resultado;
    }

    private void preOrder(AVLNode nodo, List<Integer> resultado) {
        if (nodo == null) return;
        resultado.add(nodo.valor);
        preOrder(nodo.izquierdo, resultado);
        preOrder(nodo.derecho, resultado);
    }

    public List<Integer> postOrder() {
        List<Integer> resultado = new ArrayList<>();
        postOrder(raiz, resultado);
        return resultado;
    }

    private void postOrder(AVLNode nodo, List<Integer> resultado) {
        if (nodo == null) return;
        postOrder(nodo.izquierdo, resultado);
        postOrder(nodo.derecho, resultado);
        resultado.add(nodo.valor);
    }

    public List<Integer> bfs() {
        List<Integer> resultado = new ArrayList<>();
        if (raiz == null) return resultado;

        Queue<AVLNode> cola = new LinkedList<>();
        cola.add(raiz);

        while (!cola.isEmpty()) {
            AVLNode actual = cola.poll();
            resultado.add(actual.valor);
            if (actual.izquierdo != null) cola.add(actual.izquierdo);
            if (actual.derecho   != null) cola.add(actual.derecho);
        }

        return resultado;
    }

    // =========================================================
    // INFORMACION
    // =========================================================

    public AVLNode getRaiz()   { return raiz; }
    public boolean estaVacio() { return raiz == null; }
    public int getAltura()     { return altura(raiz); }

    public int getTotalNodos() {
        return contarNodos(raiz);
    }

    private int contarNodos(AVLNode nodo) {
        if (nodo == null) return 0;
        return 1 + contarNodos(nodo.izquierdo) + contarNodos(nodo.derecho);
    }

    public void limpiar() {
        raiz = null;
    }
}