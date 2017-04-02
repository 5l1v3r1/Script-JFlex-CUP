package árbol;

import tabla.TablaSímbolos;
import árbol.nodos.NodoProg;

public class Árbol {

    /**
     * Nodo a partir del cual heredarán el resto.
     */
    private final Nodo raíz;

    /**
     * Constructor.
     *
     * @param raíz
     *              Raíz del árbol.
     */
    public Árbol (NodoProg raíz) {

        this.raíz = raíz;
    }

    /**
     * Muestra por pantalla el árbol.
     */
    public void imprimir () {

        raíz.imprimir();
    }

    /**
     * Realiza las comprobaciones semánticas en el nodo, usando la información
     * de la tabla de símbolos.
     *
     * @param tabla
     *              Tabla de símbolos construida a lo largo del análisis
     *          sintáctico.
     */
    public void comprobarÁrbol (TablaSímbolos tabla) {

        raíz.comprobarNodo (tabla);
    }
}
