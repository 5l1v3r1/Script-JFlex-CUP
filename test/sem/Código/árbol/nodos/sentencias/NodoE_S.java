package árbol.nodos.sentencias;

import java_cup.runtime.*;
import common.Logger;
import tabla.Info;
import tabla.TablaSímbolos;
import tabla.TipoDato;
import árbol.Nodo;
import árbol.nodos.declaraciones.NodoVar;

/**
 * Nodo para almacenar una sentencia de tipo entrada/salida.
 *
 * <p>
 * Este nodo tendrá sólo un hijo con el valor a escribir/leer de uno de los
 * siguientes tipos:
 * <ul>
 *      <li> Identificador ({@link NodoVar})
 *      <li> Operación entero ({@link NodoOp} con {@code tipo = entero})
 *      <li> Entero inmediato ({@link NodoVar} con {@code tipo = entero})
 * </ul>
 */
public class NodoE_S extends Nodo {

    /**
     * Constructor.
     *
     * @param tipo
     *              Tipo de instrucción ("entrada" o "salida").
     */
    public NodoE_S (Symbol tipo) {

        super (tipo, (String) tipo.value);
    }

    /**
     * Añade la palabra reservada de entrada/salida.
     *
     * <p>
     * Tipos permitidos:
     * <ul>
     *      <li> Identificador ({@link NodoVar})
     *      <li> Operación entero ({@link NodoOp} con {@code tipo = entero})
     *      <li> Entero inmediato ({@link NodoVar} con {@code tipo = entero})
     * </ul>
     *
     *
     * @param valor
     *              Un nodo que almacene el valor a leer/escribir.
     *
     *
     * @return
     *              <i>true</i> si el nodo era de uno de los tipos aceptados
     *          (y, por tanto, se ha añadido a la lista); o <i>false</i> si el
     *          tipo de nodo no se ha aceptado.
     */
    public boolean añadirID (Nodo valor) {

        /* Comprueba que sea uno de los tipos admitidos */
        if (!(valor instanceof NodoVar)
            && !(valor instanceof NodoOp)
            ) {

            return false;
        } else {

            /* Sólo se permite un hijo */
            if (verHijos().isEmpty()) {

                añadirHijo (valor);
                return true;
            } else {

                return false;
            }
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

        Nodo hijo;
        Info aux;

        if (verHijos().isEmpty ()) {

            return;
        }

        hijo = verHijos().get (0);

        /* Comprueba primero su hijo para que actualice su valor y poder
        obtener la información correctamente */
        hijo.comprobarNodo (tabla);

        /* Si es una operación de lectura, sólo se permite una variable de
        tipo entero (NodoVar de variable con tipo = entero) */
        if (verID().equals("read")) {

            if (!(hijo instanceof NodoVar)) {

                Logger.error_semántico (hijo.verSímbolo (), "El valor a leer "
                        + "debe ser una variable de tipo entero");
            } else {
                /* Comprueba si es una variable entera declarada (si no estaba
                declarada, hijo.comprobarNodo () ya se ha encargado de añadirla
                a la tabla de símbolos para poder seguir trabajando) */
                aux = tabla.obtenerID ( ((NodoVar) hijo).verID() );

		if (aux == null) {

                    Logger.error_semántico(hijo.verSímbolo (), "El valor a leer"
                                + " debe ser una variable de tipo entero");

                } else {

                    if (!aux.verTipo ().equals (TipoDato.entero)) {

                        Logger.error_semántico (hijo.verSímbolo(), "El valor a leer"
                                + " debe ser una variable de tipo entero");
                    } else {

                        /* Marca la variable como inicializada en la tabla y en el
                        nodo */
                        aux.inicializar ();
                        ((NodoVar) hijo).verInfo ().inicializar ();
                    }
                }
            }
        } else {

            /* Si se intenta escribir una variable sin inicializar, se avisa */
            if (hijo instanceof NodoVar) {

                aux = tabla.obtenerID ( ((NodoVar) hijo).verID() );

                if (aux != null) {

                    if (!aux.inicializada ()) {

                        Logger.aviso (hijo.verSímbolo(), "La variable '"
                                      + hijo.verID () + "' no ha sido inicializada");
                    }

                    /* Marca la variable como usada en la tabla y en el nodo */
                    aux.usar ();
                    tabla.obtenerID ( ((NodoVar) hijo).verID() ).usar ();
                }
            }
        }
    }
}
