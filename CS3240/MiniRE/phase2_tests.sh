#!/bin/bash

BASEPATH=$(dirname "$0")
TESTSPATH="$BASEPATH/test"

for testdir in $(find "$TESTSPATH" -type d -depth 1); do
    echo $testdir
    "$testdir/run.sh"
done
