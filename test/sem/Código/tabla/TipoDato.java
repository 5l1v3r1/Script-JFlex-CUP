package tabla;

/**
 * Enumeración con los tipos de dato reconocidos en el lenguaje a analizar.
 */
public enum TipoDato {

    /**
     * Tipo de dato booleano (<i>boolean</i>).
     */
    booleano ("boolean"),
    /**
     * Tipo de dato entero (<i>integer</i>).
     */
    entero ("integer"),
    /**
     * Tipo de dato pedido para representar el nombre del programa
     */
    pseudo ("pseudo"),
    /**
     * Tipo de dato no válido (no está reconocido en el lenguaje, pero se usa
     * para devolver valores nulos, como en {@link TablaSímbolos}).
     */
    nulo ("indefinido");

/* -------------------------------------------- */
/* ---- FIN DE LA DECLARACIÓN DE ELEMENTOS ---- */
/* -------------------------------------------- */

    /**
     * Cadena con el nombre del identificador (la cadena que pondrá en el
     * código fuente a analizar).
     */
    private final String nombre;

    /**
     * Constructor para los elementos de la enumeración.
     *
     *
     * @param nombre
     *              Cadena con el nombre del tipo de dato.
     */
    private TipoDato (String nombre) {

        this.nombre = nombre;
    }

    public String verNombre () {

        return nombre;
    }

    /**
     * Obtiene el tipo de dato que se corresponde con la cadena pasada.
     *
     *
     * @param cadena
     *              Cadena con el nombre del tipo de dato para buscar entre los
     *          valores.
     *
     * @return
     *              El tipo de dato correspondiente, o {@code TipoDato.nulo} si no
     *          se ha encontrado.
    */
    public static TipoDato obtenerTipo (String cadena) {

        for (TipoDato t : TipoDato.values()) {

            if (t.verNombre().equals(cadena)) {

                return t;
            }
        }

        return TipoDato.nulo;
    }
}
