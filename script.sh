# Texto de ayuda del uso del script
AYUDA="
Script para la compilación y ejecución de un analizador con JFlex y CUP.
Miguel García Martín (Foo-Manroot) <miguel.garciamartin@hotmail.com> - 2016

Llamada correcta:
$0 [-opciones | --opciones] [-n | --name] [fichero_lex] [-a | --args][argumentos]

Estando disponibles las siguientes opciones:
	-a
	--args
		 Establece las opciones que se pasarán al ejecutar el archivo resultante de
		la compilación, si es que se necesitan. Todo lo que venga después de -a (o
		--args) se tomará como argumentos y se pasarán directamente.

	-c
	--cup
		 Especifica el fichero de especificación semántica para CUP. Si no se especifica
		ninguno, sólo se generará y compilará la parte correspondiente al análisis léxico
		(con JFlex).

	-d
	--dir
		 Indica el directorio en el que se encuentra el archivo .lex (si no se especifica
		nada, se toma por defecto el directorio actual).

	-f
	--file
		 Especifica el fichero que se ha de copiar a la carpeta class/ para que el
		analizador pueda trabajar con él.

	-h
	--help
		  Muestra esta ayuda y termina la ejecución

	-j
	--jflex
		 Especifica el nombre del archivo jlex a compilar (YYlex.lex, por defecto).

	-p
	--classpath
		 Establece el classpath para que java pueda ejecutarla (necesario para CUP).
		Por defecto es \"java-cup-11a.jar\". Si se quieren poner varias rutas, deben
		separarse con dos puntos -> \"path1:path2:path3\".
"

# Opciones en formato corto y largo para getopt
OP_CORTAS=ac:d:f:hj:p:
OP_LARGAS=args,cup:,dir:,file:,help,jflex:,classpath:

# Función sin terminar para procesar las opciones a mano y malamente por si getopt falla
args_a_mano ()
{

	# Ya si eso algún día lo haré bien... (aunque se supone que la mayoría de los sistemas soportan getpot)
	echo -e "
		$0: Error - No se pueden obtener los argumentos ('getopt --test' falló, seguramente porque el sistema no es compatible).

		El script se puede seguir usando, pero con los valores por defecto.
		"
	exit -1;
}

# Comprueba los argumentos y establece las variables de manera acorde
comprobar_args ()
{
	# Inicializa las variables necesarias con sus valores por defecto
	DIR="$PWD" 			# Directorio de compilación
	CP="java-cup-11a.jar"		# Classpath
	ARGS=""				# Argumentos para el archivo final
	NOMBRE_LEX="Yylex.lex"		# Nombre del archivo JFlex
	NOMBRE_SEM=""			# Vacío, por defecto
	FICHERO=""			# Fichero de prueba

	# Comprueba que se puede usar getopt para obtener las opciones
	getopt --test > /dev/null
	if [[ $? != 4 ]]
	then
		args_a_mano "$@"
	fi

	# Guarda el resultado para manejar correctamente los errores
	salida=`getopt --options $OP_CORTAS --longoptions $OP_LARGAS --name "$0" -- "$@"`

	if [[ $? != 0 ]]; then

		# Getopt devolvió error (argumentos desconocidos o mal usados)
		exit -2
	fi
	eval set -- "$salida"

	# Bucle para evaluar todos los argumentos disponibles
	while true
	do
		case "$1" in
			-h|--help)
				# Muestra la ayuda y sale
				echo -e "$AYUDA"
				exit 1;;

			-a|--args)
				#  Avanza en los argumentos, se queda con el resto como
				# los argumentos para el archivo final y sale del bucle
				shift 2
				ARGS="$@"
				break;;

			-c|--cup)
				# Comprueba que exista el fichero
				if [ -f "$2" ]
                                then
                                        NOMBRE_SEM="$2"
                                else
                                        echo -e "$0: Error - El fichero $2 no existe.\n" >&2
                                        exit -1;
                                fi

				shift 2;;


			"-d"|--dir)
				# Comprueba que exista el directorio
				if [ -d "$2" ]
				then
					# Elimina el carácter final, /, si existe
					DIR="${2%/}"
				else
					echo -e "$0: Error - El directorio $2 no existe.\n" >&2
					exit -1;
				fi

				shift 2;;

			"-f"|--file)
				# Comprueba que exista el fichero
                                if [ -f "$2" ]
                                then
					# Obtiene la ruta absoluta
                                        FICHERO=`readlink -f "$2"`
                                else
                                        echo -e "$0: Error - El fichero $2 no existe.\n" >&2
                                        exit -1;
                                fi

                                shift 2;;


			"-j"|--jflex)
				# Comprueba que exista el fichero
				if [ -f "$2" ]
				then
					NOMBRE_LEX="$2"
				else
					echo -e "$0: Error - El fichero $2 no existe.\n" >&2
					exit -1;
				fi

				shift 2;;

			-p|--classpath)
				# Sustituye la ruta por defecto
				CP="$2"
				shift 2;;

			--)
				# Sale del bucle (ya ha acabado con los argumentos)
				shift
				break;;
		*)
			echo "$0: Error no identificado - $1" >&2
			exit -3;;
		esac
	done

	# Cambia al directorio seleccionado, si es necesario
	cd "$DIR"
}

# Crea los archivos .java a partir de los archivos de especificación léxica y semántica
crear_fuentes ()
{
	echo "Creando archivos .java"
	echo -e "\n-Parte 1: JFlex"
	salida=$(jflex -v "$NOMBRE_LEX" -d src/)
	ret_val=0

	if [[ "$salida" =~ .error. ]]
	then
		echo -e "Error al crear el archivo .java: \n $salida" >&2
		exit -1;
	fi

	# La salida de jflex proporciona información sobre el nombre del archivo .java
	# La última línea debería ser Writing code to "XXX.java" (con las comillas)
	NOMBRE=$(echo -e "$salida" | grep -P "Writing code to .*" | awk -F \" '{print$2}')

	# Comprueba que se haya creado correctamente
	if [ ! -f "$NOMBRE" ]
	then
		echo -e "Error creando el archivo $NOMBRE con JFlex" >&2
		return -1;
	else
		echo -e "Archivo $NOMBRE creado correctamente."
	fi

	# Se queda con el nombre sin la extensión ni la ruta (se usa luego para
	#ejecutar el archivo .class)
	NOMBRE=$(echo "$NOMBRE" | awk -F / '{print $2}')
	NOMBRE="${NOMBRE%.*}"

	# Crea los archivos fuente con CUP (si se ha especificado)
	if [ "$NOMBRE_SEM" ]
	then
		echo -e "\n-Parte 2: CUP"
		# Fuerza a que los archivos de salida se llamen Parser.java y Sym.java
		salida=$(cup -destdir src/ -parser Parser -symbols Sym "$NOMBRE_SEM" 2>&1)

		if [[ "$salida" =~ .Error. ]]
        	then
	                echo -e "Error al crear el archivo .java: \n $salida" >&2
        	        exit -1;
	        fi
	fi

	# Comprueba que se hayan creado correctamente los archivos Parser.java y Sym.java
	if [ ! -f "src/Parser.java" ] && [ ! -f "src/Sym.java" ]
	then
		echo -e "Error creando el archivo $NOMBRE con CUP" >&2
		return -1;
	else
		echo -e "Archivos src/Parser.java y src/Sym.java creados correctamente."
	fi

	return 0
}

# Crea los archivos .class (jvm bytecode) a partir de los fuentes generados en /src
compilar ()
{
	echo -e "\n--------------"
	echo -e "Creando archivos .class...\n"

	# Crea el archivo .class (redirecciona stderr a stdout
	# y almacena la salida en una variable)
	salida=$(javac -cp "$CP" -d class src/*.java -Xdiags:verbose -Xlint:unchecked 2>&1)

	# Comprueba si hay errores
	if [[ "$salida" =~ .error. ]]
	then
		echo -e "Errores en el código: \n $salida"
		return -1;
	else
		echo -e "Compilación correcta \n $salida"
	fi

	return 0;
}

# Ejecuta solamente el analizador léxico
ejecutar_lex ()
{
	RUTA=$(find . -name "$NOMBRE.class")

	# Cambia las barras por puntos para obtener la ruta y elimina el principio
	# (./class/) y la extensión (.class) para obtener el nombre de la clase
	RUTA="${RUTA#./class/}"
	RUTA="${RUTA%.class}"

	RUTA=$(echo -e "$RUTA" | sed -e 's/\//./g')

	if [ "$RUTA" ]
	then
		echo -e "\n--------------"
		echo -e "Ejecutando $RUTA"
                echo -e "--------------\n"

		java -cp "$CP":class "$RUTA" "$ARGS"
	else
		echo "$0: Error al intentar ejecutar $NOMBRE.class (no se ha encontrado)" >&2
	fi
}

# Ejecuta el analizador semántico (para el que se necesita el léxico)
ejecutar_sem ()
{
	RUTA=$(find . -name Parser.class)

	# Cambia las barras por puntos para obtener la ruta y elimina el principio
	# (./class/) y la extensión (.class) para obtener el nombre de la clase
	RUTA="${RUTA#./class/}"
	RUTA="${RUTA%.class}"

	RUTA=$(echo -e "$RUTA" | sed -e 's/\//./g')

	if [ "$RUTA" ]
	then
		echo -e "\n--------------"
		echo -e "Ejecutando $RUTA"
                echo -e "--------------\n"

		java -cp "$CP":class "$RUTA" "$ARGS"
	else
		echo "$0: Error al intentar ejecutar Parser.class (no se ha encontrado)" >&2
	fi
}

# Ejecuta las funciones principales para la compilación y ejecución (si se quiere) del analizador
main ()
{
	#  Comprueba la existencia de los directorios necesarios.
	# El árbol de directorios final debe quedar como sigue:
	#	$CWD
	#	|-------src/
	#	|	|____ ... (archivos .java)
	#	|
	#	|-------class/
	#		|____ ... (archivos .class)
	if [ ! -d src ]
	then
		mkdir --parents src -v
	fi

	if [ ! -d class ]
	then
		mkdir --parents class -v
	fi

	# Crea los archivos fuente (.java)
	if crear_fuentes
	then
		echo -e "\n--------------"
		echo "Archivos fuente creados"

		# Compila para crear los archivos .class
		if compilar
		then
			# Si existe el fichero de prueba, lo copia al directorio adecuado
#			if [ "$FICHERO" != "" -a -f "$FICHERO" ]
#			then
#				cp -v "$FICHERO" class
#			fi

			echo -e "\n--------------"
			echo -e "\nTareas terminadas\n"

			# Si se ha creado correctamente, intenta ejecutar el archivo
			echo "¿Ejecutar el programa? (introducir el número de la respuesta elegida)"
			select respuesta in "Sí" "No"
			do
				case $respuesta in
			       		"Sí" )
						# Si se ha especificado un archivo .cup, busca Parser.class
						# y lo ejecuta. Si no, busca el archivo generado con JFlex.
						if [ "$NOMBRE_SEM" ]
						then
							ejecutar_sem
						else
							ejecutar_lex
						fi

						break;;

				        "No" )
						echo -e "Para ejecutar el archivo, usar la orden 'java [-cp class] paquete.clase'"
						exit;;
				esac
			done

		else
			echo -e "$0: Error al compilar.\n" >&2
		fi

	else
		echo -e "$0: Error al crear los archivos fuente.\n" >&2
	fi
}


# Primero comprueba los argumentos y luego compila y ejecuta el programa
comprobar_args "$@"
main
