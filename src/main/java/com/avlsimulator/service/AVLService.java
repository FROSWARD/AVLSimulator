package com.avlsimulator.service;

import com.avlsimulator.model.AVLNode;
import com.avlsimulator.model.AVLTree;
import com.avlsimulator.service.TraversalService.TipoRecorrido;

import java.util.List;

/**
 * Fachada principal entre la UI y la logica del arbol.
 * Todos los paneles interactuan con este servicio,
 * nunca directamente con AVLTree.
 */
public class AVLService {

    private final AVLTree arbol;
    private final TraversalService traversalService;

    public AVLService() {
        this.arbol            = new AVLTree();
        this.traversalService = new TraversalService(arbol);
    }

    // =========================================================
    // OPERACIONES PRINCIPALES
    // =========================================================

    /**
     * Inserta un valor en el arbol.
     * Retorna true si se inserto correctamente,
     * false si el valor ya existe.
     */
    public boolean insertar(int valor) {
        if (arbol.buscar(valor)) return false;
        arbol.insertar(valor);
        return true;
    }

    /**
     * Elimina un valor del arbol.
     * Retorna true si se elimino correctamente,
     * false si el valor no existia.
     */
    public boolean eliminar(int valor) {
        if (!arbol.buscar(valor)) return false;
        arbol.eliminar(valor);
        return true;
    }

    /**
     * Busca un valor en el arbol.
     * Retorna el nodo si existe, null si no.
     */
    public AVLNode buscar(int valor) {
        return arbol.buscarNodo(valor);
    }

    /**
     * Vacia el arbol completamente.
     */
    public void limpiar() {
        arbol.limpiar();
    }

    // =========================================================
    // RECORRIDOS
    // =========================================================

    public List<Integer> ejecutarRecorrido(TipoRecorrido tipo) {
        return traversalService.ejecutar(tipo);
    }

    public String getNombreRecorrido(TipoRecorrido tipo) {
        return traversalService.getNombreRecorrido(tipo);
    }

    // =========================================================
    // INFORMACION DEL ARBOL
    // =========================================================

    public AVLNode getRaiz() {
        return arbol.getRaiz();
    }

    public boolean estaVacio() {
        return arbol.estaVacio();
    }

    public int getAltura() {
        return arbol.getAltura();
    }

    public int getTotalNodos() {
        return arbol.getTotalNodos();
    }
}