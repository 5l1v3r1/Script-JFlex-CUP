package árbol.nodos.sentencias;

import java_cup.runtime.*;
import common.Logger;
import tabla.Info;
import tabla.TablaSímbolos;
import árbol.Nodo;
import árbol.nodos.declaraciones.NodoVar;

/**
 * Nodo para una asignación.
 *
 * <p>
 * Sólo necesita dos hijos (sus tipos deben coincidir):
 * <ul>
 *      <li> El identificador al que asignar el valor ({@link NodoVar}).
 *      <li> El valor a asignar ({@link NodoVar} o {@link NodoOp}).
 * </ul>
 */
public class NodoAsigna extends Nodo {

    /**
     * Variable a la que se le va a asignar el valor.
     */
    private NodoVar variable;

    /**
     * Valor a asignar.
     */
    private Nodo valor;

    /**
     * Constructor.
     *
     * @param asigna
     *              Símbolo de asignación (":=");
     */
    public NodoAsigna (Symbol asigna) {

        super (asigna, "Asignación");

        variable = null;
        valor = null;
    }

    /**
     * Añade un nodo con la información de la variable.
     *
     *
     * @param variable
     *              Nodo representando a la variable cuyo valor va a ser
     *          asignado.
     *
     * @return
     *              <i>true</i> si el nodo era de uno de los tipos aceptados
     *          y no se había añadido aún una parte izquierda; o <i>false</i>
     *          si el tipo de nodo no se ha aceptado.
     */
    public boolean añadirVariable (NodoVar variable) {

        /* Comprueba si ya se había asignado algún valor anteriormente */
        if (this.variable != null) {

            return false;
        }

        this.variable = variable;
        añadirHijo (variable);

        return true;
    }

    /**
     * Añade un nodo que representa la parte derecha de la asignación.
     * Se admiten los siguientes tipos:
     * <ul>
     *      <li> Comparación {{@link NodoOp})
     *      <li> Operación ({@link NodoOp})
     *      <li> Identificador ({@link NodoVar})
     *      <li> Inmediato ({@link NodoVar})
     * </ul>
     *
     *
     * @param valor
     *              Nodo con la parte derecha de la asignación.
     *
     *
     * @return
     *              <i>true</i> si el nodo era de uno de los tipos aceptados
     *          y no se había añadido aún una parte derecha; o <i>false</i>
     *          si el tipo de nodo no se ha aceptado.
     */
    public boolean añadirParteDer (Nodo valor) {

        /* Comprueba que no hubiera ya una parte derecha */
        if (this.valor != null) {

            return false;
        }

        /* Comprueba que el nodo sea uno de los aceptados */
        if ( !(valor instanceof NodoOp)
            && !(valor instanceof NodoVar)) {

            return false;
        }

        /* Añade el nodo */
        this.valor = valor;
        añadirHijo (valor);

        return true;
    }

    /**
     * Comprueba si la variable de la parte derecha (si es una variable) ha sido
     * inicializada
     */
    private void comprobarIni (TablaSímbolos tabla) {

        Info infoCon;

        if (valor instanceof NodoVar) {

            /* Variable */
            if (!valor.verID ().matches ("Entero: [-]?[0-9]+")
                    && !valor.verID ().matches ("(true|false)")
                    && ! valor.verID ().matches ("Reservada: skip")) {

                infoCon = tabla.obtenerID (valor.verID ());

                if (infoCon != null) {
                    /* Comprueba si ha sido inicializada y la marca como usada en
                    la tabla y en el nodo */
                    if (!infoCon.inicializada ()) {

                        Logger.aviso (valor.verSímbolo(), "La variable '"
                                        + valor.verID ()
                                        + "' no ha sido inicializada");
                    }

                    infoCon.usar ();
                    tabla.obtenerID ( ((NodoVar) valor).verID() ).usar ();
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

        Info infoVar,
             infoValor;

        /* Comprueba primero los hijos para poder obtener toda la información
        necesaria */
        variable.comprobarNodo (tabla);
        valor.comprobarNodo (tabla);

        comprobarIni (tabla);

        /* Comprueba la concordancia de tipos */
        infoVar = variable.verInfo ();

        if (valor instanceof NodoVar) {

            infoValor = ((NodoVar) valor).verInfo ();
        } else {

            infoValor = ((NodoOp) valor).verInfo ();
        }

        /* Si la variable y el valor no tienen el mismo tipo, lanza un error */
        if (!infoValor.verTipo ().equals (infoVar.verTipo ())) {

            Logger.error_semántico (verSímbolo(), "No se puede asignar un valor"
                    + " de tipo " + infoValor.verTipo () + " a una variable de"
                    + " tipo " + infoVar.verTipo ());
        } else {

            /* Intenta calcular el valor que tendrá la variable */
            if (infoValor.verValor() != null) {

                /* Actualiza el valor en el nodo y en la tabla de símbolos */
                infoVar.cambiarValor (infoValor.verValor ());
                tabla.obtenerID (variable.verID ())
                                 .cambiarValor(infoValor.verValor ());
            }

            /* Marca la variable como inicializada en el nodo y en la
            tabla de símbolos */
            infoVar.inicializar ();
            tabla.obtenerID (variable.verID ()).inicializar();
        }
    }
}
