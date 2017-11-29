JFLAGS = -g
JC = javac
JVM= java

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Parser.java \
	Driver.java 
	
MAIN = Driver

default: run

classes: $(CLASSES:.java=.class)

run : classes 
	$(JVM) $(MAIN)

clean:
	$(RM) *.class