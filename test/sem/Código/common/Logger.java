package common;

import java_cup.runtime.*;

/**
 * Clase para manejar los mensajes de error y las estadísticas.
 */
public class Logger {

    /**
     * Contador de errores léxicos.
     */
    private static int err_lex = 0;

    /**
     * Contador de errores sintácticos.
     */
    private static int err_sin = 0;

    /**
     * Contador de errores semánticos.
     */
    private static int err_sem = 0;

    /**
     * Contador de avisos.
     */
    private static int avisos = 0;

    /**
     * Grado de detalle para la salida:
     * <ul>
     *      <li> 0 -> Muestra La salida final (AST, tabla y errores/avisos)
     *      <li> 1 -> Además, muestra las reglas aplicadas y los lexemas que
     *          llegan al analizador sintáctico.
     * </ul>
     */
    private static int nivelDetalle = 0;

    /**
     * Vuelve a poner los contadores de errores a 0
     */
    public static void reiniciarErrores () {

        avisos = 0;

        err_lex = 0;
        err_sem = 0;
        err_sin = 0;
    }

    /**
     * Cambia el nivel de detalle para la salida:
     * <ul>
     *      <li> 0 -> Muestra La salida final (AST, tabla y errores/avisos)
     *      <li> 1 -> Además, muestra las reglas aplicadas y los lexemas que
     *          llegan al analizador sintáctico.
     * </ul>
     *
     * @param nuevoNivel
     *              Nuevo valor para el nivel de detalle.
     */
    public static void cambiarNivelDetalle (int nuevoNivel) {

        nivelDetalle = nuevoNivel;
    }

    /**
     * Imprime por pantalla la regla que se está usando actualmente.
     *
     * @param idRegla
     *              Identificador de la regla.
     *
     * @param producción
     *              Cadena con la producción exacta de la regla.
     */
    public static void regla (String idRegla, String producción) {

        if (nivelDetalle >= 1) {

            System.out.printf ("\t\tAplicando regla %3s => %s\n", idRegla, producción);
        }
    }

    /**
     * Imprime el lexema pasado del analizador léxico al sintáctico.
     *
     * @param lexema
     *              Cadena con la que se formará luego el símbolo que se pasará
     *          al analizador sintáctico.
     */
    public static void lexema (String lexema) {

        if (nivelDetalle >= 1) {

            System.out.println ("\tLexema: " + lexema);
        }
    }

    /**
     * Imprime por pantalla un mensaje de error para avisar de un error léxico.
     *
     * @param s
     *              Símbolo en el que se detectó el error.
     */
    public static void error_léxico (Symbol s) {

        System.err.println ("Error léxico --> línea " + s.left + ", col. "
				+ s.right + " en el símbolo '" + s.value + "'");

        err_lex++;
    }

    /**
     * Imprime por pantalla un mensaje de error para avisar de un error
     * sintáctico.
     *
     * @param s
     *              Símbolo en el que se detectó el error.
     *
     * @param mensaje
     *              Mensaje que se desea añadir al final del aviso de error.
     */
    public static void error_sintáctico (Symbol s, String mensaje) {

        if (s == null) {

                System.err.println ("Error de sintaxis --> " + mensaje);
        } else {

                System.err.println ("Error de sintaxis --> línea " + s.left
                                + ", col. " + s.right + ": " + mensaje);
        }

        err_sin++;
    }

    /**
     * Imprime por pantalla un mensaje de error para avisar de un error
     * semántico.
     *
     * @param s
     *              Símbolo en el que se detectó el error.
     *
     * @param mensaje
     *              Mensaje que se desea añadir al final del aviso de error.
     */
    public static void error_semántico (Symbol s, String mensaje) {

        if (s == null) {

                System.err.println ("Error semántico --> " + mensaje);
        } else {

                System.err.println ("Error semántico --> línea " + s.left
                                + ", col. " + s.right + ": " + mensaje);
        }

        err_sem++;
    }

    /**
     * Imprime por <i>STDERR</i> el mensaje de error formateado para que
     * aparezca como aclaración después de que se haya llamado al método
     * syntax_error(), al que se llama automáticamente cuando llega un token
     * inesperado.
     *
     *
     * @param mensaje
     *              Mensaje a imprimir tras el aviso de error.
     */
    public static void error_sintaxis (String mensaje) {

            /* Espacios para el "Error de sintaxis " */
            System.err.println ("                  \\-> " + mensaje);
    }

    /**
     * Imprime por pantalla un mensaje de error para mostrar un aviso
     * (<i>warning</i>).
     *
     * @param s
     *              Símbolo en el que se detectó el error.
     *
     * @param mensaje
     *              Mensaje que se desea añadir al final del aviso.
     */
    public static void aviso (Symbol s, String mensaje) {

        if (s == null) {

                System.err.println ("Aviso --> " + mensaje);
        } else {

                System.err.println ("Aviso --> línea " + s.left + ", col. "
                                + s.right + ": " + mensaje);
        }

        avisos++;
    }

    /**
     * Imprime por <i>STDERR</i> un mensaje de error sin especificar tipo.
     *
     * @param mensaje
     *              Mensaje que se imprimirá tras el aviso de error inesperado
     *          (puede ser 'null').
     */
    public static void error (String mensaje) {

        if (mensaje == null) {

            System.err.println ("Error inesperado");
        } else {

            System.err.println ("Error inesperado: " + mensaje);
        }
    }

    /**
     * Imprime las estadísticas recogidas
     */
    public static void imprimir_estadísticas () {

        if ((err_lex == 0)
            && (err_sin == 0)
            && (err_sem == 0)) {

            if (avisos > 0) {

                System.out.println ("\n-------------\n"
                                    + "Estadísticas:"
                                    + "\n-------------");

                System.out.println ("-Avisos: " + avisos);
            }

            System.out.println ("\n\t--------------------------------");
            System.out.println ("\t\t**** FIN ****");
            System.out.println ("\tAnálisis finalizado sin errores.");
            System.out.println ("\t--------------------------------\n");

        } else {

            System.out.println ("\n-------------\n"
                                + "Estadísticas:"
                                + "\n-------------");

            System.out.println ("-Errores léxicos: " + err_lex);
            System.out.println ("-Errores sintácticos: " + err_sin);
            System.out.println ("-Errores semánticos: " + err_sem);
            System.out.println ("-Avisos: " + avisos);

            System.out.println ("\n\t--------------------------------");
            System.out.println ("\t\t**** FIN ****");
            System.out.println ("\tAnálisis finalizado con "
                            + (err_lex + err_sem + err_sin) + " errores.");
            System.out.println ("\t--------------------------------\n");
        }
    }
}
