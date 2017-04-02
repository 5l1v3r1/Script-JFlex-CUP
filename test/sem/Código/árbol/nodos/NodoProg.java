package árbol.nodos;

import common.Logger;
import tabla.TablaSímbolos;
import tabla.TipoDato;
import árbol.nodos.declaraciones.NodoDecl;
import árbol.nodos.sentencias.NodoSents;
import árbol.Nodo;

/**
 * Nodo para la raíz del árbol.
 *
 * <p>
 * Sólo puede tener dos hijos:
 *  <ul>
 *      <li> Declaraciones ({@link NodoDecl})
 *      <li> Sentencias ({@link NodoSents})
 * </ul>
 */
public class NodoProg extends Nodo {

    /**
     * Nodo con las declaraciones de variables.
     */
    private NodoDecl declaraciones;

    /**
     * Nodo con las sentencias de código.
     */
    private NodoSents sentencias;

    /**
     * Constructor.
     *
     * @param nombre
     *              Nombre del programa.
     */
    public NodoProg (String nombre) {

        super (null, nombre);

        declaraciones = null;
        sentencias = null;
    }

    /**
     * Añade el bloque de declaraciones al nodo. Si este nodo ya tenía un hijo
     * con las declaraciones de variables, no hace nada.
     *
     * @param declaraciones
     *              El nodo que se va a añadir.
     *
     *
     * @return
     *             <i>true</i> si no había ya un nodo con el bloque de
     *          declaraciones; o <i>false</i> si ya existía un hijo así.
     */
    public boolean añadirBloqueDecl (NodoDecl declaraciones) {

        if (this.declaraciones == null) {

            this.declaraciones = declaraciones;
            añadirHijo (declaraciones);

            return true;
        } else {

            return false;
        }
    }

    /**
     * Añade el bloque de sentencias al nodo. Si este nodo ya tenía un hijo
     * con las sentencias, no hace nada.
     *
     * @param sentencias
     *              El nodo que se va a añadir.
     *
     *
     * @return
     *             <i>true</i> si no había ya un nodo con el bloque de
     *          sentencias; o <i>false</i> si ya existía un hijo así.
     */
    public boolean añadirBloqueSents (NodoSents sentencias) {

        if (this.sentencias == null) {

            this.sentencias = sentencias;
            añadirHijo (sentencias);

            return true;
        } else {

            return false;
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

        /* Crea una copia para no alterar el original */
        TablaSímbolos copia = tabla.obtenerCopia ();

        /* Comprueba que el nombre del programa no se haya usado como variable */
        if (tabla.buscarID (verID())) {

            Logger.error_sintáctico (verSímbolo(), "El nombre del programa, '"
                                    + verID() + "', no se puede usar como "
                                    + "variable.");

            /* Elimina la variable de la tabla e introduce una nueva entrada
            de tipo "pseudo" */
            tabla.eliminarID (verID());
            tabla.añadirID (verID (), TipoDato.pseudo);
        } else {

            tabla.añadirID (verID (), TipoDato.pseudo);
        }

        /* Realiza las comprobaciones de las variables y luego pasa al bloque de
        sentencias */
        declaraciones.comprobarNodo (copia);
        sentencias.comprobarNodo (copia);

        /* Actualiza los valores de la tabla original */
        tabla.actualizarTabla (copia);
        tabla.revisarDatos ();
    }
}
