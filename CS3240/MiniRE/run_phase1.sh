#!/bin/bash

SRCDIR="src/"
MAINCLASS="project.scangen.ScannerGenerator"

java -cp $SRCDIR $MAINCLASS $@
