

package calculadora; 

import java_cup.runtime.*; 

import java.io.IOException; 

%% 

%class Scan

%unicode 

%line 

%column 

%cup 

%init{ 

   yyline = 1; 

   yycolumn = 1; 

%init} 

%eofval{ 

   return new Symbol(Sym.EOF); 

%eofval} 

%% 

[\r\n\t ]+   {/*prescindir de blancos, tabuladores, nueva linea, etc */} 

"+"          {return new Symbol(Sym.SUMA, yyline, yycolumn, yytext());} 

"-"          {return new Symbol(Sym.RESTA, yyline, yycolumn, yytext()); } 

"*"          {return new Symbol(Sym.MULTIPLICACION, yyline, yycolumn, yytext()); } 

"/"          {return new Symbol(Sym.DIVISION, yyline, yycolumn, yytext()); } 

"("          {return new Symbol(Sym.LPARENT, yyline, yycolumn, yytext()); } 

")"          {return new Symbol(Sym.RPARENT, yyline, yycolumn, yytext()); } 

"="          {return new Symbol(Sym.RESULTADO, yyline, yycolumn, yytext()); } 

[0-9]+       {return new Symbol(Sym.ENTERO, yyline, yycolumn,new Integer (yytext())); } 

[^0-9\r\n\t \+\-\*"^"/]+ {System.err.println("Error lexico: linea "+yyline +" ; valor del token : "+ yytext() ); }

 .           {System.err.println("Error lexico");} 


