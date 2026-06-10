package com.avlsimulator;

import com.avlsimulator.model.AVLTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para AVLTree.
 * Verifica insercion, eliminacion, rotaciones y balance.
 */
class AVLTreeTest {

    private AVLTree arbol;

    @BeforeEach
    void setUp() {
        arbol = new AVLTree();
    }

    // =========================================================
    // INSERCION
    // =========================================================

    @Test
    void insertar_arbolVacio_raizCorrecta() {
        arbol.insertar(10);
        assertEquals(10, arbol.getRaiz().valor);
    }

    @Test
    void insertar_rotacionLL_balanceoCorrecto() {
        // Insertar 30, 20, 10 debe disparar rotacion LL
        // Resultado esperado: raiz = 20, izq = 10, der = 30
        arbol.insertar(30);
        arbol.insertar(20);
        arbol.insertar(10);

        assertEquals(20, arbol.getRaiz().valor);
        assertEquals(10, arbol.getRaiz().izquierdo.valor);
        assertEquals(30, arbol.getRaiz().derecho.valor);
        assertEquals(0,  arbol.getRaiz().getFactorBalance());
    }

    @Test
    void insertar_rotacionRR_balanceoCorrecto() {
        // Insertar 10, 20, 30 debe disparar rotacion RR
        // Resultado esperado: raiz = 20, izq = 10, der = 30
        arbol.insertar(10);
        arbol.insertar(20);
        arbol.insertar(30);

        assertEquals(20, arbol.getRaiz().valor);
        assertEquals(10, arbol.getRaiz().izquierdo.valor);
        assertEquals(30, arbol.getRaiz().derecho.valor);
        assertEquals(0,  arbol.getRaiz().getFactorBalance());
    }

    @Test
    void insertar_rotacionLR_balanceoCorrecto() {
        // Insertar 30, 10, 20 debe disparar rotacion LR
        // Resultado esperado: raiz = 20
        arbol.insertar(30);
        arbol.insertar(10);
        arbol.insertar(20);

        assertEquals(20, arbol.getRaiz().valor);
        assertEquals(10, arbol.getRaiz().izquierdo.valor);
        assertEquals(30, arbol.getRaiz().derecho.valor);
        assertEquals(0,  arbol.getRaiz().getFactorBalance());
    }

    @Test
    void insertar_rotacionRL_balanceoCorrecto() {
        // Insertar 10, 30, 20 debe disparar rotacion RL
        // Resultado esperado: raiz = 20
        arbol.insertar(10);
        arbol.insertar(30);
        arbol.insertar(20);

        assertEquals(20, arbol.getRaiz().valor);
        assertEquals(10, arbol.getRaiz().izquierdo.valor);
        assertEquals(30, arbol.getRaiz().derecho.valor);
        assertEquals(0,  arbol.getRaiz().getFactorBalance());
    }

    @Test
    void insertar_duplicado_noAgrega() {
        arbol.insertar(10);
        arbol.insertar(10);
        assertEquals(1, arbol.getTotalNodos());
    }

    @Test
    void insertar_multiples_factorBalanceSiempreValido() {
        int[] valores = {50, 30, 70, 20, 40, 60, 80, 10, 25};
        for (int v : valores) arbol.insertar(v);
        verificarBalanceTodo(arbol.getRaiz());
    }

    // =========================================================
    // ELIMINACION
    // =========================================================

    @Test
    void eliminar_nodoHoja_arbolCorrecto() {
        arbol.insertar(20);
        arbol.insertar(10);
        arbol.insertar(30);
        arbol.eliminar(10);

        assertFalse(arbol.buscar(10));
        assertEquals(2, arbol.getTotalNodos());
    }

    @Test
    void eliminar_nodoConUnHijo_arbolCorrecto() {
        arbol.insertar(20);
        arbol.insertar(10);
        arbol.insertar(30);
        arbol.insertar(5);
        arbol.eliminar(10);

        assertFalse(arbol.buscar(10));
        assertTrue(arbol.buscar(5));
        verificarBalanceTodo(arbol.getRaiz());
    }

    @Test
    void eliminar_nodoConDosHijos_usaMayorSubarbolDerecho() {
        // Arbol: 30 raiz, subarbol derecho: 40 con hijos 35 y 45
        // Eliminar 30: mayor del subarbol derecho es 45
        arbol.insertar(30);
        arbol.insertar(20);
        arbol.insertar(40);
        arbol.insertar(35);
        arbol.insertar(45);

        arbol.eliminar(30);

        assertFalse(arbol.buscar(30));
        verificarBalanceTodo(arbol.getRaiz());
    }

    @Test
    void eliminar_nodoInexistente_arbolSinCambios() {
        arbol.insertar(10);
        arbol.insertar(20);
        arbol.eliminar(99);
        assertEquals(2, arbol.getTotalNodos());
    }

    @Test
    void eliminar_raiz_arbolCorrecto() {
        arbol.insertar(20);
        arbol.insertar(10);
        arbol.insertar(30);
        arbol.eliminar(20);

        assertFalse(arbol.buscar(20));
        assertTrue(arbol.buscar(10));
        assertTrue(arbol.buscar(30));
        verificarBalanceTodo(arbol.getRaiz());
    }

    // =========================================================
    // BUSQUEDA
    // =========================================================

    @Test
    void buscar_valorExistente_retornaTrue() {
        arbol.insertar(15);
        assertTrue(arbol.buscar(15));
    }

    @Test
    void buscar_valorInexistente_retornaFalse() {
        arbol.insertar(15);
        assertFalse(arbol.buscar(99));
    }

    @Test
    void buscar_arbolVacio_retornaFalse() {
        assertFalse(arbol.buscar(10));
    }

    // =========================================================
    // INFORMACION
    // =========================================================

    @Test
    void altura_arbolVacio_menosUno() {
        assertEquals(-1, arbol.getAltura());
    }

    @Test
    void altura_unNodo_cero() {
        arbol.insertar(10);
        assertEquals(0, arbol.getAltura());
    }

    @Test
    void limpiar_arbolQuedaVacio() {
        arbol.insertar(10);
        arbol.insertar(20);
        arbol.limpiar();
        assertTrue(arbol.estaVacio());
        assertEquals(0, arbol.getTotalNodos());
    }

    // =========================================================
    // UTILIDAD: verifica que todos los nodos esten balanceados
    // =========================================================

    private void verificarBalanceTodo(com.avlsimulator.model.AVLNode nodo) {
        if (nodo == null) return;
        int fb = nodo.getFactorBalance();
        assertTrue(fb >= -1 && fb <= 1,
            "Nodo " + nodo.valor + " tiene FB=" + fb + " (desbalanceado)");
        verificarBalanceTodo(nodo.izquierdo);
        verificarBalanceTodo(nodo.derecho);
    }
}