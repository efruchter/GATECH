JAVAC=javac
SRCDIR=src
MAINCLASS=project/scangen/ScannerGenerator.java

.PHONY: all
all:
	javac -cp $(SRCDIR) $(SRCDIR)/$(MAINCLASS)

.PHONY: clean
clean:
	find $(SRCDIR) -type f -name "*.class" | xargs rm -f
