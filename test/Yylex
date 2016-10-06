package yylex;

import java.io.*;

%%

%unicode

%{

String mensaje="";
int línea = 1;
String tipoCafé = null;
int contadorNO = 0;

public static void main (String argv[])
throws java.io.IOException {

	FileInputStream fis=new FileInputStream("ficheroPrueba.txt");
	Yylex yy = new Yylex(fis);

	while (yy.yylex() != -1);
   } 
%}

email=[a-zA-Z]+(\.[a-zA-Z0-9]+)*@[a-zA-Z0-9]+(\.[a-zA-Z0-9]+)+

%state estadoAgua, estadoCafé, estadoCuchara, estadoError, estadoOK

%integer
%%

[ \t]	{

	/* Nada */
}

<YYINITIAL> "AGUA" {

	mensaje = "--> Sirve agua";
	yybegin (estadoAgua);
}
<YYINITIAL> "CAFE" {

	tipoCafé = null;
	mensaje = "--> Sirve un café";
	yybegin (estadoCafé);
}
<YYINITIAL> "CUCHARA" {

	mensaje = "--> Sirve una cuchara";
	yybegin (estadoCuchara);
}




<estadoAgua> {email} {

	mensaje = mensaje + " a " + yytext();
	yybegin(estadoOK);
}
<estadoAgua> \r\n {

	System.out.println ("Línea " + línea + "--> Error: se esperaba un email y se ha"
				+ " encontrado un cambio de línea");
	yybegin(YYINITIAL);
}
<estadoAgua> . {

	System.out.println ("Línea " + línea + " --> Error: se esperaba un email y se ha"
				+ " encontrado \"" + yytext () + "\"");
	yybegin(estadoError);
}




<estadoCafé> {email} {

	mensaje = mensaje + ((tipoCafé == null)? " normal" : tipoCafé) + " a " + yytext ();

	tipoCafé = null;
	contadorNO = 0;

	yybegin (estadoOK);
}
<estadoCafé> "NO" {

	/* Corrige el tipo de café (si no se había indicado,
	se pasa a estado de error) */
	if (tipoCafé == null) {

		System.out.println ("Línea " + línea + " --> Error: El modificador \"NO\" sólo"
					+ " debe aparecer tras un tipo de café.");
		yybegin (estadoError);
	} else if (tipoCafé.equals (" normal") || contadorNO >= 1) {

		/* Comprueba si ya se había escrito "NO" */
		System.out.println ("Línea " + línea + " --> Error: El modificador \"NO\" sólo"
					+ " debe aparecer una vez.");
		yybegin (estadoError);
	} else {

		tipoCafé = " normal";
		contadorNO++;
		yybegin (estadoCafé);
	}
}
<estadoCafé> "CAPUCHINO" {

	/* Si el café era de otro tipo, devuelve un error */
	if (tipoCafé != null &&
	    !tipoCafé.isEmpty() &&
	    !tipoCafé.equals (" normal")) {

		System.out.println ("Línea " + línea + " --> Error: El café ya era de tipo " + tipoCafé
				+ ". No se puede asignar el nuevo tipo " + yytext ());
		yybegin (estadoError);
	} else {

		tipoCafé = " capuchino";
		yybegin (estadoCafé);
	}
}
<estadoCafé> "LECHE" {

	/* Si el café era de otro tipo, devuelve un error */
	if (tipoCafé != null &&
	    !tipoCafé.isEmpty() &&
	    !tipoCafé.equals (" normal")) {

		System.out.println ("Línea " + línea + " --> Error: El café ya era de tipo " + tipoCafé
				+ ". No se puede asignar el nuevo tipo " + yytext ());
		yybegin (estadoError);
	} else {

		tipoCafé = " con leche";
		yybegin (estadoCafé);
	}
}
<estadoCafé> . {

	System.out.println ("Línea " + línea + " --> Error: se esperaba un email y se ha"
				+ " encontrado \"" + yytext () + "\"");
	yybegin (estadoError);
}



<estadoCuchara> {email} {

	mensaje = mensaje + " a " + yytext ();
	yybegin (estadoOK);
}
<estadoCuchara> . {

	System.out.println ("Línea " + línea + " --> Error: se esperaba un email y se ha"
				+ " encontrado  \"" + yytext () + "\"");
	yybegin (estadoError);
}



<estadoOK> "OK" {

	System.out.println (mensaje);
	yybegin(estadoError);
}
<estadoOK> \r\n {

	línea ++;
	yybegin(YYINITIAL);
}
<estadoOK> . {

	System.out.println ("Línea " + línea + " --> Error: se esperaba un OK y se ha encontrado \"" + yytext () + "\"");
	yybegin(estadoError);
}




<estadoError> \n {

	línea ++;
	yybegin(YYINITIAL);
}
<estadoError> . {

	/*Mientras estemos en error nos saltamos todo lo que leamos*/
}




[\n] {

	línea++;
}

. {

	/* Nada */
}
