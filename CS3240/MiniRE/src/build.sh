#!/bin/bash

set -v

BASEPATH=$(dirname "$0")
CLASSPATH="$BASEPATH/../lib/junit-4.10.jar"

find "$BASEPATH" -name "*.java" | xargs javac -cp "$CLASSPATH"
