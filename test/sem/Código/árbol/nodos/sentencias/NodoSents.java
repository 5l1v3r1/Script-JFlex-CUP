package árbol.nodos.sentencias;

import tabla.TablaSímbolos;
import árbol.Nodo;
import árbol.nodos.declaraciones.NodoVar;

/**
 * Nodo para almacenar todas las sentencias del bloque.
 *
 * <p>
 * Puede tener tantos hijos como sentencias haya en el bloque.<br>
 * Los tipos de hijo posibles son:
 * <ul>
 *      <li> asignación ({@link NodoAsigna})
 *      <li> bucle ({@link NodoBucle})
 *      <li> sent_condicional ({@link NodoIf})
 *      <li> entrada_salida ({@link Nodo_ES})
 *      <li> La palabra reservada "skip" ({@link NodoVar})
 * </ul>
 */
public class NodoSents extends Nodo {

    /**
     * Constructor.
     */
    public NodoSents () {

        super (null, "Sentencias");
    }

    /**
     * Añade una nueva sentencia.
     *
     * Los tipos de hijo posibles son:
     * <ul>
     *      <li> asignación ({@link NodoAsigna})
     *      <li> bucle ({@link NodoBucle})
     *      <li> sent_condicional ({@link NodoCond})
     *      <li> entrada_salida ({@link Nodo_ES})
     * </ul>
     *
     *
     * @param sentencia
     *              El nodo a añadir.
     *
     *
     * @return
     *              <i>true</i> si el nodo era de uno de los tipos aceptados
     *          (y, por tanto, se ha añadido a la lista); o <i>false</i> si el
     *          tipo de nodo no se ha aceptado.
     */
    public boolean añadirSentencia (Nodo sentencia) {

        if (!(sentencia instanceof NodoE_S)
            && !(sentencia instanceof NodoBucle)
            && !(sentencia instanceof NodoIf)
            && !(sentencia instanceof NodoAsigna)
            && !(sentencia instanceof NodoVar)
            ) {

            return false;
        } else {

            añadirHijo (sentencia);
            return true;
        }
    }

    /**
     * Método auxiliar para ir recorriendo los nodos por niveles.
     *
     * <p>
     * Basado en
     * <a href="https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram#8948691">
     * una respuesta en StackOverflow</a>
     */
    @Override
    public void print (String prefijo, boolean esHoja) {

        System.out.println (prefijo + (esHoja ? "└── " : "├── ") + toString ());

        for (int i = verHijos ().size () - 1; i > 0; i--) {

            verHijos ().get (i).print(prefijo + (esHoja ? "    " : "│   "), false);
        }

        if (verHijos ().size() > 0) {

            verHijos ().get (0)
                    .print(prefijo + (esHoja ?"    " : "│   "), true);
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

        /* Realiza la comprobación de todos los hijos desde  atrás hacia
        adelante (como una pila) */
        for (int i = (verHijos().size() - 1); i >= 0; i--) {

            verHijos ().get (i).comprobarNodo (tabla);
        }
    }
}
