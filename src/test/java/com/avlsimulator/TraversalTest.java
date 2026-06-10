package com.avlsimulator;

import com.avlsimulator.model.AVLTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para los recorridos del arbol AVL.
 */
class TraversalTest {

    private AVLTree arbol;

    @BeforeEach
    void setUp() {
        arbol = new AVLTree();
        // Arbol base: insertar 30, 20, 40, 10, 25
        // Estructura resultante (ya balanceada):
        //        30
        //       /  \
        //      20   40
        //     /  \
        //    10   25
        arbol.insertar(30);
        arbol.insertar(20);
        arbol.insertar(40);
        arbol.insertar(10);
        arbol.insertar(25);
    }

    @Test
    void inOrder_retornaOrdenAscendente() {
        List<Integer> resultado = arbol.inOrder();
        assertEquals(List.of(10, 20, 25, 30, 40), resultado);
    }

    @Test
    void preOrder_raizPrimero() {
        List<Integer> resultado = arbol.preOrder();
        // La raiz debe ser el primer elemento
        assertEquals(30, resultado.get(0));
        assertEquals(List.of(30, 20, 10, 25, 40), resultado);
    }

    @Test
    void postOrder_raizUltimo() {
        List<Integer> resultado = arbol.postOrder();
        // La raiz debe ser el ultimo elemento
        int ultimo = resultado.get(resultado.size() - 1);
        assertEquals(30, ultimo);
        assertEquals(List.of(10, 25, 20, 40, 30), resultado);
    }

    @Test
    void bfs_porNiveles() {
        List<Integer> resultado = arbol.bfs();
        // Nivel 0: 30 | Nivel 1: 20, 40 | Nivel 2: 10, 25
        assertEquals(List.of(30, 20, 40, 10, 25), resultado);
    }

    @Test
    void inOrder_arbolVacio_listaVacia() {
        AVLTree vacio = new AVLTree();
        assertTrue(vacio.inOrder().isEmpty());
    }

    @Test
    void bfs_arbolVacio_listaVacia() {
        AVLTree vacio = new AVLTree();
        assertTrue(vacio.bfs().isEmpty());
    }

    @Test
    void inOrder_unSoloNodo_retornaEseNodo() {
        AVLTree uno = new AVLTree();
        uno.insertar(42);
        assertEquals(List.of(42), uno.inOrder());
    }

    @Test
    void recorridos_mismoTamano() {
        int n = arbol.getTotalNodos();
        assertEquals(n, arbol.inOrder().size());
        assertEquals(n, arbol.preOrder().size());
        assertEquals(n, arbol.postOrder().size());
        assertEquals(n, arbol.bfs().size());
    }
}