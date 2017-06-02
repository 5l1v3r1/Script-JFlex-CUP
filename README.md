# Script-JFlex-CUP
Script para generar analizadores léxicos o sintácticos a partir de unos ficheros de especificación con JFlex y CUP, además de ejecutarlos si se desea.

# Requerimientos
Para poder funcionar correctamente necesita tener instalados JFlex, CUP y el jdk (para usar javac y java).
Si se quiere ejecutar el analizador semántico también hace falta descargarse [un _.jar_ de CUP](http://www2.cs.tum.edu/projects/cup/). En este repositorio se incluye la versión 11a; pero se puede descargar otra versión si se desea (habrá que especificarlo con la opción '-p' o '--classpath').

# Uso
Para ver la ayuda se puede usar la opción -h o --help. La salida debería ser similar a la siguiente:
<pre>
Script para la compilación y ejecución de un analizador con JFlex y CUP.
Miguel García Martín (Foo-Manroot) <miguel.garciamartin@hotmail.com> - 2016
v. 1.1

Llamada correcta:
./script.sh [-opciones | --opciones] [-a | --args][argumentos]

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
		(Yylex.lex, por defecto).

	-l
	--limpiar
	--clean
		 Elimina todos los archivos generados por este script (las carpetas
		class y src).

	-m
	--main
		 Especifica el nombre de la clase principal del analizador sintáctico.
		(Parser, por defecto).

	-p
	--classpath
		 Establece el classpath para que java pueda ejecutar los archivos de
		salida. Por defecto es "java-cup-11a.jar:class" (necesario para CUP). Si se quieren poner
		varias rutas, deben separarse con dos puntos -> "path1:path2:path3".

	-s
	--sym
		 Establece el nombre del archivo con la tabla de símbolos para CUP
		(Sym, por defecto).

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
## Analizador sintáctico
Para realizar una prueba con un analizador sintáctico hace falta [el _.jar_ de CUP](http://www2.cs.tum.edu/projects/cup/). En este repositorio se incluye la versión 11a; pero se puede descargar otra versión si se desea (habrá que especificarlo con la opción '-p' o '--classpath').
Un ejemplo de ejecución es el siguiente:
`./script.sh -j Calculadora.lex -c Calculadora.cup -d test/sint/`

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

## Analizador semántico
Por último, en la carpeta test/sem/ se proporciona un ejemplo de un analizador semántico para un lenguaje inventado, realizado como una práctica para la asignatura de Procesadores del Lenguaje. Por si se quiere mmás información sobre el proyecto, se adjunta la memoria de la práctica.

Se dispone de un archivo README explicando el contenido de las subcarpetas.

Si se quiere ver las reglas aplicadas al analizar, por ejemplo, el archivo '01.-Prueba_Lex.prog' se puede usar el _script_ de la siguiente manera
`./script.sh -d test/sem/ -c PECL1.cup -j PECL1.lex -a pruebas/01.-Prueba_Lex.prog:-v`

Lo que generará una salida como la siguiente:
<pre>
Creando archivos .java

-Parte 1: JFlex
Archivo 'src/Scanner.java' creado correctamente.

-Parte 2: CUP
Archivos src/Parser.java y src/Sym.java  creados correctamente.

--------------
Archivos fuente creados
Copiando los archivos .java auxiliares...
'./Código/árbol/Nodo.java' -> 'src/./Código/árbol/Nodo.java'
'./Código/árbol/Árbol.java' -> 'src/./Código/árbol/Árbol.java'
'./Código/árbol/nodos/declaraciones/NodoDecl.java' -> 'src/./Código/árbol/nodos/declaraciones/NodoDecl.java'
'./Código/árbol/nodos/declaraciones/NodoVar.java' -> 'src/./Código/árbol/nodos/declaraciones/NodoVar.java'
'./Código/árbol/nodos/declaraciones/NodoTipo.java' -> 'src/./Código/árbol/nodos/declaraciones/NodoTipo.java'
'./Código/árbol/nodos/sentencias/NodoE_S.java' -> 'src/./Código/árbol/nodos/sentencias/NodoE_S.java'
'./Código/árbol/nodos/sentencias/NodoIf.java' -> 'src/./Código/árbol/nodos/sentencias/NodoIf.java'
'./Código/árbol/nodos/sentencias/NodoOp.java' -> 'src/./Código/árbol/nodos/sentencias/NodoOp.java'
'./Código/árbol/nodos/sentencias/NodoAsigna.java' -> 'src/./Código/árbol/nodos/sentencias/NodoAsigna.java'
'./Código/árbol/nodos/sentencias/NodoBucle.java' -> 'src/./Código/árbol/nodos/sentencias/NodoBucle.java'
'./Código/árbol/nodos/sentencias/NodoSents.java' -> 'src/./Código/árbol/nodos/sentencias/NodoSents.java'
'./Código/árbol/nodos/NodoProg.java' -> 'src/./Código/árbol/nodos/NodoProg.java'
'./Código/common/Logger.java' -> 'src/./Código/common/Logger.java'
'./Código/tabla/Info.java' -> 'src/./Código/tabla/Info.java'
'./Código/tabla/TipoDato.java' -> 'src/./Código/tabla/TipoDato.java'
'./Código/tabla/TablaSímbolos.java' -> 'src/./Código/tabla/TablaSímbolos.java'

--------------
Creando archivos .class...

Compilación correcta 
 

--------------

Tareas terminadas

¿Ejecutar el programa? (introducir el número de la  respuesta elegida)
1) Sí
2) No
#? 1

--------------
Ejecutando pecl1.Parser
--------------



-------------------------------------------
Comenzando análisis del archivo pruebas/01.-Prueba_Lex.prog
-------------------------------------------

Error léxico --> línea 4, col. 0 en el símbolo 'ç'
	Lexema: program
	Lexema: léxico
	Lexema: is
	Lexema: var
Error léxico --> línea 6, col. 5 en el símbolo '_'
	Lexema: bool
	Lexema: :
	Lexema: boolean
	Lexema: ;
		Aplicando regla  3a => VARIABLE ::= ident dos_puntos reservada
	Lexema: var
	Lexema: b
Error léxico --> línea 7, col. 6 en el símbolo 'ö'
	Lexema: ol
Error de sintaxis --> línea 7, col. 7: 
	Lexema: :
	Lexema: boolean
	Lexema: ;
	Lexema: var
	Lexema: entero
	Lexema: ,
                  \-> Se esperaba una coma (',') tras 'b'
		Aplicando regla  3c => VARIABLE ::= ident ERROR
	Lexema: variable
	Lexema: :
	Lexema: integer
	Lexema: ;
		Aplicando regla  3a => VARIABLE ::= ident dos_puntos reservada
		Aplicando regla  3b => VARIABLE ::= ident coma VARIABLE
	Lexema: begin
		Aplicando regla  2b => DECLARACIONES ::= epsilon (cadena vacía)
		Aplicando regla  2a => DECLARACIONES ::= reservada VARIABLE punto_coma DECLARACIONES
		Aplicando regla  2a => DECLARACIONES ::= reservada VARIABLE punto_coma DECLARACIONES
		Aplicando regla  2a => DECLARACIONES ::= reservada VARIABLE punto_coma DECLARACIONES
	Lexema: read
	Lexema: variable
	Lexema: ;
		Aplicando regla 14a => VALOR ::= ident
		Aplicando regla  7a => ENTRADA_SALIDA ::= ent_sal VALOR
		Aplicando regla  5a => SENTENCIA ::= ENTRADA_SALIDA
	Lexema: entero
	Lexema: :=
	Lexema: (
	Lexema: variable
	Lexema: >
	Lexema: 3
		Aplicando regla 20d => SIM_COMP ::= mayor
	Lexema: )
		Aplicando regla  8a => VALOR_ENTERO ::= entero
		Aplicando regla 14c => VALOR ::= VALOR_ENTERO
		Aplicando regla 19d => COMPARACIÓN ::= ident SIM_COMP VALOR
		Aplicando regla 15c => OPERACIÓN_LÓGICA ::= COMPARACIÓN
Error léxico --> línea 12, col. 25 en el símbolo '?'
	Lexema: variable
Error de sintaxis --> línea 12, col. 27: 
	Lexema: :
	Lexema: 3
	Lexema: ;
	Lexema: if
	Lexema: (
	Lexema: entero
                  \-> Operación mal formada
		Aplicando regla  9l => OPERACIÓN_ENTERA ::= parent_ab ERROR
		Aplicando regla  8f => VALOR_ENTERO ::= OPERACIÓN_ENTERA
		Aplicando regla 14c => VALOR ::= VALOR_ENTERO
		Aplicando regla 13a => ASIGNACIÓN ::= ident asigna VALOR
		Aplicando regla  5b => SENTENCIA ::= ASIGNACIÓN
	Lexema: >
	Lexema: 3
		Aplicando regla 20d => SIM_COMP ::= mayor
	Lexema: )
		Aplicando regla  8a => VALOR_ENTERO ::= entero
		Aplicando regla 14c => VALOR ::= VALOR_ENTERO
		Aplicando regla 19d => COMPARACIÓN ::= ident SIM_COMP VALOR
		Aplicando regla 15c => OPERACIÓN_LÓGICA ::= COMPARACIÓN
	Lexema: then
		Aplicando regla 17e => VALOR_LÓGICO ::= parent_ab OPERACIÓN_LÓGICA parent_cerr
		Aplicando regla 22a => CONDICIÓN ::= VALOR_LÓGICO
Error léxico --> línea 16, col. 6 en el símbolo '{'
	Lexema: variable
	Lexema: :=
	Lexema: entero
Error léxico --> línea 19, col. 21 en el símbolo '^'
	Lexema: 2
Error de sintaxis --> línea 19, col. 23: 
	Lexema: ;
Error léxico --> línea 21, col. 1 en el símbolo '}'
	Lexema: end
	Lexema: if
	Lexema: ;
Error léxico --> línea 24, col. 1 en el símbolo '['
Error léxico --> línea 24, col. 2 en el símbolo '!'
Error léxico --> línea 24, col. 3 en el símbolo '"'
Error léxico --> línea 24, col. 4 en el símbolo '·'
Error léxico --> línea 24, col. 5 en el símbolo '#'
Error léxico --> línea 24, col. 6 en el símbolo '@'
Error léxico --> línea 24, col. 7 en el símbolo '%'
	Lexema: write
                  \-> Expresión de asignación mal formada
		Aplicando regla 13b => ASIGNACIÓN ::= ident asigna ERROR
		Aplicando regla  5b => SENTENCIA ::= ASIGNACIÓN
		Aplicando regla  4b => BLOQUE_SENTENCIAS ::= SENTENCIA
		Aplicando regla 23a => SENT_CONDICIONAL ::= if CONDICIÓN then BLOQUE_SENTENCIAS res_end id
		Aplicando regla  5d => SENTENCIA ::= SENT_CONDICIONAL
	Lexema: entero
	Lexema: end
		Aplicando regla 14a => VALOR ::= ident
		Aplicando regla  7a => ENTRADA_SALIDA ::= ent_sal VALOR
		Aplicando regla  5a => SENTENCIA ::= ENTRADA_SALIDA
		Aplicando regla  4b => BLOQUE_SENTENCIAS ::= SENTENCIA
		Aplicando regla  4a => BLOQUE_SENTENCIAS ::= SENTENCIA punto_coma BLOQUE_SENTENCIAS
		Aplicando regla  4a => BLOQUE_SENTENCIAS ::= SENTENCIA punto_coma BLOQUE_SENTENCIAS
		Aplicando regla  4a => BLOQUE_SENTENCIAS ::= SENTENCIA punto_coma BLOQUE_SENTENCIAS
		Aplicando regla  1a => PROGRAMA ::= res_prog ident res_is DECLARACIONES res_beg BLOQUE_SENTENCIAS res_end
		Aplicando regla   0 => INICIO ::= PROGRAMA
Aviso --> La variable 'bool' no se utiliza en el programa
Aviso --> La variable 'variable' no se utiliza en el programa

------------------
Análisis terminado
------------------

Análisis léxico completado. 
Lista de símbolos:
	reserv ('program')   - ident ('léxico')     - reserv ('is')        - reserv ('var')       - ident ('bool')       - puntuación (':') 
	reserv ('boolean')   - puntuación (';')     - reserv ('var')       - ident ('b')          - ident ('ol')         - puntuación (':') 
	reserv ('boolean')   - puntuación (';')     - reserv ('var')       - ident ('entero')     - puntuación (',')     - ident ('variable')
	puntuación (':')     - reserv ('integer')   - puntuación (';')     - reserv ('begin')     - reserv ('read')      - ident ('variable')
	puntuación (';')     - ident ('entero')     - asigna (':=')        - puntuación ('(')     - ident ('variable')   - relac ('>')
	entero ('3')         - puntuación (')')     - ident ('variable')   - puntuación (':')     - entero ('3')         - puntuación (';') 
	reserv ('if')        - puntuación ('(')     - ident ('entero')     - relac ('>')          - entero ('3')         - puntuación (')') 
	reserv ('then')      - ident ('variable')   - asigna (':=')        - ident ('entero')     - entero ('2')         - puntuación (';') 
	reserv ('end')       - reserv ('if')        - puntuación (';')     - reserv ('write')     - ident ('entero')     - reserv ('end')
	No hay más símbolos.


Árbol sintáctico: 
└── léxico
    ├── Declaraciones
    │   ├── integer
    │   │   ├── variable
    │   │   └── entero
    │   └── boolean
    │       └── bool
    └── Sentencias
        ├── read
        │   └── variable
        ├── Asignación
        │   ├── entero
        │   └── Operación entero: null
        ├── Sentencia condicional
        │   ├── Operación booleano: >
        │   │   ├── entero
        │   │   └── Entero: 3
        │   └── Rama 'if'
        └── write
            └── entero



---------------------
Tabla de símbolos: 
---------------------
Símbolos: 
	Símbolo:      reserv ('read') -> líneas  10 
	Símbolo:     puntuación ('(') -> líneas  12  15 
	Símbolo:     puntuación (')') -> líneas  12  15 
	Símbolo:       reserv ('var') -> líneas   6   7   8 
	Símbolo:     puntuación (',') -> líneas   8 
	Símbolo:        reserv ('is') -> líneas   4 
	Símbolo:       reserv ('end') -> líneas  22  35 
	Símbolo:   reserv ('program') -> líneas   4 
	Símbolo:     reserv ('begin') -> líneas   9 
	Símbolo:          relac ('>') -> líneas  12  15 
	Símbolo:         entero ('3') -> líneas  12  12  15 
	Símbolo:        asigna (':=') -> líneas  12  19 
	Símbolo:      reserv ('then') -> líneas  16 
	Símbolo:         entero ('2') -> líneas  19 
	Símbolo:     reserv ('write') -> líneas  34 
	Símbolo:     puntuación (':') -> líneas   6   7   8  12 
	Símbolo:     puntuación (';') -> líneas   6   7   8  10  12  19  22 
	Símbolo:   reserv ('boolean') -> líneas   6   7 
	Símbolo:   reserv ('integer') -> líneas   8 
	Símbolo:        reserv ('if') -> líneas  15  22 

Variables: 
	Identificador: bool
		Tipo: boolean
		Valor: indeterminado
		Sin inicializar
		Sin usar

	Identificador: variable
		Tipo: integer
		Valor: indeterminado
		Sin usar

	Identificador: entero
		Tipo: integer
		Valor: indeterminado

	Identificador: léxico
		Tipo: pseudo


-------------
Estadísticas:
-------------
-Errores léxicos: 14
-Errores sintácticos: 3
-Errores semánticos: 0
-Avisos: 2

	--------------------------------
		**** FIN ****
	Análisis finalizado con 17 errores.
	--------------------------------
</pre>
