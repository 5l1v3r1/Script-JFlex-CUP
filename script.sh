JAR=/media/foo-manroot/HDD_Varios/Programas/Java/bin/jar # Ubicación del binario jar
FICHERO=ficheroPrueba.txt # Fichero de prueba

# Texto de ayuda del uso del script
AYUDA="
Script para la compilación y ejecución de un archivo de especificación léxica con JFlex.
Miguel García Martín - 2016

Llamada correcta:
$0 [-opciones | --opciones] [-n | --name] [fichero_lex] [-a | --args][argumentos]

Estando disponibles las siguientes opciones:
	-a
	--args
		 Establece las opciones que se pasarán al ejecutar el archivo resultante
		de la compilación, si es que se necesitan. Todo lo que venga después de
		-a (o --args) se tomará como argumentos y se pasarán directamente.
	-d
	--dir
		 Indica el directorio en el que se encuentra el archivo .lex (si no se
		especifica nada, se toma por defecto el directorio actual).
	-h
	--help
		  Muestra esta ayuda y termina la ejecución
	-n
	--nombre
		 Especifica el nombre del archivo jlex a compilar (YYlex, por defecto)
"

# Opciones en formato corto y largo para getopt
OP_CORTAS=d:fhn:
OP_LARGAS=args,help,dir:,name:

# Función sin terminar para procesar las opciones a mano y malamente por si getopt falla
args_a_mano ()
{

	# Ya si eso algún día lo haré bien... (aunque se supone que la mayoría de los sistemas soportan getpot)
	echo -e "
		Error: No se pueden obtener los argumentos ('getopt --test' falló, seguramente porque el sistema no es compatible).

		El script se puede seguir usando, pero con los valores por defecto.
		"
	exit -1;
	# Bucle para comprobar los argumentos
#	for arg in $@
#	do
#		case "$arg" in
#
#			-d)
#				# Comprueba la existencia del directorio siguiente
#
#			-h | --help)
#				# Muestra la ayuda y sale
#				echo -e "$AYUDA"
#				exit;
#
#
#		esac
#	done
#	exit 0;
#
#	if [ $# -gt 0 ]
#	then
#
#		if [ -d $1 ]
#		then
#			# Elimina el carácter final /, si existe
#			DIR="${1%/}"
#			cd "$DIR"
#			CP="$DIR"/class
#		else
#			echo -e "El directorio $1 no existe.\n"
#			exit -1;
#		fi
#	fi
}

# Comprueba los argumentos y establece las variables de manera acorde
comprobar_args ()
{
	# Inicializa las variables necesarias con sus valores por defecto
	DIR=$PWD 	# Directorio de compilación
	CP=$DIR/class 	# Classpath
	ARGS=""		# Argumentos para el archivo final
	NOMBRE="Yylex" # Nombre del archivo

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
	while true; do
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

			"-d"|--dir)
				# Comprueba que exista el directorio
				if [ -d "$2" ]
				then
					# Elimina el carácter final, /, si existe
					DIR="${2%/}"
					cd "$DIR"
					CP="$DIR"/class
				else
					echo -e "$0: Error - El directorio $2 no existe.\n"
					exit -1;
				fi

				shift 2;;

			"-n"|--name)
				# Comprueba que exista el fichero
				if [ -f "$2" ]
				then
					# Elimina la extensión, si existe
					NOMBRE="${2%.lex}"
				else
					echo -e "$0: Error - El fichero $2 no existe.\n"
					exit -1;
				fi

				shift 2;;

			--)
				# Sale del bucle (ya ha acabado con los argumentos)
				shift
				break;;
		*)
			echo "$0: Error no identificado - $1"
			exit -3;;
		esac
	done
}

# Ejecuta las funciones principales para la compilación y ejecución (si se quiere) del analizador
main ()
{
	# Comprueba la existencia de los directorios necesarios
	if [ ! -d src ]
	then
		mkdir --parents src/yylex -v
	fi

	if [ ! -d class ]
	then
		mkdir --parents class/yylex -v
	fi

	#if [ ! -d dist ]
	#then
	#	mkdir --parents /dist -v
	#fi

	rm -f "$NOMBRE".java

	echo "Creando archivo .java"
	salida=`jflex -v "$NOMBRE"`

	if [[ "$salida" =~ .error. ]]
	then

		echo -e "Error al crear el archivo .java: \n $salida"
		exit -1;
	else
		echo -e "Archivo .java creado correctamente. \n $salida"
	fi

	if [ -f "$NOMBRE".java ]
	then
		echo -e "\n--------------"
		echo "Archivo creado"

		# Se mueve a la carpeta adecuada
		mv -v "$NOMBRE".java src/yylex

		echo -e "\n--------------"
		echo -e "Creando el archivo .class...\n"
		# Crea el archivo .class (redirecciona stderr a stdout
		# y almacena la salida en una variable)
		salida=`javac -cp "$CP" -d class src/yylex/"$NOMBRE".java -Xdiags:verbose -Xlint:unchecked 2>&1`

		# Comprueba si hay errores
		if [[ "$salida" =~ .error. ]]
		then
		echo -e "Errores en el código: \n $salida"
			exit -1;
		else
			echo -e "Compilación correcta \n $salida"
		fi

		# Crea el ejecutable .jar
	#	$JAR cvfe dist/$NOMBRE.jar yylex.$NOMBRE class/yylex/$NOMBRE.class

		# Si existe el fichero de prueba, lo copia al directorio adecuado
		if [ -f "$FICHERO" ]
		then
			cp -v "$FICHERO" class
		fi

		echo -e "\n--------------"
		echo -e "\nTareas terminadas\n"

		# Si se ha creado correctamente, intenta ejecutar el archivo
		echo "¿Ejecutar el programa? (introducir el número de la respuesta elegida)"
		select respuesta in "Sí" "No"
		do
			case $respuesta in
		       		"Sí" )
					if [ -f class/yylex/"$NOMBRE".class ]
					then
						cd class
						echo -e "\n--------------"
						echo -e "Ejecutando el archivo $NOMBRE.class"
						echo -e "--------------\n"

						java yylex."$NOMBRE" "$ARGS"
					else
						echo -e "No existe el fichero $DIR/class/yylex/$NOMBRE.class"
					fi
					break;;

			        "No" )
					echo -e "Para ejecutar el archivo, usar la orden \"java yylex.Yylex\" en el directorio class/"
					exit;;
			esac
		done
	else
		echo -e "Error al crear el archivo\n"
	fi
}


# Primero comprueba los argumentos y luego compila y ejecuta el programa
comprobar_args "$@"
main
