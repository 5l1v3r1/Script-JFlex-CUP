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
	-f
        --file
                 Especifica el fichero que se ha de copiar a la carpeta class/ para que el
                analizador pueda trabajar con él. Por defecto es ficheroPrueba.txt. 
	-h
	--help
		  Muestra esta ayuda y termina la ejecución
	-n
	--nombre
		 Especifica el nombre del archivo jlex a compilar
</pre>

Se puede probar el script con los ficheros proporcionados en la carpeta test ejecutando
	./script.sh -d test/

La salida debería ser similar a la siguiente:
<pre>

mkdir: created directory 'src'
mkdir: created directory 'src/yylex'
mkdir: created directory 'class'
mkdir: created directory 'class/yylex'
Creando archivo .java
Archivo .java creado correctamente. 
 Reading "Yylex"
Constructing NFA : 156 states in NFA
Converting NFA to DFA : 
..........................................................................
86 states before minimization, 73 states in minimized DFA
Writing code to "Yylex.java"

--------------
Archivo creado
'Yylex.java' -> 'src/yylex/Yylex.java'

--------------
Creando el archivo .class...

Compilación correcta 
 
'ficheroPrueba.txt' -> 'class/ficheroPrueba.txt'

--------------

Tareas terminadas

¿Ejecutar el programa? (introducir el número de la respuesta elegida)
1) Sí
2) No
#? 1

--------------
Ejecutando el archivo Yylex.class
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
