package árbol.nodos.sentencias;

import common.Logger;
import tabla.Info;
import tabla.TablaSímbolos;
import tabla.TipoDato;
import árbol.Nodo;
import árbol.nodos.declaraciones.NodoVar;

/**
 * Nodo para una sentencia condicional.
 *
 * <p>
 * Puede tener 2 o 3 hijos:
 * <ul>
 *      <li> Condición ({@code Nodo} booleano)
 *      <li> Sentencias para el bloque <i>if</i> ({@link NodoSents})
 *      <li> Sentencias para el bloque <i>else</i> ({@link NodoSents})
 *              <b>- opcional</b>
 * </ul>
 */
public class NodoIf extends Nodo {


    /**
     * Nodo con la condición de entrada en la rama del "if".
     */
    private Nodo condición;

    /**
     * Bloque de sentencias dentro de la rama del "if".
     */
    private NodoSents sentencias_if;

    /**
     * Bloque de sentencias dentro de la rama del "else".
     */
    private NodoSents sentencias_else;
    /**
     * Constructor.
     */
    public NodoIf () {

        super (null, "Sentencia condicional");

        condición = null;
        sentencias_if = null;
        sentencias_else = null;

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

        /* Comprueba que sea uno de los tipos aceptados */
        if (!(condición instanceof NodoVar)
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
    public boolean añadirIf (NodoSents sentencias) {

        if (this.sentencias_if != null) {

            return false;
        } else {

            this.sentencias_if = sentencias;
            sentencias.cambiarID ("Rama 'if'");

            añadirHijo (sentencias);

            return true;
        }
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
    public boolean añadirElse (NodoSents sentencias) {

        if (this.sentencias_else != null) {

            return false;
        } else {

            this.sentencias_else = sentencias;
            sentencias.cambiarID ("Rama 'else'");

            añadirHijo (sentencias);

            return true;
        }
    }

    /**
     * Intenta anticipar la rama que se tomará.
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

                    Logger.aviso (condición.verSímbolo(), "Siempre se va a "
                                   + "entrar en la rama 'if'");
                    return 1;
                } else {

                    if (sentencias_else == null) {

                        Logger.aviso (condición.verSímbolo(),
                                      "Nunca se van ejecutar las sentencias");
                    } else {

                        Logger.aviso (condición.verSímbolo(), "Siempre se va a "
                                    + "entrar en la rama 'else'");
                    }

                    return 2;
                }
            }
        } else {

            infoCon = ((NodoOp) condición).verInfo ();
            /* Si se sabe con certeza el valor que tomará
            (infoCon.verValor () != null), se avisa */
            if (infoCon.verValor () != null) {

                if (infoCon.verValor().equals("true")) {

                    Logger.aviso (condición.verSímbolo(), "Es posible que "
                            + "siempre se vaya a entrar en la rama 'if'");
//                    return 1;
                } else {

                    if (sentencias_else == null) {

                        Logger.aviso (condición.verSímbolo(), "Es posible que"
                                + " nunca se ejecuten las sentencias");
                    } else {

                        Logger.aviso (condición.verSímbolo(), "Es posible que"
                                + " siempre se entre en la rama 'else'");
                    }

//                    return 2;
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

        /* Copias de la tabla de símbolos para realizar un seguimiento en la
        rama del 'if' y en la del 'else' */
        TablaSímbolos tmp_if = tabla.obtenerCopia (),
                      tmp_else = null;

        /* Comprueba primero los hijos para poder obtener toda la información
        necesaria */
        condición.comprobarNodo (tmp_if);
        sentencias_if.comprobarNodo (tmp_if);

        /* Si hay una rama 'else', también lo explora */
        if (sentencias_else != null) {

            tmp_else = tabla.obtenerCopia ();

            sentencias_else.comprobarNodo (tmp_else);
        }


        comprobarIni (tabla);

        /* Comprueba la condición (por si siempre toma el mismo valor) */
        switch (comprobarCondición()) {

            case 0:
                /* No se ha podido concluir nada -> se cogen los valores
                comunes a las dos ramas */
                if (tmp_else != null) {

                    tabla.actualizarTabla (
                            TablaSímbolos.fusionarTablas (tmp_if, tmp_else)
                    );
                } else {

                    tabla.actualizarUsoVars (tmp_if);
                }
                break;

            case 1:
                /* Se sabe que se tomará siempre la rama del 'if' -> se
                actualizan los valores en consecuencia */
                tabla.actualizarTabla (tmp_if);
                break;

            default:
                /* Se sabe que se tomará siempre la rama del 'else' -> se
                actualizan los valores en consecuencia */
                if (tmp_else != null) {

                    tabla.actualizarTabla (tmp_else);
                }
                break;
        }
    }
}
