<!--
   Estimado programador,

   Cuando escribí este código, solo Dios, Claude y yo sabíamos cómo funcionaba.
   Ahora, solo Dios sabe.

   Por lo tanto, si estás intentando optimizar esta rutina y falla (muy probable),
   por favor aumenta este contador como advertencia para la siguiente persona:

   total_horas_perdidas_aqui = 6

   ░░░░░░░░░░░░░░░░░
   ░░░█████████████░░
   ░░█████████████████░
   ░█████████████████████░
   ░██░░░░█████░░░░░██░░░
   ░██░██░█████░██░░██░░░
   ░█████████████████████░
   ░░░████░░░░░████░░░░░
   ░░░███████████░░░░░░
   ░░░░░░░█████░░░░░░░░
   ░░░░░░░░░░░░░░░░░░░
-->

# AVL Simulator

Simulador interactivo de árboles AVL con animaciones en tiempo real, desarrollado como proyecto final para la asignatura de **Estructura de Datos II**.

---

## Descripción

AVL Simulator es una aplicación de escritorio que permite visualizar y comprender el funcionamiento de los árboles AVL (Adelson-Velsky and Landis) mediante animaciones paso a paso. El usuario puede insertar, eliminar y buscar nodos, observando cómo el árbol se autobalancea automáticamente mediante rotaciones.

---

## Estado actual — v2.0.0

**Mejoras v2.0 (UI/UX renovado):**

- **Splash screen** animado con logo, barra de carga y fade-in/out al iniciar
- **Ícono de aplicación y .exe** — logo integrado en la ventana y en el ejecutable Windows
- **ControlPanel rediseñado** — header con logo/subtítulo, secciones colapsadas, botones con color semántico, recorridos como lista interactiva con flecha
- **TreePanel renovado** — header con badge "Balanceado ✓", contador de nodos en vivo, toolbar de zoom (＋ / － / ↺)
- **InfoPanel renovado** — 4 tarjetas de métricas (Altura, Nodos, Balance, Última op), historial con puntos de color por tipo de operación
- **NodeRenderer mejorado** — degradado radial en nodos, brillo superior, factor de balance con color semafórico (verde=0, amarillo=±1, rojo=desbalance)
- **Paleta unificada** — dark navy `#080E1C` + azul `#3B82F6` + púrpura `#7C3AED`
- **styles.css completo** — scrollbars, slider, textfield focus, tooltips, diálogos en dark mode

**Completado en v1.1.0:**
- Scroll horizontal y vertical en el canvas
- Radio de nodos dinámico según cantidad de nodos
- Tooltip con información detallada al pasar el mouse

---

## Funcionalidades

- **Inserción** de nodos con animación paso a paso
- **Eliminación** de nodos con rebalanceo visual
- **Búsqueda** de nodos con resaltado en pantalla
- **Rotaciones automáticas** — LL, RR, LR, RL animadas
- **Recorridos animados** — InOrder, PreOrder, PostOrder y BFS
- **Panel informativo** — altura, nodos, balance, última operación, historial completo
- **Zoom** — acercar/alejar el árbol con botones en el header
- **Splash screen** con logo al iniciar

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Interfaz gráfica | JavaFX 21 |
| Animaciones | JavaFX Animation API (Timeline, Transitions) |
| Build | Maven |
| Pruebas | JUnit 5 |
| Ejecutable | jpackage → `.exe` nativo Windows |

---

## Estructura del Proyecto

```
AVLSimulator/
├── src/
│   ├── main/
│   │   ├── java/com/avlsimulator/
│   │   │   ├── model/
│   │   │   │   ├── AVLNode.java
│   │   │   │   └── AVLTree.java
│   │   │   ├── service/
│   │   │   │   ├── AVLService.java
│   │   │   │   └── TraversalService.java
│   │   │   ├── ui/
│   │   │   │   ├── MainApp.java              # SplashScreen + ventana principal
│   │   │   │   ├── panels/
│   │   │   │   │   ├── TreePanel.java        # Canvas + header + zoom
│   │   │   │   │   ├── ControlPanel.java     # Sidebar renovado
│   │   │   │   │   └── InfoPanel.java        # Tarjetas de métricas + historial
│   │   │   │   ├── components/
│   │   │   │   │   └── NodeRenderer.java     # Nodos con degradado
│   │   │   │   └── animations/
│   │   │   │       ├── InsertAnimation.java
│   │   │   │       ├── DeleteAnimation.java
│   │   │   │       ├── RotationAnimation.java
│   │   │   │       └── TraversalAnimation.java
│   │   │   └── util/
│   │   │       └── TreeLayoutCalculator.java
│   │   └── resources/
│   │       ├── styles.css                    # Dark theme completo
│   │       ├── logo.png                      # Logo para ventana y splash
│   │       └── logo.ico                      # Ícono para ejecutable Windows
│   └── test/
│       └── java/com/avlsimulator/
│           ├── AVLTreeTest.java
│           └── TraversalTest.java
├── docs/
├── scripts/
└── pom.xml
```

---

## Cómo ejecutar

### Requisitos
- Java 17+ (recomendado Java 21)
- Maven 3.8+

### Compilar y ejecutar
```bash
mvn clean javafx:run
```

### Generar ejecutable Windows (.exe)
```bash
mvn clean package
jpackage --input target/ \
         --name AVLSimulator \
         --main-jar AVLSimulator-2.0.0.jar \
         --main-class com.avlsimulator.ui.MainApp \
         --type exe \
         --icon src/main/resources/logo.ico \
         --win-shortcut \
         --win-menu \
         --app-version 2.0.0 \
         --vendor "FROSWARD"
```

---

## Rotaciones AVL

| Caso | Condición | Solución |
|---|---|---|
| LL | Balance > 1, hijo izquierdo | Rotación simple derecha |
| RR | Balance < -1, hijo derecho | Rotación simple izquierda |
| LR | Balance > 1, hijo derecho | Rotación doble izquierda-derecha |
| RL | Balance < -1, hijo izquierdo | Rotación doble derecha-izquierda |

---

## Recorridos

- **InOrder** (Izquierda → Raíz → Derecha) — produce nodos en orden ascendente
- **PreOrder** (Raíz → Izquierda → Derecha) — útil para copiar el árbol
- **PostOrder** (Izquierda → Derecha → Raíz) — útil para eliminar el árbol
- **BFS** (nivel por nivel) — recorrido por amplitud

---

## Autor

**FROSWARD**
Proyecto Final — Estructura de Datos II
