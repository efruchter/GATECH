#!/bin/bash

SRCDIR="src/"
MAINCLASS="scannergenerator.ScannerGenerator"

java -cp $SRCDIR $MAINCLASS $@
