JFLAGS = -g
JC = javac


.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Parser.java \
	Driver.java 
	
MAIN = Driver

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class