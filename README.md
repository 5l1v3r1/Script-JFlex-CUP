# Script-JFlex
Script para compilar archivos de especificación léxica con JFlex y ejecutarlos con ficheros de prueba si se quiere.

# Requerimientos
Para poder funcionar correctamente necesita JFlex y jdk (para usar javac y java)

# Uso
Para ver la ayuda se puede usar la opción -h o --help. La salida es la siguiente:
<pre>
Script para la compilación y ejecución de un archivo de especificación léxica con JFlex.
Miguel García Martín - 2016

Llamada correcta:
./script.sh [-opciones | --opciones] [-n | --name] [fichero_lex] [-a | --args][argumentos]

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
		 Especifica el nombre del archivo jlex a compilar
</pre>
