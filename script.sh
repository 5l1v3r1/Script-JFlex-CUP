#!/bin/bash

COMPIL=javac	# Compilador
INTERP=java	# Intérprete

OUTDIR="class"	# Directorio en el que se crean los archivos
SRCDIR="src"	# Directorio en el que copiar los archivos .java generados



# Inicializa las variables necesarias con sus valores por defecto
DIR="$PWD" 			# Directorio de compilación
CP="java-cup-11a.jar:$OUTDIR"	# Classpath
ARGS=""				# Argumentos para el archivo final
NOMBRE_LEX="Yylex.lex"		# Nombre del archivo JFlex
NOMBRE_CUP=""			# Vacío, por defecto

MAIN="Parser"	# Nombre del analizador síntáctico (la clase principal de CUP)
SYM="Sym"	# Nombre de la clase con los símbolos (para CUP)



# Texto de ayuda del uso del script
AYUDA="
Script para la compilación y ejecución de un analizador con JFlex y CUP.
Miguel García Martín (Foo-Manroot) <miguel.garciamartin@hotmail.com> - 2016
v. 1.1

Llamada correcta:
$0 [-opciones | --opciones] [-a | --args][argumentos]

Estando disponibles las siguientes opciones:
	-a
	--args
		 Establece las opciones que se pasarán al ejecutar el archivo resultante
		de la compilación, si es que se necesitan. Todo lo que venga después
		de -a (o --args) se tomará como argumentos y se pasarán directamente.
		Si son varios elementos, deben separarse con dos puntos, ':'

	-c
	--cup
		 Especifica el fichero de especificación sintáctica para CUP. Si no se
		especifica ninguno, sólo se generará y compilará la parte correspondiente
		al análisis léxico (con JFlex).

	-d
	--dir
		 Indica el directorio en el que se encuentran los archivos .lex y .cup
		(si no se especifica nada, se toma por defecto el directorio actual).

	-h
	--help
		  Muestra esta ayuda y termina la ejecución

	-j
	--jflex
		 Especifica el nombre del archivo jlex a compilar
		($NOMBRE_LEX, por defecto).

	-l
	--limpiar
	--clean
		 Elimina todos los archivos generados por este script (las carpetas
		$OUTDIR y $SRCDIR).

	-m
	--main
		 Especifica el nombre de la clase principal del analizador sintáctico.
		($MAIN, por defecto).

	-p
	--classpath
		 Establece el classpath para que java pueda ejecutar los archivos de
		salida. Por defecto es \"$CP\" (necesario para CUP). Si se quieren poner
		varias rutas, deben separarse con dos puntos -> \"path1:path2:path3\".

	-s
	--sym
		 Establece el nombre del archivo con la tabla de símbolos para CUP
		($SYM, por defecto).
"

# Opciones en formato corto y largo para getopt
OP_CORTAS=ac:d:hj:lm:s:p:
OP_LARGAS=args,cup:,dir:,help,jflex:,limpiar,clean,main:,sym:,classpath:

# Función sin terminar para procesar las opciones a mano y malamente por si getopt falla
args_a_mano ()
{
	# Ya si eso algún día lo haré bien... (aunque se supone que la mayoría de los
	# sistemas soportan getpot)
	echo -e "
		$0: Error - No se pueden obtener los argumentos ('getopt --test' falló,"\
		" seguramente porque el sistema no es compatible).

		El script se puede seguir usando, pero con los valores por defecto.
		También se pueden cambiar estos valores directamente en el script.
		"
	exit -1;
}

# Comprueba los argumentos y establece las variables de manera acorde
comprobar_args ()
{

	# Comprueba que se puede usar getopt para obtener las opciones
	getopt --test > /dev/null
	if [[ $? != 4 ]]
	then
		args_a_mano "$@"
	fi

	# Guarda el resultado para manejar correctamente los errores
	salida=$(getopt --options $OP_CORTAS --longoptions $OP_LARGAS \
		 --name "$0" -- "$@")

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
				shift 2;
				IFS=':' read -r -a ARGS <<< "$@"
				break;;

			-c|--cup)
				NOMBRE_CUP="$2"

				shift 2;;


			"-d"|--dir)
				# Comprueba que exista el directorio
				if [ -d "$2" ]
				then
					# Elimina el carácter final, /, si existe
					DIR="${2%/}"
				else
					echo -e "$0: Error - El directorio '$2' no " \
						"existe." >&2
					exit -1;
				fi

				shift 2;;

			"-j"|--jflex)
				NOMBRE_LEX="$2"

				shift 2;;

			-l|--clean|--limpiar)
				echo -e "-> Eliminando carpeta '$OUTDIR'"
				rm -rf "$OUTDIR"
				echo -e "-> Hecho <- "
				echo -e "-> Eliminando carpeta '$SRCDIR'"
				rm -rf "$SRCDIR"
				echo -e "-> Hecho <- "

				echo "Tareas terminadas."

				exit 1;;

			-m|--main)
				MAIN="$2"

				shift 2;;

			-s|--sym)
				SYM="$2"

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
			echo "$0: Error no identificado al interpretar los argumentos"\
				" - $1" >&2
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
	salida=$(jflex -v "$NOMBRE_LEX" -d "$SRCDIR")
	ret_val=0

	if [[ "$salida" =~ .error. ]]
	then
		echo -e "Error al crear el archivo .java: \n $salida" >&2
		exit -1;
	fi

	# La salida de jflex proporciona información sobre el nombre del archivo .java
	# La última línea debería ser Writing code to "X.java" (con las comillas)
	NOMBRE=$(echo -e "$salida" | grep -P "Writing code to .*" | awk -F \" '{print$2}')

	# Comprueba que se haya creado correctamente
	if [ ! -f "$NOMBRE" ]
	then
		echo -e "Error creando el archivo '$NOMBRE' con JFlex" >&2
		return -1;
	else
		echo -e "Archivo '$NOMBRE' creado correctamente."
	fi

	# Se queda con el nombre sin la extensión ni la ruta (se usa luego para
	#ejecutar el archivo .class)
	NOMBRE=$(echo "$NOMBRE" | awk -F / '{print $2}')
	NOMBRE="${NOMBRE%.*}"

	# Crea los archivos fuente con CUP (si se ha especificado)
	if [ "$NOMBRE_CUP" ]
	then
		echo -e "\n-Parte 2: CUP"
		# Fuerza a que los archivos de salida se llamen Parser.java y Sym.java
		salida=$(cup -destdir "$SRCDIR" -parser "$MAIN" -symbols "$SYM" \
			 "$NOMBRE_CUP" 2>&1)

		if [[ "$salida" =~ .*Error.* ]]
        	then
	                echo -e "Error al crear el archivo .java: \n $salida" >&2
        	        exit -1;
	        fi

		# Comprueba que se hayan creado correctamente los archivos
		# Parser.java y Sym.java
		if [ ! -f "$SRCDIR/$MAIN.java" ] && [ ! -f "$SRCDIR/$SYM.java" ]
		then
			echo -e "Error creando el archivo '$NOMBRE' con CUP" >&2
			return -1;
		else
			echo -e "Archivos $SRCDIR/$MAIN.java y $SRCDIR/$SYM.java"\
				" creados correctamente."
		fi
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
	salida=$("$COMPIL" -cp "$CP" -d "$OUTDIR" $(find "$SRCDIR" -name '*.java') \
		-Xdiags:verbose -Xlint:unchecked 2>&1)

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

		"$INTERP" -cp "$CP" "$RUTA" "${ARGS[@]}"
	else
		echo "$0: Error al intentar ejecutar $NOMBRE.class (no se ha"\
			" encontrado)" >&2
	fi
}

# Ejecuta el analizador sintáctico (para el que se necesita el léxico)
ejecutar_sin ()
{
	RUTA=$(find . -name "$MAIN.class")

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

		"$INTERP" -cp "$CP" "$RUTA" ${ARGS[@]}
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
	if [ ! -d "$SRCDIR" ]
	then
		mkdir --parents "$SRCDIR" -v
	fi

	if [ ! -d "$OUTDIR" ]
	then
		mkdir --parents "$OUTDIR" -v
	fi

	# Crea los archivos fuente (.java)
	if crear_fuentes
	then
		echo -e "\n--------------"
		echo "Archivos fuente creados"

		# Copia todos los archivos .java auxiliares que pueda haber en
		# la carpeta actual en la carpeta src/
		aux=$(find . -name "*.java" -not -path "*$SRCDIR*" 2>/dev/null | wc -l)
		if [ $aux != 0 ]
		then
			echo "Copiando los archivos .java auxiliares..."
			cp --parents -v $(find . -name '*.java'\
				-not -path '*'"$SRCDIR"'*') "$SRCDIR"
		fi

		# Compila para crear los archivos .class
		if compilar
		then
			echo -e "\n--------------"
			echo -e "\nTareas terminadas\n"

			# Si se ha creado correctamente, intenta ejecutar el archivo
			echo "¿Ejecutar el programa? (introducir el número de la"\
			     " respuesta elegida)"
			select respuesta in "Sí" "No"
			do
				case $respuesta in
			       		"Sí" )
						# Si se ha especificado un archivo .cup,
						# busca Parser.class y lo ejecuta.
						# Si no, busca el archivo generado con
						# JFlex.
						if [ "$NOMBRE_CUP" ]
						then
							ejecutar_sin
						else
							ejecutar_lex
						fi

						break;;

				        "No" )
						echo -e "Para ejecutar el archivo, usar"\
							" la orden 'java [-cp $CP]"\
							" $MAIN [-a $ARGS]'"
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
