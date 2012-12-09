JAVAC=javac
SRCDIR=src
MAINCLASS=project/scangen/ScannerGenerator.java

.PHONY: all
all: clean build

.PHONY: build
build:
	$(SRCDIR)/build.sh

.PHONY: clean
clean:
	find $(SRCDIR) -type f -name "*.class" | xargs rm -f
