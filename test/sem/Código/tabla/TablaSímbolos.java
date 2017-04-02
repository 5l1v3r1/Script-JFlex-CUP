package tabla;

import common.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para realizar un seguimiento de los identificadores encontrados en el
 * análisis sintáctico.
 */
public class TablaSímbolos {

	/**
	 * Tabla para almacenar todos los identiicadores.
	 *
	 * <p>
	 * Clave -> cadena con el identificador en minúsculas.<br>
	 * Valor -> información sobre la variable.
	 */
	private final HashMap <String, Info> identificadores;

        /**
         * Tabla con los símbolos que no son identificadores
         */
        private final HashMap <String, ArrayList<Integer>> símbolos;

 	/**
	 * Guarda el último tipo de dato utilizado (para las listas de variables).
	 */
	private TipoDato últimoTipo;

	/**
	 * Constructor.
	 */
	public TablaSímbolos () {

		identificadores = new HashMap<> ();
                símbolos = new HashMap<>();

		últimoTipo = TipoDato.nulo;
	}

        /**
         * Crea una nueva tabla a partir de los valores comunes entre las dos
         * pasadas como argumento.
         *
         * @param tabla1
         *
         * @param tabla2
         *
         * @return
         *             Una nueva tabla con la información (variables y tipos
         *          y valores de las mismas) común en las dos tablas.
         */
        public static TablaSímbolos fusionarTablas (TablaSímbolos tabla1,
                                                    TablaSímbolos tabla2) {

            TablaSímbolos ret_val = new TablaSímbolos ();

            /* Recorre una de las tablas buscando los valores que están en
            la segunda de las tablas */
            for (Map.Entry <String, Info> entrada : tabla1.identificadores.entrySet ()) {

                String id = entrada.getKey ();
                Info valor = entrada.getValue ();
                Info aux;

                if ((aux = tabla2.obtenerID (id)) != null) {

                    /* Compara los valores y los tipos */
                    if (aux.verTipo ().equals (valor.verTipo ())) {

                        if ((aux.verValor () == null)
                            && (valor.verValor () == null)) {

                            /* Si la variable se ha usado en alguna
                            de las tablas, se muestra como usada en la
                            nueva */
                            if (aux.usada()) {

                                valor.usar ();
                            }

                            ret_val.añadirEntrada (id, valor);
                        } else {

                            if (aux.verValor ().equals (valor.verValor ())) {

                                /* Si la variable se ha usado en alguna
                                de las tablas, se muestra como usada en la
                                nueva */
                                if (aux.usada()) {

                                    valor.usar ();
                                }

                                ret_val.añadirEntrada (id, valor);
                            }
                        }
                    }
                }
            }

            if (tabla1.últimoTipo == tabla2.últimoTipo) {

                ret_val.últimoTipo = tabla1.últimoTipo;
            }

            return ret_val;
        }


	/**
	 * Añade la entrada a la tabla para el seguimiento.
	 *
	 *
	 * @param identificador
	 *			  Cadena con el identificador.
	 *
	 * @param tipo
	 *			  Tipo de dato ("boolean", "integer" o cualquiera de los
	 *		  reconocidos en {@link TipoDato}).
	 *
	 *
	 * @return
	 *			  <i>true</i> si la clave no estaba ya guardada. Si el mapa ya
	 *		  contenía este identificador, devuelve <i>false</i>.
	 */
	public boolean añadirID (String identificador, TipoDato tipo) {

		Info info;

		identificador = identificador.toLowerCase ();

		últimoTipo = tipo;

		/* Valor inicial -> null */
		info = new Info (tipo, null);

		if (identificadores.containsKey (identificador)) {

			return false;
		} else {

			identificadores.put (identificador, info);
			return true;
		}
	 }


        /**
         * Añade el símbolo a la tabla {@link #símbolos}.
         *
         * @param id
         *              Cadena con el símbolo a añadir
         *
         * @param línea
         *              Línea en la que se ha encontrado
         */
        public void añadirSímbolo (String id, Integer línea) {

            ArrayList <Integer> líneas;

            /* Si ya estaba en la lista, obtiene el valor y añade una línea */
            if (símbolos.containsKey(id)) {

                símbolos.get (id).add (línea);
            } else {

                líneas = new ArrayList<>();
                líneas.add (línea);

                símbolos.put (id, líneas);
            }
        }

	/**
	 * Añade un identificador con el último tipo de dato añadido.
	 *
	 *
	 * @param identificador
	 *			  Cadena con el identificador.
	 *
	 *
	 * @return
	 *			  <i>true</i> si la clave no estaba ya guardada. Si el mapa ya
	 *		  contenía este identificador, devuelve <i>false</i>.
	 */
	public boolean añadirID (String identificador) {

		Info info;

		identificador = identificador.toLowerCase ();

		/* Valor inicial -> null */
		info = new Info (últimoTipo, null);

		if (últimoTipo.equals (TipoDato.nulo) ||
		    identificadores.containsKey (identificador)) {

			return false;
		} else {

			identificadores.put (identificador, info);
			return true;
		}
	}


        /**
         * Cambia el valor de {@link #últimoTipo}.
         *
         * @param nuevoTipo
         *              Nuevo valor del tipo de dato.
         */
        public void cambiarÚltimoTipo (TipoDato nuevoTipo) {

            this.últimoTipo = nuevoTipo;
        }


        /**
         * Devuelve el último tipo de dato introducido.
         *
         * @return
         *              El valor de {@link #últimoTipo}.
         */
        public TipoDato verÚltimoTipo () {

            return últimoTipo;
        }

	/**
	 * Tras detectar un error, devuelve el tipo de dato a "nulo" para evitar que
	 * se añadan los identificadores erróneos.
	 */
	public void errorTipoDato () {

		últimoTipo = TipoDato.nulo;
	}


	/**
	 * Obtiene el tipo de dato asociado con el identificador pasado como
	 * argumento.
	 *
	 *
	 * @param identificador
	 *			  Identificador usado para buscar el tipo de dato.
	 *
	 *
	 * @return
	 *			  Toda la información asociada a ese identificador; o
	 *		  null, si el identificador no estaba en la tabla.
	 */
	public Info obtenerID (String identificador) {

		identificador = identificador.toLowerCase ();

		if (identificadores.containsKey (identificador)) {

			return identificadores.get (identificador);
		} else {

			return null;
		}
	}

	/**
	 * Elimina de la lista el identificador seleccionado.
	 *
	 * @param identificador
	 *              Cadena con la clave para buscar dentro del mapa.
	 */
	public void eliminarID (String identificador) {

		identificadores.remove (identificador);
	}

	/**
	 * Busca el identificador pasado como argumento en la lista.
	 *
	 *
	 * @param identificador
	 *              Cadena con el identificador que se ha de buscar.
	 *
	 *
	 * @return
	 *              <i>true</i> si está en la lista; o <i>false</i> si no.
	 */
	public boolean buscarID (String identificador) {

		identificador = identificador.toLowerCase ();

		return identificadores.containsKey (identificador);
	}

        /**
         * Permite añadir una entrada a la tabla.
         */
        private void añadirEntrada (String identificador, Info información) {

            if (!identificadores.containsKey (identificador)) {

                identificadores.put (identificador, información);
            }
        }

        /**
         * Crea un nuevo objeto de tipo {@link TablaSímbolos} y copia el
         * contenido de la actual en la nueva.
         *
         * @return
         *              Un nuevo objeto con los mismos identificadores que
         *          la tabla original.
         */
        public TablaSímbolos obtenerCopia () {

            TablaSímbolos tabla = new TablaSímbolos ();

            for (Map.Entry<String, Info> entrada : identificadores.entrySet()) {

                String id = entrada.getKey ();
                Info info = new Info (entrada.getValue ().verTipo (),
                                      entrada.getValue ().verValor ());

                /* Marca las entradas como "inicializadas" o "usadas"
                si es necesario */
                if (entrada.getValue ().inicializada ()) {

                    info.inicializar ();
                }

                if (entrada.getValue ().usada ()) {

                    info.usar ();
                }

                tabla.añadirEntrada (id, info);
            }

            tabla.cambiarÚltimoTipo (últimoTipo);

            return tabla;
        }

        /**
         * Si hay alguna variable sin usar, lanza un aviso.
         */
        public void revisarDatos () {

            for (Map.Entry<String, Info> entrada : identificadores.entrySet()) {

                String id = entrada.getKey ();
                Info info = entrada.getValue ();

                if ((info != null) && !info.usada()) {

                    if (info.verTipo () != null
                        && !info.verTipo ().equals(TipoDato.pseudo)
                        && !info.verTipo ().equals(TipoDato.nulo)) {

                        Logger.aviso (null, "La variable '"
                                  + id + "' no se utiliza en el programa");
                    }
                }
            }
        }

        /**
         * Revisa los valores de la tabla la pasada como argumento y actualiza
         * los valores de los identificadores en esta tabla.
         *
         * @param tabla
         *              La tabla con los valores actualizados que se deben
         *          copiar a la actual.
         */
        public void actualizarTabla (TablaSímbolos tabla) {

            for (Map.Entry<String, Info> entrada : tabla.identificadores.entrySet()) {

                String id = entrada.getKey ();
                Info nuevo = entrada.getValue ();
                Info antiguo = identificadores.get (id);

                if (identificadores.containsKey (id)) {

                    /* Compara los valores y actualiza, si es necesario */
                    antiguo.cambiarValor (nuevo.verValor ());

                    if ( !antiguo.inicializada ()
                        && nuevo.inicializada ()) {

                        antiguo.inicializar ();
                    }

                    if ( !antiguo.usada ()
                        && nuevo.usada ()) {

                        antiguo.usar ();
                    }
                }
            }
        }

        /**
         * Revisa los valores de la tabla la pasada como argumento y actualiza
         * únicamente el atributo "usada".
         *
         * @param tabla
         *              La tabla con los valores actualizados que se deben
         *          copiar a la actual.
         */
        public void actualizarUsoVars (TablaSímbolos tabla) {

            for (Map.Entry<String, Info> entrada : tabla.identificadores.entrySet()) {

                String id = entrada.getKey ();
                Info nuevo = entrada.getValue ();
                Info antiguo = identificadores.get (id);

                if (identificadores.containsKey (id)) {

                    /* Compara los valores y actualiza, si es necesario */
                    if (nuevo.usada ()) {

                        antiguo.usar ();
                    }
                }
            }
        }

	/**
	 * Imprime todos los identificadores (con su tipo) que hay en la tabla.
	 */
	public void imprimirTabla () {

            if (!símbolos.isEmpty() || !identificadores.isEmpty()) {

                System.out.println ("\n---------------------");
                System.out.println ("Tabla de símbolos: ");
                System.out.println ("---------------------");
            }

            if (!símbolos.isEmpty()) {

                System.out.println ("Símbolos: ");

                for (Map.Entry<String, ArrayList<Integer>> entry : símbolos.entrySet ()) {

                        String id = entry.getKey();
                        ArrayList<Integer> líneas = entry.getValue ();

                        System.out.printf ("\tSímbolo: %20s -> líneas ",
                                            id);

                        for (Integer i : líneas) {

                            System.out.printf ("%3s ", i);
                        }
                        System.out.println ();
                }

                System.out.println ();
            }

            if (!identificadores.isEmpty ()) {

                System.out.println ("Variables: ");

                for (Map.Entry<String, Info> entry : identificadores.entrySet ()) {

                        String id = entry.getKey();
                        Info info = entry.getValue ();

                        System.out.println ("\tIdentificador: " + id
                                            + "\n" + info.toString ()
                                            + "\n");
                }
            }
	}
}
