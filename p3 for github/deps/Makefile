###
# This Makefile can be used to make a parser for the Cdull language
# (parser.class) and to make a program (P3.class) that tests the parser and
# the unparse methods in ast.java.
#
# make clean removes all generated files.
#
###

JC = javac
FLAGS = -g  
CP = ./deps:.

P3.class: P3.java parser.class Yylex.class ASTnode.class
	$(JC) $(FLAGS) -cp $(CP) P3.java

parser.class: parser.java ASTnode.class Yylex.class ErrMsg.class
	$(JC) $(FLAGS) -cp $(CP) parser.java

parser.java: cdull.cup
	java -cp $(CP) java_cup.Main < cdull.cup

Yylex.class: cdull.jlex.java sym.class ErrMsg.class
	$(JC) $(FLAGS) -cp $(CP) cdull.jlex.java

ASTnode.class: ast.java
	$(JC) $(FLAGS) -cp $(CP) ast.java

cdull.jlex.java: cdull.jlex sym.class
	java -cp $(CP) JLex.Main cdull.jlex

sym.class: sym.java
	$(JC) $(FLAGS) -cp $(CP) sym.java

sym.java: cdull.cup
	java -cp $(CP) java_cup.Main < cdull.cup

ErrMsg.class: ErrMsg.java
	$(JC) $(FLAGS) -cp $(CP) ErrMsg.java

##test
test:
	java -cp $(CP) P3 test.cdull test.out

###
# clean
###
clean:
	rm -f *~ *.class parser.java cdull.jlex.java sym.java

## cleantest (delete test artifacts)
cleantest:
	rm -f *.out
