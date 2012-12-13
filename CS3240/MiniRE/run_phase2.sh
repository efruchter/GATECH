#!/bin/bash

BASEPATH=$(dirname "$0")
CLASSPATH="$BASEPATH/src/"

MAINCLASS="project.phase2.Interpreter"

java -cp $CLASSPATH $MAINCLASS $@
