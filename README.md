# Script-JFlex-CUP
Script para generar analizadores léxicos o sintácticos a partir de unos ficheros de especificación con JFlex y CUP, además de ejecutarlos si se desea.

# Requerimientos
Para poder funcionar correctamente necesita tener instalados JFlex, CUP y el jdk (para usar javac y java).
Si se quiere ejecutar el analizador semántico también hace falta descargarse [un _.jar_ de CUP](http://www2.cs.tum.edu/projects/cup/). En este repositorio se incluye la versión 11a; pero se puede descargar otra versión si se desea (habrá que especificarlo con la opción '-p' o '--classpath').

# Uso
Para ver la ayuda se puede usar la opción -h o --help. La salida debería ser similar a la siguiente:
<pre>
Script para la compilación y ejecución de un analizador con JFlex y CUP.
Miguel García Martín (Foo-Manroot) miguel.garciamartin@hotmail.com - 2016

Llamada correcta:
./script.sh [-opciones | --opciones] [-n | --name] [fichero_lex] [-a | --args][argumentos]

Estando disponibles las siguientes opciones:
	-a
	--args
		 Establece las opciones que se pasarán al ejecutar el archivo resultante de
		la compilación, si es que se necesitan. Todo lo que venga después de -a (o
		--args) se tomará como argumentos y se pasarán directamente.

	-c
	--cup
		 Especifica el fichero de especificación sintáctica para CUP. Si no se especifica
		ninguno, sólo se generará y compilará la parte correspondiente al análisis léxico
		(con JFlex).

	-d
	--dir
		 Indica el directorio en el que se encuentran los archivos .lex y .cup (si no se especifica
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
		Por defecto es "java-cup-11a.jar". Si se quieren poner varias rutas, deben
		separarse con dos puntos -> "path1:path2:path3".
</pre>

# Ejecución y pruebas
## Analizador léxico
Se puede probar el script con los ficheros proporcionados en la carpeta test ejecutando
`./script.sh -d test/lex/ -j Yylex.lex`

La salida debería ser similar a la siguiente:
<pre>
mkdir: created directory 'src'
mkdir: created directory 'class'
Creando archivos .java

-Parte 1: JFlex
Archivo src/Yylex.java creado correctamente.

--------------
Archivos fuente creados

--------------
Creando archivos .class...

Compilación correcta 
 

--------------

Tareas terminadas

¿Ejecutar el programa? (introducir el número de la respuesta elegida)
1) Sí
2) No
#? 1

--------------
Ejecutando yylex.Yylex
--------------

--> Sirve un café normal a luis.rodriguez@net.uy
--> Sirve un café capuchino a luis.rodriguez@net.uy
--> Sirve un café con leche a luis.rodriguez@net.uy
--> Sirve agua a luis.rodriguez@net.uy
--> Sirve una cuchara a luis.rodriguez@net.uy
--> Sirve un café capuchino a luis.rodriguez@net.uy
Línea 9 --> Error: se esperaba un email y se ha encontrado "C"
Línea 10 --> Error: El modificador "NO" sólo debe aparecer tras un tipo de café.
Línea 11 --> Error: El modificador "NO" sólo debe aparecer una vez.
Línea 12 --> Error: El café ya era de tipo  con leche. No se puede asignar el nuevo tipo CAPUCHINO
Línea 13 --> Error: se esperaba un email y se ha encontrado "O"
Línea 15 --> Error: se esperaba un OK y se ha encontrado "A"
Línea 16 --> Error: El modificador "NO" sólo debe aparecer una vez.
Línea 17 --> Error: El modificador "NO" sólo debe aparecer una vez.
Línea 18 --> Error: El modificador "NO" sólo debe aparecer una vez.
Línea 19 --> Error: El modificador "NO" sólo debe aparecer una vez.
</pre>
## Analizador semántico
Para realizar una prueba con un analizador sintáctico hace falta [el _.jar_ de CUP](http://www2.cs.tum.edu/projects/cup/). En este repositorio se incluye la versión 11a; pero se puede descargar otra versión si se desea (habrá que especificarlo con la opción '-p' o '--classpath').
Un ejemplo de ejecución es el siguiente:
`./script.sh -j Calculadora.lex -c Calculadora.cup -d test/sem/`

Esto permitirá realizar un análisis de una calculadora y generar una salida si la entrada es correcta. La entrada se producirá desde la entrada estándar y terminará cuando se detecte fin de entrada (^D o ^C).

La salida debería ser similar a la siguiente:
<pre>
mkdir: created directory 'src'
mkdir: created directory 'class'
Creando archivos .java

-Parte 1: JFlex
Archivo src/Scan.java creado correctamente.

-Parte 2: CUP
Archivos src/Parser.java y src/Sym.java creados correctamente.

--------------
Archivos fuente creados

--------------
Creando archivos .class...

Compilación correcta 
 

--------------

Tareas terminadas

¿Ejecutar el programa? (introducir el número de la respuesta elegida)
1) Sí
2) No
#? 1

--------------
Ejecutando calculadora.Parser
--------------

1+3=
4
3+-1=
2
</pre>
