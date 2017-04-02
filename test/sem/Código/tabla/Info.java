package tabla;

/**
 * Proporciona información sobre una variable, como su tipo y su valor.
 */
public class Info {

	/**
	 * Tipo de dato de la variable.
	 */
	private TipoDato tipo;

	/**
	 * Valor actual de la variable.
	 */
	private String valor;

        /**
         * En el caso de las variables, permite saber si han sido inicializadas
         */
        private boolean inicializada = false;

        /**
         * En el caso de las variables, permite saber si han sido usadas
         */
        private boolean usada = false;

	/**
	 * Constructor.
	 *
	 * @param tipo
	 *			Tipo de dato de la variable.
	 *
	 * @param valor
	 *			Valor de inicio de la variable (puede ser
	 *		null, si aún no se ha inicializado).
	 */
	public Info (TipoDato tipo, String valor) {

		this.tipo = tipo;
		this.valor = valor;
	}

        /**
	 * Permite saber si una variable ha sido inicializada.
	 *
	 * @return
	 *			El valor de {@link #inicializada}.
	 */
	public boolean inicializada () {

		return inicializada;
	}


        /**
	 * Permite saber si una variable ha sido usada.
	 *
	 * @return
	 *			El valor de {@link #usada}.
	 */
	public boolean usada () {

		return usada;
	}

	/**
	 * Devuelve el tipo de dato de la variable.
	 *
	 * @return
	 *			El valor de {@link #tipo}.
	 */
	public TipoDato verTipo () {

		return tipo;
	}


	/**
	 * Devuelve el valor actual de la variable.
	 *
	 * @return
	 *			El valor de {@link #valor}.
	 */
	public String verValor () {

		return valor;
	}

	/**
	 * Cambia el valor guardado actualmente para la variable.
	 *
	 * @param nuevoValor
	 *			Nuevo valor para {@link #valor}.
	 */
	public void cambiarValor (String nuevoValor) {

		this.valor = nuevoValor;
	}

        /**
	 * Cambia el tipo de dato guardado actualmente para la variable.
	 *
	 * @param nuevoTipo
	 *			Nuevo valor para {@link #tipo}.
	 */
	public void cambiarTipo (TipoDato nuevoTipo) {

		this.tipo = nuevoTipo;
	}

        /**
	 * Permite marcar la variable como usada.
	 */
	public void usar () {

		this.usada = true;
	}

        /**
	 * Permite marcar la variable como inicializada.
	 */
	public void inicializar () {

		this.inicializada = true;
	}

	/**
	 * Devuelve una cadena con la información del objeto.
         * @return
	 */
	@Override
	public String toString () {

            StringBuilder mensaje = new StringBuilder ("\t\tTipo: "
                                                       + tipo.verNombre ());

            if (valor != null) {

                mensaje.append("\n\t\tValor: ")
                       .append (valor);
            } else {

                if ( !tipo.equals (TipoDato.pseudo)
                    && !tipo.equals (TipoDato.nulo) ) {

                    mensaje.append("\n\t\tValor: ")
                           .append ("indeterminado");
                }
            }

            if (!inicializada) {

                if ( !tipo.equals (TipoDato.pseudo)
                    && !tipo.equals (TipoDato.nulo) ) {

                    mensaje.append ("\n\t\tSin inicializar");
                }
            }

            if (!usada) {

                if ( !tipo.equals (TipoDato.pseudo)
                    && !tipo.equals (TipoDato.nulo) ) {

                    mensaje.append ("\n\t\tSin usar");
                }
            }

            return new String (mensaje);
	}
}
