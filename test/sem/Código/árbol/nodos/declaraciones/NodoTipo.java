package árbol.nodos.declaraciones;

import tabla.TablaSímbolos;
import tabla.TipoDato;
import árbol.Nodo;

/**
 * Nodo para almacenar un conjunto de variables de un tipo dado.
 *
 * <p>
 * Puede almacenar tantos {@link NodoVar} como variables haya declaradas en una
 * sentencia (todas son del mismo tipo).
 */
public class NodoTipo extends Nodo {

    /**
     * Tipo de dato de las variables que almacenará el nodo.
     */
    private final TipoDato tipo;

    /**
     * Constructor.
     *
     * @param tipo
     *              Tipo de dato de las variables que almacenará el nodo.
     */
    public NodoTipo (TipoDato tipo) {

        super (null, tipo.verNombre());

        this.tipo = tipo;
    }

    /**
     * Devuelve el tipo de dato de las variables que almacena este nodo.
     *
     * @return
     *              El valor de {@code tipo}.
     */
    public TipoDato verTipo () {

        return tipo;
    }

    /**
     * Añade una variable declarada.
     *
     * @param variable
     *              Un nodo con la información de la variable.
     */
    public void añadirVar (NodoVar variable) {

        /* Completa la información sobre la variable */
        variable.verInfo ().cambiarTipo (tipo);

        añadirHijo (variable);
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

        /* Comprueba que todas las variables están en la tabla de símbolos */
        for (Nodo hijo : verHijos()) {

            hijo.comprobarNodo (tabla);
        }
    }
}
