package árbol.nodos.sentencias;

import common.Logger;
import tabla.Info;
import tabla.TablaSímbolos;
import tabla.TipoDato;
import árbol.Nodo;
import árbol.nodos.declaraciones.NodoVar;

/**
 * Nodo para un bucle.
 *
 * <p>
 * Son necesarios dos nodos hijos:
 * <ul>
 *      <li> La condición para salir del bucle ({@link Nodo} booleano)
 *      <li> Las sentencias que forman el bucle ({@link NodoSents})
 * </ul>
 */
public class NodoBucle extends Nodo {

    /**
     * Nodo con la condición de salida.
     */
    private Nodo condición;

    /**
     * Bloque de sentencias dentro del bucle.
     */
    private NodoSents sentencias;

    /**
     * Constructor.
     */
    public NodoBucle () {

        super (null, "Bucle");

        condición = null;
        sentencias = null;
    }

    /**
     * Añade un nodo con la condición.
     * <p>
     * Nodos aceptados:
     * <ul>
     *      <li> Booleano ({@link NodoVar} con {@code tipo = booleano})
     *      <li> Identificador ({@link NodoVar} con {@code tipo = booleano})
     *      <li> Comparación ({@link NodoOp} con {@code tipo = booleano})
     * </ul>
     *
     * @param condición
     *              Nodo con la condición de salida.
     *
     * @return
     *              <i>true</i> si no había ya una condición (y, por tanto, se
     *          ha añadido a la lista); o <i>false</i> si ya había un nodo de
     *          tipo {@link NodoCond}.
     */
    public boolean añadirCondición (Nodo condición) {

        /* Comprueba que no hubiera ya una condición */
        if (this.condición != null) {

            return false;
        }

        /* Comprueba que le nodo sea uno de los aceptados */
        if ( !(condición instanceof NodoVar)
             && !(condición instanceof NodoOp)) {

            return false;
        }

        this.condición = condición;
        añadirHijo (condición);

        return true;
    }

    /**
     * Añade un nodo con el bloque de sentencias.
     *
     * @param sentencias
     *              Nodo con las sentencias.
     *
     * @return
     *              <i>true</i> si no había ya un bloque de sentencias (y,
     *          por tanto, se ha añadido a la lista); o <i>false</i> si ya había
     *          un nodo de tipo {@link NodoSents}.
     */
    public boolean añadirSentencias (NodoSents sentencias) {

        if (this.sentencias != null) {

            return false;
        } else {

            this.sentencias = sentencias;
            añadirHijo (sentencias);

            return true;
        }
    }

    /**
     * Intenta anticipar la rama que se tomará.
     *
     *
     * @param infoCon
     *              Información con el valor y el tipo de condición.
     *
     *
     * @return
     *          Uno de los siguientes valores, dependiendo de lo que se haya
     *      podido calcular:
     *      <ul>
     *          <li> 0 si no se ha podido concluir nada
     *          <li> 1 si se sabe que se tomará la rama del 'if'
     *          <li> 2 si se sabe que se tomará la rama del 'else' (o, en su
     *          defecto, no se entrará nunca en el bloque de sentencias)
     *      </ul>
     */
    private int comprobarCondición () {

        Info infoCon;

        /* Si la condición es un inmediato booleano, muestra un aviso
          (hace lo mismo si es una operación que siempre toma el mismo valor) */
        if (condición instanceof NodoVar) {

            infoCon = ((NodoVar) condición).verInfo ();

            /* Inmediato booleano */
            if (condición.verID ().matches("(true|false)")) {

                if (infoCon.verValor().equals("true")) {

                    Logger.aviso (condición.verSímbolo(), "Bucle infinito");
                    return 1;
                } else {

                        Logger.aviso (condición.verSímbolo(), "Nunca se"
                                + " ejecutarán las sentencias del bucle");

                    return 2;
                }
            }
        } else {

            infoCon = ((NodoOp) condición).verInfo ();
            /* Si se sabe con certeza el valor que tomará
            (infoCon.verValor () != null), se avisa */
            if (infoCon.verValor () != null) {

                if (infoCon.verValor().equals("true")) {

//                    Logger.aviso (condición.verSímbolo(), "Posible bucle infinito");
//                    return 1;
                } else {
                        Logger.aviso (condición.verSímbolo(), "Nunca se"
                                + " ejecutarán las sentencias del bucle");

                    return 2;
                }
            }
        }

        return 0;
    }

    /**
     * Comprueba si la variable de la condición (si es una variable) ha sido
     * inicializada
     */
    private void comprobarIni (TablaSímbolos tabla) {

        Info infoCon;

        if (condición instanceof NodoVar) {

            infoCon = ((NodoVar) condición).verInfo ();

            /* Variable (no inmediato booleano) */
            if (!condición.verID ().matches("(true|false)")
                && infoCon.verTipo ().equals (TipoDato.booleano)) {

                infoCon = tabla.obtenerID (condición.verID ());

                if (infoCon != null) {
                    /* Comprueba si ha sido inicializada y la marca como usada en
                    la tabla y en el nodo */
                    if (!infoCon.inicializada ()) {

                        Logger.aviso (condición.verSímbolo(), "La variable '"
                                        + condición.verID ()
                                        + "' no ha sido inicializada");
                    }

                    infoCon.usar ();
                    tabla.obtenerID ( ((NodoVar) condición).verID() ).usar ();
                }
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

        Info infoCond;
        TablaSímbolos tmp = tabla.obtenerCopia ();

        /* Comprueba primero los hijos para poder obtener toda la información
        necesaria */
        condición.comprobarNodo (tmp);
        sentencias.comprobarNodo (tmp);

        comprobarIni (tabla);

        /* La condición tiene que ser de tipo booleano */
        if (condición instanceof NodoVar) {

            infoCond = ((NodoVar) condición).verInfo ();
        } else {

            infoCond = ((NodoOp) condición).verInfo ();
        }

        if (!infoCond.verTipo ().equals (TipoDato.booleano)) {

            Logger.error_semántico (condición.verSímbolo (), "La condición "
                                    + "del bucle debe ser de tipo booleano");
        }

        switch (comprobarCondición ()) {

            case 0:
                /* No se ha podido concluir nada */
                break;

            case 1:
                /* Bucle infinito */
                break;

            default:
                /* Nunca se entrará en el bucle */
        }
    }
}
