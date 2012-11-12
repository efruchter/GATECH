#!/bin/bash

SRCDIR="src/"
MAINCLASS="project.scannergenerator.ScannerGenerator"

java -cp $SRCDIR $MAINCLASS $@
