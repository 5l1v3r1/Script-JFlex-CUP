package pecl1;

import java_cup.runtime.*;
import java.util.ArrayList;

import common.Logger;

%%
%class Scanner
%unicode

%line
%column

%cup

/* ---------------- */
/* Inicializaciones */
/* ---------------- */
%{
	ArrayList <String> tokens = new ArrayList<>();
	Parser parser;

	public void imprimirTokens () {

		int contador = 0;
		System.out.print ("\nAnálisis léxico completado. \nLista de símbolos:\n\t");

		for (String s : tokens) {

			if (contador < 5) {

				System.out.printf ("%-20s - ", s);
				contador ++;
			} else {

				System.out.print (s + "\n\t");
				contador = 0;
			}
		}

		System.out.println ("No hay más símbolos.\n");
	}

	public void setParser (Parser parser) {

		this.parser = parser;
	}
%}

%init{
	yyline = 1;
	yycolumn = 1;
%init}

%eofval{

	return new Symbol (Sym.EOF);

%eofval}

/* ----------------------------------- */
/* Definición de expresiones regulares */
/* ----------------------------------- */
palabra_res = program | is | begin | end | var | integer | boolean | read |
	      write | skip | while | do | if | then | else | and | or| true|
	      false | not

asignación = ":="

relación = <= | < | = | > | >= | <>

op_matem = "+" | "-" | "*" | "/"

puntuación = "(" | ")" | , | ; | :

entero = [0-9]+ /* El signo se tiene en cuenta en el análisis sintáctico */

identificador = [A-Za-zÁÉÍÓÚáéúíóÑñ][A-Za-zÁÉÍÓÚáéíóúÑñ0-9]*

comentario = "/*"~"*/" | \/\/.*\n

/* --------------------- */
/* Comienzo del análisis */
/* --------------------- */
%%
{comentario} { /* No hace nada */ }

[ \t\r\n] { /* Omite espacios, tabulaciones y saltos de línea */ }

{palabra_res} {

	tokens.add ("reserv ('" + yytext () + "')");
	parser.tabla_sim.añadirSímbolo ("reserv ('" + yytext () + "')", yyline);

	Logger.lexema (yytext ());

	/*
	Devuelve un token especial si es una de las palabras reservadas siguientes: 
		"program"
		"is"
		"begin"
		"end"

		"if"
		"then"
		"else"

		"not"
		"and"
		"or"

		"true"
		"false"

		"read"
		"write"

		"while"

		"do"
	*/
	switch (yytext ()) {

		case "program":
			return new Symbol (Sym.RES_PROG, yyline, yycolumn, yytext ());

		case "is":
			return new Symbol (Sym.RES_IS, yyline, yycolumn, yytext ());

		case "begin":
			return new Symbol (Sym.RES_BEG, yyline, yycolumn, yytext ());

		case "end":
			return new Symbol (Sym.RES_END, yyline, yycolumn, yytext ());


		case "if":
			return new Symbol (Sym.IF, yyline, yycolumn, yytext ());

		case "then":
			return new Symbol (Sym.THEN, yyline, yycolumn, yytext ());

		case "else":
			return new Symbol (Sym.ELSE, yyline, yycolumn, yytext ());

		case "true":
		case "false":
			return new Symbol (Sym.BOOL, yyline, yycolumn, yytext ());


		case "read":
		case "write":
			return new Symbol (Sym.ENT_SAL, yyline, yycolumn, yytext ());


		case "while":
			return new Symbol (Sym.BUCLE, yyline, yycolumn, yytext ());


		case "not":
			return new Symbol (Sym.NOT, yyline, yycolumn, yytext ());

		case "and":
			return new Symbol (Sym.AND, yyline, yycolumn, yytext ());

		case "or":
			return new Symbol (Sym.OR, yyline, yycolumn, yytext ());


		case "do":
			return new Symbol (Sym.DO, yyline, yycolumn, yytext ());


		default:
			return new Symbol (Sym.RESERVADA, yyline, yycolumn, yytext ());
	}
}

{asignación} { 

	tokens.add ("asigna ('" + yytext () + "')");
	parser.tabla_sim.añadirSímbolo ("asigna ('" + yytext () + "')", yyline);

	Logger.lexema (yytext ());

	return new Symbol (Sym.ASIGNA, yyline, yycolumn, yytext ());
}

{relación} {

	tokens.add ("relac ('" + yytext () + "')");
	parser.tabla_sim.añadirSímbolo ("relac ('" + yytext () + "')", yyline);

	Logger.lexema (yytext ());

	/* Añade un token diferente en función del operador de relación */
	switch (yytext()) {

		case "<=":
			return new Symbol (Sym.MENOR_IGUAL, yyline, yycolumn, yytext ());

		case "<":
			return new Symbol (Sym.MENOR, yyline, yycolumn, yytext ());

		case "=":
			return new Symbol (Sym.IGUAL, yyline, yycolumn, yytext ());

		case ">":
			return new Symbol (Sym.MAYOR, yyline, yycolumn, yytext ());

		case ">=":
			return new Symbol (Sym.MAYOR_IGUAL, yyline, yycolumn, yytext ());

		case "<>":
			return new Symbol (Sym.NO_IGUAL, yyline, yycolumn, yytext ());

		default:
			System.err.println ("Error léxico --> " + yyline + " : " + yycolumn
				+ ". Operador de relación no reconocdido: " + yytext ());
	}
}

{op_matem} {

	tokens.add ("operador ('" + yytext () + "')");
	parser.tabla_sim.añadirSímbolo ("operador ('" + yytext () + "')", yyline);

	Logger.lexema (yytext ());

	/* Añade un token diferente en función del operador matemático */
	switch (yytext ()) {

		case "+":
			return new Symbol (Sym.SUMA, yyline, yycolumn, yytext ());

		case "-":
			return new Symbol (Sym.RESTA, yyline, yycolumn, yytext ());

		case "*":
			return new Symbol (Sym.MULTI, yyline, yycolumn, yytext ());

		case "/":
			return new Symbol (Sym.DIVI, yyline, yycolumn, yytext ());

		default:
			System.err.println ("Error léxico --> " + yyline + " : " + yycolumn
				+ ". Operador matemático no reconocdido: " + yytext ());
	}
}

{puntuación} {

	tokens.add ("puntuación ('" + yytext () + "') ");
	Logger.lexema (yytext ());
	parser.tabla_sim.añadirSímbolo ("puntuación ('" + yytext () + "')", yyline);

	/* Añade un token diferente en función del signo de puntuación */
	switch (yytext ()) {

		case "(":
			return new Symbol (Sym.PARENT_AB, yyline, yycolumn, yytext ());

		case ")":
			return new Symbol (Sym.PARENT_CERR, yyline, yycolumn, yytext ());

		case ",":
			return new Symbol (Sym.COMA, yyline, yycolumn, yytext ());

		case ";":
			return new Symbol (Sym.PUNTO_COMA, yyline, yycolumn, yytext ());

		case ":":
			return new Symbol (Sym.DOS_PUNTOS, yyline, yycolumn, yytext ());

		default:
			System.err.println ("Error léxico --> " + yyline + " : " + yycolumn
				+ ". Signo de puntuación no reconocdido: " + yytext ());
	}
}

{entero} {

	tokens.add ("entero ('" + yytext () + "')");
	parser.tabla_sim.añadirSímbolo ("entero ('" + yytext () + "')", yyline);

	Logger.lexema (yytext ());

	return new Symbol (Sym.ENTERO, yyline, yycolumn, yytext ());
}

{identificador} {

	tokens.add ("ident ('" + yytext ().toLowerCase () + "')");
	Logger.lexema (yytext ().toLowerCase ());

	return new Symbol (Sym.IDENT, yyline, yycolumn, yytext ().toLowerCase ());
}

. {
	/* Cualquier otro carácter -> error (lo muestra por pantalla y sigue) */
	Logger.error_léxico (new Symbol (Sym.error, yyline, yycolumn, yytext ()));
}
