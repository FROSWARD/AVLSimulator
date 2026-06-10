package com.avlsimulator.service;

import com.avlsimulator.model.AVLTree;

import java.util.List;

/**
 * Servicio que expone los recorridos del arbol AVL.
 * Cada metodo retorna la lista de valores en el orden
 * del recorrido para que la capa de animacion los use.
 */
public class TraversalService {

    private final AVLTree arbol;

    public TraversalService(AVLTree arbol) {
        this.arbol = arbol;
    }

    /**
     * InOrder: Izquierda -> Raiz -> Derecha
     * Produce los valores en orden ascendente.
     */
    public List<Integer> inOrder() {
        return arbol.inOrder();
    }

    /**
     * PreOrder: Raiz -> Izquierda -> Derecha
     * Util para copiar o serializar el arbol.
     */
    public List<Integer> preOrder() {
        return arbol.preOrder();
    }

    /**
     * PostOrder: Izquierda -> Derecha -> Raiz
     * Util para eliminar el arbol sin perder referencias.
     */
    public List<Integer> postOrder() {
        return arbol.postOrder();
    }

    /**
     * BFS: Recorrido por niveles de izquierda a derecha.
     * Usa una cola internamente.
     */
    public List<Integer> bfs() {
        return arbol.bfs();
    }

    /**
     * Retorna el nombre del recorrido como String legible.
     * Util para mostrar en el panel de informacion.
     */
    public String getNombreRecorrido(TipoRecorrido tipo) {
        return switch (tipo) {
            case INORDER   -> "InOrder (Izq -> Raiz -> Der)";
            case PREORDER  -> "PreOrder (Raiz -> Izq -> Der)";
            case POSTORDER -> "PostOrder (Izq -> Der -> Raiz)";
            case BFS       -> "BFS (Por niveles)";
        };
    }

    /**
     * Ejecuta el recorrido indicado y retorna la lista de valores.
     */
    public List<Integer> ejecutar(TipoRecorrido tipo) {
        return switch (tipo) {
            case INORDER   -> inOrder();
            case PREORDER  -> preOrder();
            case POSTORDER -> postOrder();
            case BFS       -> bfs();
        };
    }

    /**
     * Enum con los tipos de recorrido disponibles.
     */
    public enum TipoRecorrido {
        INORDER,
        PREORDER,
        POSTORDER,
        BFS
    }
}