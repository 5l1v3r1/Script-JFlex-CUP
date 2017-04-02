package árbol;

import java_cup.runtime.*;
import java.util.ArrayList;
import tabla.TablaSímbolos;

/**
 * Nodo para el árbol sintáctico.
 */
public abstract class Nodo {

    /**
     * Identificador
     */
    private String identificador;

    /**
     * Símbolo (token) del nodo actual.
     */
    private final Symbol símbolo;

    /**
     * Lista de hijos de este nodo.
     */
    private final ArrayList<Nodo> hijos;

    /**
     * Constructor.
     *
     * @param símbolo
     *              Token del nodo (puede ser {@code null}).
     *
     * @param identificador
     *              Cadena para identificar el nodo.
     */
    public Nodo (Symbol símbolo, String identificador) {

        this.identificador = identificador;

        hijos = new ArrayList<>();

        this.símbolo = símbolo;
    }

    /**
     * Añade un elemento a la lista de hijos.
     *
     * @param nodo
     *              Nuevo nodo hijo del actual.
     */
    public void añadirHijo (Nodo nodo) {

        hijos.add (nodo);
    }

    /**
     * Devuelve la lista con los hijos del nodo.
     *
     * @return
     *              El valor de {@link #hijos}.
     */
    public ArrayList <Nodo> verHijos () {

        return hijos;
    }

    /**
     * Devuelve el nombre identificador asignado al nodo.
     *
     * @return
     *              El valor de {@link #identificador}.
     */
    public String verID () {

        return this.identificador;
    }

    /**
     * Cambia la cadena que permite identificar el nodo.
     *
     * @param nuevoID
     *              Nueva cadena que identificará al nodo.
     */
    public void cambiarID (String nuevoID) {

        this.identificador = nuevoID;
    }

    /**
     * Permite saber si un nodo es hoja o no.
     *
     * @return
     *              <i>true</i> si es un nodo hoja; o <i>false</i> si no.
     */
    public boolean esHoja () {

        return hijos.isEmpty();
    }

    /**
     * Devuelve el símbolo almacenado en el nodo.
     *
     * @return
     *              El valor de {@link #símbolo}.
     */
    public Symbol verSímbolo () {

        return símbolo;
    }


    /**
     * Devuelve una cadena con el identificador del nodo.
     *
     * @return
     *              El valor de {@link #identificador}.
     */
    @Override
    public String toString () {

        return identificador;
    }

    /**
     * Imprime por pantalla una representación del árbol.
     */
    public void imprimir () {

        print("", true);
    }


    /**
     * Método auxiliar para ir recorriendo los nodos por niveles.
     *
     * <p>
     * Obtenido de
     * <a href="https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram#8948691">
     * una respuesta en StackOverflow</a>
     */
    public void print (String prefijo, boolean esHoja) {

        System.out.println (prefijo + (esHoja ? "└── " : "├── ") + toString ());

        for (int i = 0; i < hijos.size() - 1; i++) {

            hijos.get(i).print(prefijo + (esHoja ? "    " : "│   "), false);
        }

        if (hijos.size() > 0) {

            hijos.get(hijos.size () - 1)
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
    public abstract void comprobarNodo (TablaSímbolos tabla);
}
