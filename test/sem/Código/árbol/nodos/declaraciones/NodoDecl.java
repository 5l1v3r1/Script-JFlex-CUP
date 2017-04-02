package árbol.nodos.declaraciones;

import tabla.TablaSímbolos;
import árbol.Nodo;

/**
 * Nodo que almacenará las declaraciones de las variables.
 *
 * Este nodo puede tener tantos hijos como declaraciones (sentencias) diferentes
 * haya.
 */
public final class NodoDecl extends Nodo {

    /**
     * Constructor.
     */
    public NodoDecl () {

        super (null, "Declaraciones");
    }

    /**
     * Añade un nuevo nodo para el tipo de variable
     *
     * @param declaraciones
     *              Nodo que almacena las variables declaradas.
     */
    public void añadirTipo (NodoTipo declaraciones) {

        if (declaraciones != null) {

            añadirHijo (declaraciones);
        }
    }

    /**
     * Realiza el análisis semántico del nodo actual y todos sus hijos,
     * empezando desde abajo (primero los hijos) y mostrando todos los errores
     * semánticos encontrados.
     *
     * @param tabla
     *              Tabla de símbolos construida a lo largo del análisis
     *          sintáctico.
     */
    @Override
    public void comprobarNodo (TablaSímbolos tabla) {

        /* Pasa por todos los hijos comprobando las variables */
        for (Nodo hijo : verHijos()) {

            hijo.comprobarNodo (tabla);
        }
    }
}
