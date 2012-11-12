JAVAC=javac
SRCDIR=src
MAINCLASS=scannergenerator.ScannerGenerator

all:
	javac -cp $(SRCDIR) $(SRCDIR)$(MAINCLASS).java

clean:
	find $(SRCDIR) -type f -name "*.class" | xargs rm -f
