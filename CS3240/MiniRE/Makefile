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

.PHONY: turnin
turnin: 3240Project.tgz

3240Project.tgz: .git
	git archive --prefix=3240Project/ master | gzip > $@
