package árbol.nodos.declaraciones;

import tabla.Info;
import tabla.TipoDato;
import java_cup.runtime.*;
import common.Logger;
import tabla.TablaSímbolos;
import árbol.Nodo;

/**
 * Nodo para almacenar una variable declarada.
 */
public class NodoVar extends Nodo {

    /**
     * Información relativa a la variable almacenada.
     */
    private final Info info;

    /**
     * Información adicional para realizar una negación a la hora de calcular
     * el valor
     */
    private boolean negar = false;

    /**
     * Constructor.
     *
     * @param símbolo
     *              Token del nodo.
     *
     * @param identificador
     *              Cadena para identificar el nodo (nombre dela variable).
     */
    public NodoVar (Symbol símbolo, String identificador) {

        super (símbolo, identificador);

        this.info = new Info (TipoDato.nulo, null);
    }

    /**
     * Marca la variable para realizar una negación cuando sea necesario.
     */
    public void negar () {

        this.negar = true;
    }

    /**
     * Permite ver la información almacenada sobre la variable.
     *
     * @return
     *              El valor de {@code info}.
     */
    public Info verInfo () {

        return info;
    }

    /**
     * Devuelve el nombre de la variable almacenada.
     *
     * @return
     *              El valor de {@code identificador}.
     */
    public String verNombreVar () {

        return verID ();
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

        String aux;

        if (verID () == null || verID ().equals("")) {

            this.cambiarID ("");
            return;
        }

        /* Si es un inmediato (booleano o entero), realiza las comprobaciones
        y sale. Si es una variable, la busca en la tabla de símbolos y realiza
        las comprobaciones */
        if (verID ().matches ("Entero: [-]?[0-9]+")) {

            info.cambiarTipo (TipoDato.entero);

            aux = verID ();
            aux = aux.replaceAll ("\\D+", "");

            if (verID ().matches("Entero: [-][0-9]+")) {

                info.cambiarValor ("-" + aux);
            } else {

                info.cambiarValor (aux);
            }

            return;
        }

        if (verID ().matches ("(true|false)")) {

            info.cambiarTipo (TipoDato.booleano);

            /* Comprueba si hay que cambiar el valor */
            if (negar) {

                info.cambiarValor (verID().equals("false")? "true" : "false");
                negar = false;
            } else {

                info.cambiarValor (verID().equals("true")? "true" : "false");
            }

            return;
        }

        /* Caso especial: palabra reservada "skip" */
        if (verID ().equals("Reservada: skip")) {

            return;
        }

        /* Busca su identificador en la tabla de símbolos y lo pone a 0.
        Si no lo encuentra, lanza un mensaje de error semántico y se añade
        en la tabla para proseguir con el análisis */
        Info infoVar = tabla.obtenerID (verID ());

        if (infoVar == null) {

            Logger.error_semántico (verSímbolo (), "La variable '" + verID ()
                                    + "' no ha sido declarada.");

            if (!tabla.añadirID (verID (), info.verTipo ())) {

                Logger.error ("No se ha podido añadir el identificador '"
                               + verID () + "' a la tabla de símbolos. Se "
                               + "prosigue con el siguiente nodo.");
                return;
            }

            infoVar = tabla.obtenerID (verID ());
        }

        /* Compara la información de la tabla de símbolos con la del nodo
        (prevalece la información de la tabla de símbolos) */
        if (!infoVar.verTipo ().equals (info.verTipo ())) {

            if (!info.verTipo ().equals (TipoDato.nulo)) {

                Logger.error_semántico (verSímbolo (), "La variable '" + verID ()
                            + "' es de tipo " + infoVar.verTipo ()
                            + ", pero se esperaba un tipo " + info.verTipo ());
            }

            info.cambiarTipo (infoVar.verTipo ());
        }

        /* Actualiza la información del nodo. Si es una variable booleana negada,
        actúa de manera acorde */
        if (info.verTipo ().equals (TipoDato.booleano)) {

            if (infoVar.verValor () == null) {

                return;
            }

            /* Como se sabe el valor, significa que está inicializada */
            infoVar.inicializar();

            if (negar) {

                info.cambiarValor ((infoVar.verValor().equals("true"))?
                                        "false" : "true");
                negar = false;
            } else {

                info.cambiarValor ((infoVar.verValor().equals("true"))?
                                        "true" : "false");
            }
        } else {

            if (infoVar.verValor () != null) {

                /* Como se sabe el valor, significa que está inicializada */
                infoVar.inicializar();
            }

            info.cambiarValor (infoVar.verValor ());
        }
    }
}
