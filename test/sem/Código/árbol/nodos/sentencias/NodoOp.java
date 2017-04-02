package árbol.nodos.sentencias;

import java_cup.runtime.*;
import common.Logger;
import java.util.Objects;
import pecl1.Parser;
import tabla.Info;
import tabla.TipoDato;
import árbol.Nodo;
import árbol.nodos.declaraciones.NodoVar;

import static pecl1.Sym.*;
import tabla.TablaSímbolos;

/**
 * Nodo para representar una operación.
 *
 * <p>
 * Sólo puede tener dos hijos (uno para cada parte de la operación) de uno de
 * los siguientes tipos (sus tipos deben coincidir):
 * <ul>
 *      <li> Comparación {{@link NodoOp})
 *      <li> Identificador ({@link NodoVar})
 *      <li> Inmediato ({@link NodoVar})
 *      <li> Operación ({@link NodoOp})
 * </ul>
 */
public class NodoOp extends Nodo {

    /**
     * Tipo de operación (lógica o entera) y valor esperado (si se puede
     * calcular).
     */
    private final Info infoOp;

    /**
     * Parte derecha de la opperació.
     */
    private Nodo parteIzquierda;

    /**
     * Parte derecha de la operación.
     */
    private Nodo parteDerecha;

    /**
     * Operación a realizar entre las dos partes.
     */
    private final Symbol operación;

    /**
     * Información adicional para realizar una negación a la hora de calcular
     * el valor
     */
    private boolean negar = false;

    /**
     * Constructor.
     *
     * @param tipo
     *              Tipo de dato del valor de retorno la operación.
     *
     * @param operación
     *              Símbolo de la operación.
     */
    public NodoOp (TipoDato tipo, Symbol operación) {

        super (operación, "Operación " + tipo + ": " + operación.value);

        infoOp = new Info (tipo, null);

        this.operación = operación;

        parteIzquierda = null;
        parteDerecha = null;
    }

    /**
     * Marca la variable para realizar una negación cuando sea necesario.
     */
    public void negar () {

        this.negar = true;
    }

    /**
     * Intenta calcular el valor de la operación final.
     */
    private void calcularValor (Info parteIzq, Info parteDer) {

        /* Deben estar los dos hijos para intentar calcular el valor */
        if ((parteIzquierda == null)
             || (parteDerecha == null)
                ) {

            return;
        }

        switch (operación.sym) {
            /* Operaciones enteras */
            case SUMA:
                infoOp.cambiarValor(
                        String.valueOf(
                                Integer.valueOf(parteIzq.verValor())
                                + Integer.valueOf(parteDer.verValor())
                            )
                        );
                break;

            case RESTA:
                infoOp.cambiarValor(
                        String.valueOf(
                                Integer.valueOf(parteIzq.verValor())
                                - Integer.valueOf(parteDer.verValor())
                            )
                        );
                break;

            case MULTI:
                infoOp.cambiarValor(
                        String.valueOf(
                                Integer.valueOf(parteIzq.verValor())
                                * Integer.valueOf(parteDer.verValor())
                            )
                        );
                break;

            case DIVI:
                /* Detecta la división entre 0 */
                if (parteDer.verValor().equals("0")) {

                    Parser.aviso ("División entre 0", this.verSímbolo());
                } else {

                    infoOp.cambiarValor(
                        String.valueOf(
                                Integer.valueOf(parteIzq.verValor())
                                / Integer.valueOf(parteDer.verValor())
                            )
                        );
                }
                break;

            /* Comparaciones */
            case MENOR:
                infoOp.cambiarValor (
                            (
                                Integer.valueOf(parteIzq.verValor())
                                < Integer.valueOf(parteDer.verValor())
                            )? "true" : "false"
                        );
                break;

            case MENOR_IGUAL:
                infoOp.cambiarValor (
                            (
                                Integer.valueOf(parteIzq.verValor())
                                <= Integer.valueOf(parteDer.verValor())
                            )? "true" : "false"
                        );
                break;

            case MAYOR:
                infoOp.cambiarValor (
                            (
                                Integer.valueOf(parteIzq.verValor())
                                > Integer.valueOf(parteDer.verValor())
                            )? "true" : "false"
                        );
                break;


            case MAYOR_IGUAL:
                infoOp.cambiarValor (
                            (
                                Integer.valueOf(parteIzq.verValor())
                                >= Integer.valueOf(parteDer.verValor())
                            )? "true" : "false"
                        );
                break;

            case IGUAL:
                if (infoOp.verTipo().equals(TipoDato.entero)) {

                    infoOp.cambiarValor (
                                (
                                    Objects.equals(
                                            Integer.valueOf(parteIzq.verValor()),
                                            Integer.valueOf(parteDer.verValor())
                                    )
                                )? "true" : "false"
                            );
                } else {

                    infoOp.cambiarValor (
                                (
                                    Objects.equals(
                                            Boolean.valueOf(parteIzq.verValor()),
                                            Boolean.valueOf(parteDer.verValor())
                                    )
                                )? "true" : "false"
                            );
                }
                break;

            case NO_IGUAL:
                if (infoOp.verTipo().equals(TipoDato.entero)) {

                    infoOp.cambiarValor (
                                (
                                    !Objects.equals(
                                            Integer.valueOf(parteIzq.verValor()),
                                            Integer.valueOf(parteDer.verValor())
                                    )
                                )? "true" : "false"
                            );
                } else {

                    infoOp.cambiarValor (
                                (
                                    !Objects.equals(
                                            Boolean.valueOf(parteIzq.verValor()),
                                            Boolean.valueOf(parteDer.verValor())
                                    )
                                )? "true" : "false"
                            );
                }
                break;


            /* Operaciones lógicas (los valores de info.verValor() se asume que
             son "true" o "false") */
            case AND:
                infoOp.cambiarValor (
                            (
                                Boolean.valueOf(parteIzq.verValor())
                                && Boolean.valueOf(parteDer.verValor())
                            )? "true" : "false"
                        );
                break;

            case OR:
                infoOp.cambiarValor (
                            (
                                Boolean.valueOf(parteIzq.verValor())
                                || Boolean.valueOf(parteDer.verValor())
                            )? "true" : "false"
                        );
                break;

            default:
                Logger.error("Operación no soportada: " + operación.value);
        }

        /* Si era una expresión lógica negada, realiza la negación */
        if (negar) {

            if (infoOp.verTipo ().equals(TipoDato.booleano)) {

                infoOp.cambiarValor((infoOp.verValor ().equals ("true"))?
                                        "false"
                                        : "true");
            } else {

                Logger.error_sintáctico (operación, "No se puede realizar una "
                        + "negación sobre una operación que no sea booleana");
            }

            negar = false;
        }
    }

    /**
     * Añade un nodo que representa la parte izquierda de la operación.
     * Se admiten los siguientes tipos:
     * <ul>
     *      <li> Comparación {{@link NodoOp})
     *      <li> Operación ({@link NodoOp})
     *      <li> Identificador ({@link NodoVar})
     *      <li> Inmediato ({@link NodoVar})
     * </ul>
     *
     *
     * @param parteIzquierda
     *              Nodo con la parte izquierda de la operación.
     *
     *
     * @return
     *              <i>true</i> si el nodo era de uno de los tipos aceptados
     *          y no se había añadido aún una parte izquierda; o <i>false</i>
     *          si el tipo de nodo no se ha aceptado.
     */
    public boolean añadirParteIzq (Nodo parteIzquierda) {

        /* Comprueba que no hubiera ya una parte izquierda */
        if (this.parteIzquierda != null) {

            return false;
        }

        /* Comprueba que el nodo sea uno de los aceptados */
        if (!(parteIzquierda instanceof NodoVar)
             && !(parteIzquierda instanceof NodoOp)) {

            return false;
        }

        /* Añade el nodo */
        this.parteIzquierda = parteIzquierda;
        añadirHijo (parteIzquierda);

        return true;
    }

    /**
     * Añade un nodo que representa la parte derecha de la operación.
     * Se admiten los siguientes tipos:
     * <ul>
     *      <li> Comparación {{@link NodoOp})
     *      <li> Operación ({@link NodoOp})
     *      <li> Identificador ({@link NodoVar})
     *      <li> Inmediato ({@link NodoVar})
     * </ul>
     *
     *
     * @param parteDerecha
     *              Nodo con la parte derecha de la operación.
     *
     *
     * @return
     *              <i>true</i> si el nodo era de uno de los tipos aceptados
     *          y no se había añadido aún una parte derecha; o <i>false</i>
     *          si el tipo de nodo no se ha aceptado.
     */
    public boolean añadirParteDer (Nodo parteDerecha) {

        /* Comprueba que no hubiera ya una parte derecha */
        if (this.parteDerecha != null) {

            return false;
        }

        /* Comprueba que el nodo sea uno de los aceptados */
        if (!(parteDerecha instanceof NodoVar)
             && !(parteDerecha instanceof NodoOp)) {

            return false;
        }

        /* Añade el nodo */
        this.parteDerecha = parteDerecha;
        añadirHijo (parteDerecha);

        return true;
    }

    /**
     * Devuelve el nombre del operador almacenada.
     *
     * @return
     *              El valor de {@code operación}.
     */
    public Symbol verOperación () {

        return operación;
    }

    /**
     * Devuelve información sobre la operación.
     *
     * @return
     *              El valor de {@link #infoOp}.
     */
    public Info verInfo () {

        return infoOp;
    }

    /**
     * Devuelve la información del nodo, comprobando el tipo del mismo.
     */
    private Info obtenerInfo (Nodo nodo, TablaSímbolos tabla) {

        Info info;

        if (nodo instanceof NodoVar) {

            info = ((NodoVar) nodo).verInfo ();

            /* Aprovecha para mirar si la variable (si es una variable) ha sido
            inicializada */
            if (!nodo.verID ().matches ("Entero: [-]?[0-9]+")
                    && !nodo.verID ().matches ("(true|false)")
                    && ! nodo.verID ().matches ("Reservada: skip")) {

                info = tabla.obtenerID (nodo.verID ());
                /* Comprueba si ha sido inicializada y la marca como usada en
                la tabla y en el nodo */
                if (!info.inicializada ()) {

                    Logger.aviso (nodo.verSímbolo(), "La variable '"
                                    + nodo.verID ()
                                    + "' no ha sido inicializada");
                }

                info.usar ();
                tabla.obtenerID ( ((NodoVar) nodo).verID() ).usar ();
            }
        } else {

            info = ((NodoOp) nodo).verInfo ();
        }

        return info;
    }

    /**
     * Comprueba si la variable del nodo (si es una variable) ha sido
     * inicializada
     */
    private void comprobarIni (Nodo nodo, TablaSímbolos tabla) {

        Info infoCon;

        if (nodo instanceof NodoVar) {

            infoCon = ((NodoVar) nodo).verInfo ();

            /* Variable (no inmediato booleano) */
            if (!nodo.verID ().matches("(true|false)")
                && infoCon.verTipo ().equals (TipoDato.booleano)) {

                infoCon = tabla.obtenerID (nodo.verID ());

                if (infoCon != null) {
                    /* Comprueba si ha sido inicializada y la marca como usada en
                    la tabla y en el nodo */
                    if (!infoCon.inicializada ()) {

                        Logger.aviso (nodo.verSímbolo(), "La variable '"
                                        + nodo.verID ()
                                        + "' no ha sido inicializada");
                    }

                    infoCon.usar ();
                    tabla.obtenerID ( ((NodoVar) nodo).verID() ).usar ();
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

        Info izq,
             der;

        if ((parteIzquierda == null)
            || (parteDerecha == null)) {

            return;
        }

        /* Comprueba primero los hijos para poder obtener toda la información
        necesaria */
        parteIzquierda.comprobarNodo (tabla);
        parteDerecha.comprobarNodo (tabla);

        comprobarIni(parteIzquierda, tabla);
        comprobarIni(parteDerecha, tabla);

        /* Se comprueba que los operandos sean del mismo tipo y sean el
        esperado */
        izq = obtenerInfo (parteIzquierda, tabla);
        der = obtenerInfo (parteDerecha, tabla);

        if (!izq.verTipo ().equals (der.verTipo ())) {

            Logger.error_semántico (operación, "No se puede operar entre un "
                                + izq.verTipo () + " (" + parteIzquierda.verID ()
                                + ") y un " + der.verTipo () + " ("
                                + parteDerecha.verID () + ")");
        } else {

            /* Si se conoce el valor de los dos operandos, se calcula el
            resultado */
            if ((izq.verValor() != null)
                && (der.verValor() != null)) {

               calcularValor (izq, der);
            }
        }
    }
}
