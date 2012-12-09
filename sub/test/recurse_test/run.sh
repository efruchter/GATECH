#!/bin/bash

BASEPATH=$(dirname "$0")
PHASE2="$BASEPATH/../../run_phase2.sh"

$PHASE2 "$BASEPATH"/script.txt
